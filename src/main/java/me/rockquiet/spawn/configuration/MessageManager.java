package me.rockquiet.spawn.configuration;

import me.rockquiet.spawn.Spawn;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class MessageManager {

    private final ConfigManager configManager;

    public MessageManager(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
    }

    public boolean messageExists(String messagePath) {
        final Configuration messages = configManager.getLanguageFile();

        return (!messages.getString(messagePath).isEmpty() && messages.getString(messagePath) != null);
    }

    public void sendMessageToPlayer(Player player, String messagePath) {
        final Configuration messages = configManager.getLanguageFile();

        if (messageExists(messagePath)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendPlaceholderMessageToPlayer(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        final Configuration messages = configManager.getLanguageFile();

        if (messageExists(messagePath)) {
            String placeholderMessage = messages.getString(messagePath).replace(placeholder, replacePlaceholder);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', placeholderMessage.replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendMessageToSender(CommandSender sender, String messagePath) {
        final Configuration messages = configManager.getLanguageFile();

        if (messageExists(messagePath)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"))));
        }
    }

    public void sendPlaceholderMessageToSender(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        final Configuration messages = configManager.getLanguageFile();

        if (messageExists(messagePath)) {
            String placeholderMessage = messages.getString(messagePath).replace(placeholder, replacePlaceholder);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', placeholderMessage.replace("%prefix%", messages.getString("prefix"))));
        }
    }
}
