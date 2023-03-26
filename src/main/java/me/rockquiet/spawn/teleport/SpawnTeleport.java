package me.rockquiet.spawn.teleport;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnTeleport {

    private final Spawn plugin;
    private final FileManager fileManager;
    private final Messages messageManager;

    public SpawnTeleport(Spawn plugin,
                         FileManager fileManager,
                         Messages messageManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
    }

    public Location getSpawn() {
        YamlConfiguration location = fileManager.getLocation();

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
        YamlConfiguration location = fileManager.getLocation();

        location.set("spawn.world", newSpawnLocation.getWorld().getName());
        location.set("spawn.x", newSpawnLocation.getX());
        location.set("spawn.y", newSpawnLocation.getY());
        location.set("spawn.z", newSpawnLocation.getZ());
        location.set("spawn.yaw", newSpawnLocation.getYaw());
        location.set("spawn.pitch", newSpawnLocation.getPitch());

        fileManager.save(location, "location.yml");

        fileManager.reloadLocation();
    }

    public boolean spawnExists() {
        YamlConfiguration location = fileManager.getLocation();

        return (location.getString("spawn.world") != null
                && location.getString("spawn.x") != null
                && location.getString("spawn.y") != null
                && location.getString("spawn.z") != null
                && location.getString("spawn.yaw") != null
                && location.getString("spawn.pitch") != null);
    }

    public boolean isEnabledInWorld(World world) {
        YamlConfiguration config = fileManager.getConfig();
        List<String> worldList = config.getStringList("plugin.world-list");
        String worldName = world.getName();

        switch (config.getString("plugin.list-type").toLowerCase()) {
            case "whitelist":
                return worldList.stream().anyMatch(s -> s.equals(worldName));
            case "blacklist":
                return worldList.stream().noneMatch(s -> s.equals(worldName));
            default:
                return true;
        }
    }

    public void teleportPlayer(Player player) {
        if (spawnExists()) {
            YamlConfiguration config = fileManager.getConfig();

            if (!config.getBoolean("fall-damage.enabled")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());

            spawnEffects(player);

            messageManager.sendMessage(player, "teleport");
        } else {
            messageManager.sendMessage(player, "no-spawn");
        }
    }

    public void spawnEffects(Player player) {
        YamlConfiguration config = fileManager.getConfig();

        // Particles
        if (config.getBoolean("particles.enabled")) {
            String particle = config.getString("particles.particle");
            int particleAmount = config.getInt("particles.amount");
            try {
                if (!Bukkit.getVersion().contains("1.8")) {
                    player.spawnParticle(Particle.valueOf(particle), getSpawn(), particleAmount);
                } else {
                    // workaround for 1.8
                    for (int p = 0; p <= particleAmount; p++) {
                        Bukkit.getWorld(getSpawn().getWorld().getName()).playEffect(getSpawn(), Effect.valueOf(particle), 0);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("The particle " + particle + " does not exist in this Minecraft version!");
            }
        }

        // Sound
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
