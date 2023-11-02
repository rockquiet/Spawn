package me.rockquiet.spawn.configuration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageManagerLegacy implements Messages {

    private final FileManager fileManager;

    public MessageManagerLegacy(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public boolean messageExists(String messagePath) {
        YamlConfiguration messages = fileManager.getMessages();

        return (messages.getString(messagePath) != null && !messages.getString(messagePath).isEmpty());
    }

    @Override
    public String getMessage(String messagePath) {
        YamlConfiguration messages = fileManager.getMessages();

        return messages.getString(messagePath).replace("%prefix%", (messages.getString("prefix") != null ? messages.getString("prefix") : ""));
    }

    @Override
    public void sendMessage(Player player, String messagePath) {
        if (messageExists(messagePath)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)));
        }
    }

    @Override
    public void sendMessage(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)).replace(placeholder, replacePlaceholder));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath) {
        if (messageExists(messagePath)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(messagePath)).replace(placeholder, replacePlaceholder));
        }
    }
}
