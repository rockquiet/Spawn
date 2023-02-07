package me.rockquiet.spawn.configuration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageManager {

    private final FileManager fileManager;

    public MessageManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public boolean messageExists(String messagePath) {
        YamlConfiguration messages = fileManager.getMessages();

        return (messages.getString(messagePath) != null && !messages.getString(messagePath).isEmpty());
    }

    private String getMessage(String messagePath) {
        YamlConfiguration messages = fileManager.getMessages();

        return messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"));
    }

    public void sendMessage(Player player, String messagePath) {
        if (messageExists(messagePath)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)));
        }
    }

    public void sendMessage(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            String placeholderMessage = getMessage(messagePath).replace(placeholder, replacePlaceholder);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', placeholderMessage));
        }
    }

    public void sendMessage(CommandSender sender, String messagePath) {
        if (messageExists(messagePath)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)));
        }
    }

    public void sendMessage(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            String placeholderMessage = getMessage(messagePath).replace(placeholder, replacePlaceholder);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', placeholderMessage));
        }
    }
}
