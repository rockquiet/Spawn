package me.rockquiet.spawn.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageManager implements Messages {

    private final FileManager fileManager;

    public MessageManager(FileManager fileManager) {
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

        return messages.getString(messagePath).replace("%prefix%", messages.getString("prefix"));
    }

    @Override
    public void sendMessage(Player player, String messagePath) {
        if (messageExists(messagePath)) {
            Component legacyToMiniMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage(messagePath));
            player.sendMessage(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(legacyToMiniMessage).replace("\\", "")));
        }
    }

    @Override
    public void sendMessage(Player player, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            Component legacyToMiniMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage(messagePath).replace(placeholder, replacePlaceholder));
            player.sendMessage(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(legacyToMiniMessage).replace("\\", "")));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath) {
        if (messageExists(messagePath)) {
            Component legacyToMiniMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage(messagePath));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(legacyToMiniMessage).replace("\\", "")));
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String messagePath, String placeholder, String replacePlaceholder) {
        if (messageExists(messagePath)) {
            Component legacyToMiniMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(getMessage(messagePath).replace(placeholder, replacePlaceholder));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(legacyToMiniMessage).replace("\\", "")));
        }
    }
}
