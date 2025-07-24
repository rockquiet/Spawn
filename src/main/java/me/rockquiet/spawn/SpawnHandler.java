package me.rockquiet.spawn;

import me.rockquiet.spawn.configuration.ConfigFile;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpawnHandler {

    // Legacy keys for backward compatibility
    private static final String WORLD_KEY = "spawn.world";
    private static final String X_KEY = "spawn.x";
    private static final String Y_KEY = "spawn.y";
    private static final String Z_KEY = "spawn.z";
    private static final String YAW_KEY = "spawn.yaw";
    private static final String PITCH_KEY = "spawn.pitch";
    
    // New world-specific keys
    private static final String WORLD_SPAWNS_KEY = "world-spawns";

    private final Spawn plugin;
    private final FileManager fileManager;
    private final Messages messageManager;

    private Location spawnLocation; // Legacy default spawn
    private Map<String, Location> worldSpawns; // World-specific spawns

    public SpawnHandler(Spawn plugin, FileManager fileManager, Messages messageManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.worldSpawns = new HashMap<>();

        migrateOldConfig();
        loadSpawns();
    }

    /**
     * Gets the spawn location for the given world, or the default spawn if no world-specific spawn exists
     */
    public Location getSpawn(World world) {
        if (world == null) {
            return spawnLocation;
        }
        
        Location worldSpawn = worldSpawns.get(world.getName());
        if (worldSpawn != null && worldSpawn.getWorld() != null) {
            return worldSpawn;
        }
        
        // Fallback to default spawn
        return spawnLocation;
    }

    /**
     * Gets the default spawn location (backward compatibility)
     */
    public Location getSpawn() {
        return spawnLocation;
    }

    /**
     * Gets spawn location for a specific world by name
     */
    public Location getSpawn(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return getSpawn(world);
    }

    /**
     * Sets spawn for a specific world
     */
    public void setSpawn(World world, Location newSpawnLocation, boolean saveToFile) {
        if (world == null) {
            setSpawn(newSpawnLocation, saveToFile);
            return;
        }
        
        worldSpawns.put(world.getName(), newSpawnLocation);
        if (saveToFile) {
            saveWorldSpawn(world.getName(), newSpawnLocation);
        }
    }

    /**
     * Sets the default spawn location (backward compatibility)
     */
    public void setSpawn(Location newSpawnLocation, boolean saveToFile) {
        spawnLocation = newSpawnLocation;
        if (saveToFile) {
            saveSpawn(newSpawnLocation);
        }
    }

    /**
     * Checks if a spawn exists for the given world
     */
    public boolean spawnExists(World world) {
        if (world == null) {
            return spawnExists();
        }
        
        Location worldSpawn = worldSpawns.get(world.getName());
        if (worldSpawn != null && worldSpawn.getWorld() != null) {
            return true;
        }
        
        // Fallback to default spawn
        return spawnExists();
    }

    /**
     * Checks if any spawn exists (backward compatibility)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean spawnExists() {
        return spawnLocation != null || isLocationConfigValid();
    }

    /**
     * Teleport player to spawn (uses world-specific spawn if available)
     */
    public void teleportPlayer(Player player) {
        teleportPlayer(player, player.getWorld());
    }

    /**
     * Teleport player to spawn in a specific world
     */
    public void teleportPlayer(Player player, World targetWorld) {
        Location targetSpawn = getSpawn(targetWorld);
        
        if (targetSpawn == null) {
            messageManager.sendMessage(player, "no-spawn");
            return;
        }

        final YamlConfiguration config = fileManager.getYamlConfig();

        if (!config.getBoolean("fall-damage.enabled")) {
            player.setFallDistance(0F);
        }

        if (config.getBoolean("use-player-head-rotation.enabled")) {
            Location location = targetSpawn.clone();
            location.setDirection(player.getLocation().getDirection());
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            player.teleport(targetSpawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        spawnParticles(player, targetSpawn);
        playSound(player, targetSpawn);

        // Send appropriate message based on whether it's a world-specific spawn
        if (targetWorld != null && worldSpawns.containsKey(targetWorld.getName())) {
            messageManager.sendMessage(player, "teleport-world", "%world%", targetWorld.getName());
        } else {
            messageManager.sendMessage(player, "teleport");
        }
    }

    private void migrateOldConfig() {
        final YamlConfiguration location = fileManager.getYamlLocation();
        
        // Check if we have old format and no new format
        if (isLocationConfigValid() && !location.isConfigurationSection(WORLD_SPAWNS_KEY)) {
            plugin.getLogger().info("Migrating spawn configuration to world-based format...");
            
            // Load old spawn
            Location oldSpawn = new Location(
                    Bukkit.getWorld(location.getString(WORLD_KEY)),
                    location.getDouble(X_KEY),
                    location.getDouble(Y_KEY),
                    location.getDouble(Z_KEY),
                    (float) location.getDouble(YAW_KEY),
                    (float) location.getDouble(PITCH_KEY)
            );
            
            // Clear old keys
            location.set(WORLD_KEY, null);
            location.set(X_KEY, null);
            location.set(Y_KEY, null);
            location.set(Z_KEY, null);
            location.set(YAW_KEY, null);
            location.set(PITCH_KEY, null);
            
            // Save as default spawn and world-specific spawn
            if (oldSpawn.getWorld() != null) {
                saveSpawn(oldSpawn);
                saveWorldSpawn(oldSpawn.getWorld().getName(), oldSpawn);
            }
            
            plugin.getLogger().info("Migration completed successfully!");
        }
    }

    private void loadSpawns() {
        // Load default spawn
        this.spawnLocation = loadSpawn();
        
        // Load world-specific spawns
        final YamlConfiguration location = fileManager.getYamlLocation();
        if (location.isConfigurationSection(WORLD_SPAWNS_KEY)) {
            for (String worldName : location.getConfigurationSection(WORLD_SPAWNS_KEY).getKeys(false)) {
                Location worldSpawn = loadWorldSpawn(worldName);
                if (worldSpawn != null) {
                    worldSpawns.put(worldName, worldSpawn);
                }
            }
        }
    }

    private Location loadWorldSpawn(String worldName) {
        final YamlConfiguration location = fileManager.getYamlLocation();
        String basePath = WORLD_SPAWNS_KEY + "." + worldName;
        
        if (location.getString(basePath + ".world") == null) {
            return null;
        }
        
        World world = Bukkit.getWorld(location.getString(basePath + ".world"));
        if (world == null) {
            plugin.getLogger().warning("World '" + worldName + "' for spawn location not found!");
            return null;
        }
        
        return new Location(
                world,
                location.getDouble(basePath + ".x"),
                location.getDouble(basePath + ".y"),
                location.getDouble(basePath + ".z"),
                (float) location.getDouble(basePath + ".yaw"),
                (float) location.getDouble(basePath + ".pitch")
        );
    }

    private void saveWorldSpawn(String worldName, Location spawnLocation) {
        final ConfigFile location = fileManager.getLocation();
        final YamlConfiguration locationYaml = location.get();
        String basePath = WORLD_SPAWNS_KEY + "." + worldName;

        locationYaml.set(basePath + ".world", spawnLocation.getWorld().getName());
        locationYaml.set(basePath + ".x", spawnLocation.getX());
        locationYaml.set(basePath + ".y", spawnLocation.getY());
        locationYaml.set(basePath + ".z", spawnLocation.getZ());
        locationYaml.set(basePath + ".yaw", spawnLocation.getYaw());
        locationYaml.set(basePath + ".pitch", spawnLocation.getPitch());

        location.save();
        location.reload();
    }

    private void saveSpawn(Location newSpawnLocation) {
        final ConfigFile location = fileManager.getLocation();
        final YamlConfiguration locationYaml = location.get();

        locationYaml.set(WORLD_KEY, newSpawnLocation.getWorld().getName());
        locationYaml.set(X_KEY, newSpawnLocation.getX());
        locationYaml.set(Y_KEY, newSpawnLocation.getY());
        locationYaml.set(Z_KEY, newSpawnLocation.getZ());
        locationYaml.set(YAW_KEY, newSpawnLocation.getYaw());
        locationYaml.set(PITCH_KEY, newSpawnLocation.getPitch());

        location.save();
        location.reload();
    }

    public Location loadSpawn() {
        if (!isLocationConfigValid()) {
            return null;
        }

        final YamlConfiguration location = fileManager.getYamlLocation();
        return new Location(
                Bukkit.getWorld(location.getString(WORLD_KEY)),
                location.getDouble(X_KEY),
                location.getDouble(Y_KEY),
                location.getDouble(Z_KEY),
                (float) location.getDouble(YAW_KEY),
                (float) location.getDouble(PITCH_KEY)
        );
    }

    private boolean isLocationConfigValid() {
        final YamlConfiguration location = fileManager.getYamlLocation();

        return location.getString(WORLD_KEY) != null
                && location.get(X_KEY) != null
                && location.get(Y_KEY) != null
                && location.get(Z_KEY) != null
                && location.get(YAW_KEY) != null
                && location.get(PITCH_KEY) != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabledInWorld(World world) {
        final YamlConfiguration config = fileManager.getYamlConfig();
        final List<String> worldList = config.getStringList("plugin.world-list");
        final String worldName = world.getName();

        switch (config.getString("plugin.list-type").toLowerCase(Locale.ROOT)) {
            case "whitelist":
                return worldList.contains(worldName);
            case "blacklist":
                return !worldList.contains(worldName);
            default:
                return true;
        }
    }

    public boolean isAllowedGameMode(Player player) {
        final YamlConfiguration config = fileManager.getYamlConfig();

        if (player.hasPermission("spawn.bypass.gamemode-restriction") || !config.getBoolean("plugin.gamemode-restricted")) {
            return true;
        }

        return config.getStringList("plugin.gamemode-list").stream().anyMatch(s -> s.equalsIgnoreCase(player.getGameMode().toString()));
    }

    public void spawnParticles(Player player) {
        spawnParticles(player, getSpawn(player.getWorld()));
    }

    public void spawnParticles(Player player, Location spawnLoc) {
        final YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("particles.enabled") && spawnLoc != null) {
            String particleName = config.getString("particles.particle");
            int particleAmount = config.getInt("particles.amount");
            try {
                if (Spawn.getServerVersion().getMinor() > 8) {
                    Particle particle = Particle.valueOf(particleName);
                    // display particles for player that teleported
                    player.spawnParticle(particle, spawnLoc, particleAmount);
                    // display particles for other players
                    player.getNearbyEntities(16, 16, 16).stream()
                            .filter(entity -> entity instanceof Player && ((Player) entity).canSee(player))
                            .forEach(entity -> ((Player) entity).spawnParticle(particle, spawnLoc, particleAmount));
                } else {
                    // workaround for 1.8
                    Effect effect = Effect.valueOf(particleName);
                    // you probably do not want to accidentally crash your server
                    int particleAmountLegacy = Math.min(particleAmount, 2000);
                    // display particles for player that teleported
                    for (int p = 0; p <= particleAmountLegacy; p++) {
                        player.playEffect(spawnLoc, effect, null);
                    }
                    // display particles for other players
                    player.getNearbyEntities(16, 16, 16).stream()
                            .filter(entity -> entity instanceof Player && ((Player) entity).canSee(player))
                            .forEach(entity -> {
                                for (int p = 0; p <= particleAmountLegacy; p++) {
                                    ((Player) entity).playEffect(spawnLoc, effect, null);
                                }
                            });
                }
            } catch (Exception e) {
                plugin.getLogger().warning("The particle " + particleName + " does not exist in this Minecraft version!");
            }
        }
    }

    public void playSound(Player player) {
        playSound(player, getSpawn(player.getWorld()));
    }

    public void playSound(Player player, Location spawnLoc) {
        final YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("sounds.enabled") && spawnLoc != null) {
            String sound = config.getString("sounds.sound");
            try {
                player.playSound(spawnLoc, Sound.valueOf(sound), (float) config.getDouble("sounds.volume"), (float) config.getDouble("sounds.pitch"));
            } catch (Exception e) {
                plugin.getLogger().warning("The sound " + sound + " does not exist in this Minecraft version!");
            }
        }
    }
}
