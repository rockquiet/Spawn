package me.rockquiet.spawn.configuration;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Messages {

    boolean messageExists(String messagePath);

    String getMessage(String messagePath);

    void sendMessage(Player player, String messagePath);

    void sendMessage(Player player, String messagePath, String placeholder, String replacePlaceholder);

    void sendMessage(CommandSender sender, String messagePath);

    void sendMessage(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder);
}
