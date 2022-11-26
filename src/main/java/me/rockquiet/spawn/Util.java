package me.rockquiet.spawn;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {

    private final ConfigManger config = new ConfigManger();

    public Location getSpawn() {
        return config.getLocation(config.getWorld("spawn.world"), config.getDouble("spawn.x"), config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getFloat("spawn.yaw"), config.getFloat("spawn.pitch"));
    }

    public boolean spawnExists() {
        return (config.getWorld("spawn.world") != null && config.getDouble("spawn.x") != null && config.getDouble("spawn.y") != null && config.getDouble("spawn.z") != null && !config.getString("spawn.yaw").isEmpty() && !config.getString("spawn.pitch").isEmpty());
    }

    public void teleportPlayer(Player player) {
        if (spawnExists()) {
            if (!config.getBoolean("options.fall-damage")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());

            spawnEffects(player);

            sendMessageToPlayer(player, "messages.teleport");
        } else {
            sendMessageToPlayer(player, "messages.no-spawn");
        }
    }

    public void spawnEffects(Player player) {
        // Particles
        try {
            if (!Bukkit.getVersion().contains("1.8")) {
                player.spawnParticle(Particle.valueOf(config.getString("options.particle")), getSpawn(), config.getInt("options.particle-amount"));
            } else {
                // workaround for 1.8
                for (int p = 0; p < config.getInt("options.particle-amount"); p++) {
                    Bukkit.getWorld(getSpawn().getWorld().getName()).playEffect(getSpawn(), Effect.valueOf(config.getString("options.particle")), 0);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Spawn] The particle " + config.getString("options.particle") + " does not exist in this Minecraft version!");
        }

        // Sounds
        try {
            player.playSound(getSpawn(), Sound.valueOf(config.getString("options.sound")), config.getFloat("options.sound-volume"), config.getFloat("options.sound-pitch"));
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Spawn] The sound " + config.getString("options.sound") + " does not exist in this Minecraft version!");
        }
    }

    public void sendMessageToPlayer(Player player, String message) {
        if (config.getString(message).isEmpty() || config.getString(message) == null) {
            // do not send a message to the player
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(message)));
        }
    }

    public void sendPlaceholderMessageToPlayer(Player player, String message, String placeholder, String replacePlaceholder) {
        if (config.getString(message).isEmpty() || config.getString(message) == null) {
            // do not send a message to the player
        } else if (config.getString(message).contains(placeholder)){
            String convertedMessage = config.getString(message).replace(placeholder, replacePlaceholder);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', convertedMessage));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(message)));
        }
    }

    public void sendMessageToSender(CommandSender sender, String message) {
        if (config.getString(message).isEmpty() || config.getString(message) == null) {
            // do not send a message to the sender
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(message)));
        }
    }

    public void sendPlaceholderMessageToSender(CommandSender sender, String message, String placeholder, String replacePlaceholder) {
        if (config.getString(message).isEmpty() || config.getString(message) == null) {
            // do not send a message to the sender
        } else if (config.getString(message).contains(placeholder)){
            String convertedMessage = config.getString(message).replace(placeholder, replacePlaceholder);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', convertedMessage));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(message)));
        }
    }
}
