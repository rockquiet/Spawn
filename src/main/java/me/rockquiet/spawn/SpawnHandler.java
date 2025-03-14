package me.rockquiet.spawn;

import me.rockquiet.spawn.configuration.ConfigFile;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.Locale;

public class SpawnHandler {

    private final Spawn plugin;
    private final FileManager fileManager;
    private final Messages messageManager;

    public SpawnHandler(Spawn plugin,
                        FileManager fileManager,
                        Messages messageManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
    }

    public Location getSpawn() {
        final YamlConfiguration location = fileManager.getYamlLocation();

        return new Location(
                Bukkit.getWorld(location.getString("spawn.world")),
                location.getDouble("spawn.x"),
                location.getDouble("spawn.y"),
                location.getDouble("spawn.z"),
                (float) location.getDouble("spawn.yaw"),
                (float) location.getDouble("spawn.pitch")
        );
    }

    public void setSpawn(Location newSpawnLocation) {
        final ConfigFile location = fileManager.getLocation();
        final YamlConfiguration locationYaml = location.get();

        locationYaml.set("spawn.world", newSpawnLocation.getWorld().getName());
        locationYaml.set("spawn.x", newSpawnLocation.getX());
        locationYaml.set("spawn.y", newSpawnLocation.getY());
        locationYaml.set("spawn.z", newSpawnLocation.getZ());
        locationYaml.set("spawn.yaw", newSpawnLocation.getYaw());
        locationYaml.set("spawn.pitch", newSpawnLocation.getPitch());

        location.save();
        location.reload();
    }

    public boolean spawnExists() {
        YamlConfiguration location = fileManager.getYamlLocation();

        return (location.getString("spawn.world") != null
                && location.getString("spawn.x") != null
                && location.getString("spawn.y") != null
                && location.getString("spawn.z") != null
                && location.getString("spawn.yaw") != null
                && location.getString("spawn.pitch") != null);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabledInWorld(World world) {
        YamlConfiguration config = fileManager.getYamlConfig();
        List<String> worldList = config.getStringList("plugin.world-list");
        String worldName = world.getName();

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
        YamlConfiguration config = fileManager.getYamlConfig();

        if (player.hasPermission("spawn.bypass.gamemode-restriction") || !config.getBoolean("plugin.gamemode-restricted")) {
            return true;
        }

        return config.getStringList("plugin.gamemode-list").stream().anyMatch(s -> s.equalsIgnoreCase(player.getGameMode().toString()));
    }

    public void teleportPlayer(Player player) {
        if (spawnExists()) {
            YamlConfiguration config = fileManager.getYamlConfig();

            if (!config.getBoolean("fall-damage.enabled")) {
                player.setFallDistance(0F);
            }

            Location spawnLocation = getSpawn();
            if (config.getBoolean("use-player-head-rotation.enabled")) {
                spawnLocation.setDirection(player.getLocation().getDirection());
            }

            player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

            spawnParticles(player);
            playSound(player);

            messageManager.sendMessage(player, "teleport");
        } else {
            messageManager.sendMessage(player, "no-spawn");
        }
    }

    public void spawnParticles(Player player) {
        YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("particles.enabled")) {
            String particleName = config.getString("particles.particle");
            int particleAmount = config.getInt("particles.amount");
            Location spawnLocation = getSpawn();
            try {
                if (!Bukkit.getVersion().contains("1.8")) {
                    Particle particle = Particle.valueOf(particleName);
                    // display particles for player that teleported
                    player.spawnParticle(particle, spawnLocation, particleAmount);
                    // display particles for other players
                    player.getNearbyEntities(16, 16, 16).stream()
                            .filter(entity -> entity instanceof Player && ((Player) entity).canSee(player))
                            .forEach(entity -> ((Player) entity).spawnParticle(particle, spawnLocation, particleAmount));
                } else {
                    // workaround for 1.8
                    Effect effect = Effect.valueOf(particleName);
                    // you probably do not want to accidentally crash your server
                    int particleAmountLegacy = (particleAmount > 2000 ? 40 : particleAmount);
                    // display particles for player that teleported
                    for (int p = 0; p <= particleAmountLegacy; p++) {
                        player.playEffect(spawnLocation, effect, 0);
                    }
                    // display particles for other players
                    player.getNearbyEntities(16, 16, 16).stream()
                            .filter(entity -> entity instanceof Player && ((Player) entity).canSee(player))
                            .forEach(entity -> {
                                for (int p = 0; p <= particleAmountLegacy; p++) {
                                    ((Player) entity).playEffect(spawnLocation, effect, 0);
                                }
                            });
                }
            } catch (Exception e) {
                plugin.getLogger().warning("The particle " + particleName + " does not exist in this Minecraft version!");
            }
        }
    }

    public void playSound(Player player) {
        YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("sounds.enabled")) {
            String sound = config.getString("sounds.sound");
            try {
                player.playSound(getSpawn(), Sound.valueOf(sound), (float) config.getDouble("sounds.volume"), (float) config.getDouble("sounds.pitch"));
            } catch (Exception e) {
                plugin.getLogger().warning("The sound " + sound + " does not exist in this Minecraft version!");
            }
        }
    }
}
