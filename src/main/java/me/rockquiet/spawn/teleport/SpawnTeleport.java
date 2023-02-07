package me.rockquiet.spawn.teleport;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.MessageManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpawnTeleport {

    private final Spawn plugin;
    private final FileManager fileManager;
    private final MessageManager messageManager;

    public SpawnTeleport(Spawn plugin,
                         FileManager fileManager,
                         MessageManager messageManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
    }

    public Location getSpawn() {
        YamlConfiguration location = fileManager.getLocation();

        World world = Bukkit.getWorld(location.getString("spawn.world"));
        double x = location.getDouble("spawn.x");
        double y = location.getDouble("spawn.y");
        double z = location.getDouble("spawn.z");
        float yaw = (float) location.getDouble("spawn.yaw");
        float pitch = (float) location.getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean spawnExists() {
        YamlConfiguration location = fileManager.getLocation();

        return (location.getString("spawn.world") != null && location.getString("spawn.x") != null && location.getString("spawn.y") != null && location.getString("spawn.z") != null && location.getString("spawn.yaw") != null && location.getString("spawn.pitch") != null);
    }

    public void teleportPlayer(Player player) {
        YamlConfiguration config = fileManager.getConfig();

        if (spawnExists()) {
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
            try {
                if (!Bukkit.getVersion().contains("1.8")) {
                    player.spawnParticle(Particle.valueOf(config.getString("particles.particle")), getSpawn(), config.getInt("particles.amount"));
                } else {
                    // workaround for 1.8
                    for (int p = 0; p <= config.getInt("particles.amount"); p++) {
                        Bukkit.getWorld(getSpawn().getWorld().getName()).playEffect(getSpawn(), Effect.valueOf(config.getString("particles.particle")), 0);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("The particle " + config.getString("particles.particle") + " does not exist in this Minecraft version!");
            }
        }

        // Sounds
        if (config.getBoolean("sounds.enabled")) {
            try {
                player.playSound(getSpawn(), Sound.valueOf(config.getString("sounds.sound")), (float) config.getDouble("sounds.volume"), (float) config.getDouble("sounds.pitch"));
            } catch (Exception e) {
                plugin.getLogger().warning("The sound " + config.getString("sounds.sound") + " does not exist in this Minecraft version!");
            }
        }
    }
}
