package me.rockquiet.spawn.configuration;

import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;

public class FileManager {

    private final ConfigFile config;
    private final ConfigFile messages;
    private final ConfigFile location;

    public FileManager(Spawn plugin) {
        final Path dataFolderPath = plugin.getDataFolder().toPath();

        config = new ConfigFile(plugin, dataFolderPath, "config");
        messages = new ConfigFile(plugin, dataFolderPath, "messages");
        location = new ConfigFile(plugin, dataFolderPath, "location");

        if (LegacyConfigUpgrade.isLegacyConfig(config)) {
            new LegacyConfigUpgrade(plugin, this).start();
        } else {
            config.update(6);
        }
        messages.update(3);
    }

    public ConfigFile getConfig() {
        return config;
    }

    public YamlConfiguration getYamlConfig() {
        return config.get();
    }

    public ConfigFile getMessages() {
        return messages;
    }

    public YamlConfiguration getYamlMessages() {
        return messages.get();
    }

    public ConfigFile getLocation() {
        return location;
    }

    public YamlConfiguration getYamlLocation() {
        return location.get();
    }

    public void reloadAll() {
        config.reload();
        messages.reload();
        location.reload();
    }
}
