package me.rockquiet.spawn.configuration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageManager implements Messages {

    private final FileManager fileManager;
    private final MiniMessage msg;

    public MessageManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.msg = MiniMessage.miniMessage();
    }

    @Override
    public boolean messageExists(String messagePath) {
        YamlConfiguration messages = fileManager.getYamlMessages();

        return messages.getString(messagePath) != null && !messages.getString(messagePath).isEmpty();
    }

    @Override
    public String getMessage(String messagePath) {
        YamlConfiguration messages = fileManager.getYamlMessages();

        return messages.getString(messagePath).replace("%prefix%", (messages.getString("prefix") != null ? messages.getString("prefix") : ""));
    }

    public String convertLegacyToMiniMessage(String string) {
        return string
                // color codes
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                // formatting codes
                .replace("&k", "<obf>")     // obfuscated
                .replace("&l", "<b>")       // bold
                .replace("&m", "<st>")      // strikethrough
                .replace("&n", "<u>")       // underlined
                .replace("&o", "<i>")       // italic
                .replace("&r", "<reset>");
    }

    @Override
    public void sendMessage(Player player, String messagePath) {
        if (messageExists(messagePath)) {
            player.sendMessage(msg.deserialize(convertLegacyToMiniMessage(getMessage(messagePath))));
        }
    }

    @Override
    public void sendMessage(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            player.sendMessage(msg.deserialize(convertLegacyToMiniMessage(getMessage(messagePath)).replace(placeholder, replacePlaceholder)));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath) {
        if (messageExists(messagePath)) {
            sender.sendMessage(msg.deserialize(convertLegacyToMiniMessage(getMessage(messagePath))));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            sender.sendMessage(msg.deserialize(convertLegacyToMiniMessage(getMessage(messagePath)).replace(placeholder, replacePlaceholder)));
        }
    }
}
