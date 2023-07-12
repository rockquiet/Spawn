package me.rockquiet.spawn.configuration;

import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {

    private final Spawn plugin;

    private YamlConfiguration config;
    private File configFile;
    private YamlConfiguration location;
    private File locationFile;
    private YamlConfiguration messages;
    private File messagesFile;

    public FileManager(Spawn plugin) {
        this.plugin = plugin;
    }

    public String getDataFolder() {
        return (plugin.getDataFolder().toPath() + "/"); // "plugins/Spawn/"
    }

    public YamlConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public YamlConfiguration getLocation() {
        if (location == null) {
            reloadLocation();
        }
        return location;
    }

    public YamlConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }

    public void reloadConfig() {
        if (configFile == null) {
            create("config.yml");
            configFile = new File(getDataFolder() + "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadLocation() {
        if (locationFile == null) {
            locationFile = new File(getDataFolder() + "location.yml");
        }
        location = YamlConfiguration.loadConfiguration(locationFile);
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            create("messages.yml");
            messagesFile = new File(getDataFolder() + "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadAll() {
        reloadConfig();
        reloadLocation();
        reloadMessages();
    }

    public void create(String file) {
        try {
            final File newFile = new File(getDataFolder() + file);

            if (!newFile.exists()) {
                final File parentFile = newFile.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }

                final InputStream inputStream = plugin.getResource(file);
                if (inputStream != null) {
                    Files.copy(inputStream, newFile.toPath());
                } else {
                    newFile.createNewFile();
                }
            }
        } catch (final IOException e) {
            plugin.getLogger().severe("Unable to create " + file);
        }
    }

    public void save(final YamlConfiguration yamlConfiguration, final String file) {
        try {
            yamlConfiguration.save(getDataFolder() + file);
        } catch (final IOException e) {
            plugin.getLogger().severe("Unable to save " + file);
        }
    }

    public void delete(String file) {
        final File file1 = new File(getDataFolder() + file);

        if (file1.exists()) {
            try {
                file1.delete();
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unable to delete " + file + " - renaming it instead");
                file1.renameTo(new File(getDataFolder() + file + "_old"));
            }
        }
    }

    public void backup(String file, String backupFile, boolean deleteOldFile) {
        final File file1 = new File(getDataFolder() + file);

        try {
            if (!Files.exists(Paths.get(getDataFolder() + "/backup"))) {
                Files.createDirectory(Paths.get(getDataFolder() + "/backup"));
            }

            if (new File(getDataFolder() + "backup/" + backupFile).exists() && (deleteOldFile)) {
                delete(backupFile);
            }
            Files.copy(file1.toPath(), Paths.get(getDataFolder() + "backup/" + backupFile), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to backup " + file);
        }
    }
}
