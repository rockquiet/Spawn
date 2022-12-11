package me.rockquiet.spawn.teleport;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.ConfigManager;
import me.rockquiet.spawn.configuration.MessageManager;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class SpawnTeleport {

    private final Spawn plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public SpawnTeleport(Spawn plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
        this.messageManager = new MessageManager(plugin);
    }

    public Location getSpawn() {
        final Configuration location = configManager.getFile("location.yml");

        World world = Bukkit.getWorld(location.getString("spawn.world"));
        double x = location.getDouble("spawn.x");
        double y = location.getDouble("spawn.y");
        double z = location.getDouble("spawn.z");
        float yaw = (float) location.getDouble("spawn.yaw");
        float pitch = (float) location.getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean spawnExists() {
        final Configuration location = configManager.getFile("location.yml");

        return (location.getString("spawn.world") != null && location.getString("spawn.x") != null && location.getString("spawn.y") != null && location.getString("spawn.z") != null && location.getString("spawn.yaw") != null && location.getString("spawn.pitch") != null);
    }

    public void teleportPlayer(Player player) {
        final Configuration config = configManager.getFile("config.yml");

        if (spawnExists()) {
            if (!config.getBoolean("fall-damage")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());

            spawnEffects(player);

            messageManager.sendMessageToPlayer(player, "teleport");
        } else {
            messageManager.sendMessageToPlayer(player, "no-spawn");
        }
    }

    public void spawnEffects(Player player) {
        final Configuration config = configManager.getFile("config.yml");

        // Particles
        try {
            if (!Bukkit.getVersion().contains("1.8")) {
                player.spawnParticle(Particle.valueOf(config.getString("particle")), getSpawn(), config.getInt("particle-amount"));
            } else {
                // workaround for 1.8
                for (int p = 0; p <= config.getInt("particle-amount"); p++) {
                    Bukkit.getWorld(getSpawn().getWorld().getName()).playEffect(getSpawn(), Effect.valueOf(config.getString("particle")), 0);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("The particle " + config.getString("particle") + " does not exist in this Minecraft version!");
        }

        // Sounds
        try {
            player.playSound(getSpawn(), Sound.valueOf(config.getString("sound")), (float) config.getDouble("sound-volume"), (float) config.getDouble("sound-pitch"));
        } catch (Exception e) {
            plugin.getLogger().warning("The sound " + config.getString("sound") + " does not exist in this Minecraft version!");
        }
    }
}
