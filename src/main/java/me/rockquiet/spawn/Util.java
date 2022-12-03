package me.rockquiet.spawn;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class Util {

    private final Spawn plugin;

    private final ConfigManager configManager;

    public Util(Spawn plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
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

        return (location.getString("spawn.world") != null && location.getString("spawn.x") != null && location.getString("spawn.y") != null && location.getString("spawn.z") != null && !location.getString("spawn.yaw").isEmpty() && !location.getString("spawn.pitch").isEmpty());
    }

    public void teleportPlayer(Player player) {
        final Configuration config = configManager.getFile("config.yml");

        if (spawnExists()) {
            if (!config.getBoolean("fall-damage")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());

            spawnEffects(player);

            sendMessageToPlayer(player, "teleport");
        } else {
            sendMessageToPlayer(player, "no-spawn");
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

    public String getLanguageFile() {
        final Configuration config = configManager.getFile("config.yml");

        return "languages/messages-" + config.getString("language") + ".yml";
    }

    public void sendMessageToPlayer(Player player, String messagePath) {
        final Configuration messages = configManager.getFile(getLanguageFile());

        if (messages.getString(messagePath).isEmpty() || messages.getString(messagePath) == null) {
            // do not send a message to the player
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendPlaceholderMessageToPlayer(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        final Configuration messages = configManager.getFile(getLanguageFile());

        if (messages.getString(messagePath).isEmpty() || messages.getString(messagePath) == null) {
            // do not send a message to the player
        } else if (messages.getString(messagePath).contains(placeholder)){
            String convertedMessage = messages.getString(messagePath).replace(placeholder, replacePlaceholder);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', convertedMessage.replace("%prefix%", messages.getString("prefix"))));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendMessageToSender(CommandSender sender, String messagePath) {
        final Configuration messages = configManager.getFile(getLanguageFile());

        if (messages.getString(messagePath).isEmpty() || messages.getString(messagePath) == null) {
            // do not send a message to the sender
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendPlaceholderMessageToSender(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        final Configuration messages = configManager.getFile(getLanguageFile());

        if (messages.getString(messagePath).isEmpty() || messages.getString(messagePath) == null) {
            // do not send a message to the sender
        } else if (messages.getString(messagePath).contains(placeholder)){
            String convertedMessage = messages.getString(messagePath).replace(placeholder, replacePlaceholder);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', convertedMessage.replace("%prefix%", messages.getString("prefix"))));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }
}
