package me.rockquiet.spawn;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Spawn plugin;

    public ConfigManager(Spawn plugin) {
        this.plugin = plugin;
    }

    private String getDataFolder() {
        return (plugin.getDataFolder().toPath() + "/"); // "plugins/Spawn/"
    }

    public YamlConfiguration getFile(String path) {
        final File file = new File(getDataFolder() + path);

        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            return new YamlConfiguration();
        }
    }

    public void reloadAllFiles() {
        getFile("config.yml");
        getFile("location.yml");
        getFile("languages/messages-en.yml");
        getFile("languages/messages-de.yml");
        getFile("languages/messages-custom.yml");
    }

    public void createFile(String file) {
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
            plugin.getLogger().warning("Unable to create " + file);
        }
    }

    public void saveFile(final YamlConfiguration yamlConfiguration, final String file) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                yamlConfiguration.save(getDataFolder() + file);
            } catch (final IOException e) {
                plugin.getLogger().warning("Unable to save " + file);
            }
        });
    }

    public void deleteFile(String file) {
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

    public void updateFile(String file, int fileVersion) {
        try {
            final File outdatedFile = new File(getDataFolder() + file);

            if (outdatedFile.exists()) {
                final YamlConfiguration outdatedFileConfig = getFile(file);

                if (outdatedFileConfig.contains("file-version") && outdatedFileConfig.getInt("file-version") < fileVersion) {
                    final HashMap<String, Object> fileKeys = new HashMap<>();

                    for (String key : outdatedFileConfig.getKeys(false)) {
                        fileKeys.put(key, outdatedFileConfig.get(key));
                    }
                    fileKeys.remove("file-version");

                    deleteFile(file); // delete old file

                    createFile(file); // get latest file packaged in jar

                    final YamlConfiguration updatedFile = getFile(file);

                    for (Map.Entry<String, Object> entry : fileKeys.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        updatedFile.set(key, value);
                    }

                    saveFile(updatedFile, file);

                    plugin.getLogger().info("Successfully updated " + file);
                } else if (!outdatedFileConfig.contains("file-version")) {
                    convertLegacyConfig(file);
                }
            } else {
                // file to update does not exist
                createFile(file);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Unable to update " + file);
        }
    }

    // config.yml from v1.4.1 or below
    public void convertLegacyConfig(String file) {
        final YamlConfiguration outdatedFileConfig = getFile(file);

        if (outdatedFileConfig.get("options") != null && outdatedFileConfig.get("messages") != null && outdatedFileConfig.get("spawn") != null) {
            final HashMap<String, Object> optionsKeys = new HashMap<>();
            final HashMap<String, Object> messagesKeys = new HashMap<>();
            final HashMap<String, Object> locationKeys = new HashMap<>();

            for (String key : outdatedFileConfig.getConfigurationSection("options").getKeys(false)) {
                optionsKeys.put(key, outdatedFileConfig.getConfigurationSection("options").get(key));
            }
            for (String key : outdatedFileConfig.getConfigurationSection("messages").getKeys(false)) {
                messagesKeys.put(key, outdatedFileConfig.getConfigurationSection("messages").get(key));
            }
            messagesKeys.remove("config-reload");
            for (String key : outdatedFileConfig.getConfigurationSection("spawn").getKeys(false)) {
                locationKeys.put(key, outdatedFileConfig.getConfigurationSection("spawn").get(key));
            }

            deleteFile("config.yml");
            deleteFile("languages/messages-custom.yml");
            deleteFile("location.yml");

            createFile("config.yml");
            final YamlConfiguration updatedConfigFile = getFile("config.yml");

            for (Map.Entry<String, Object> configEntry : optionsKeys.entrySet()) {
                String key = configEntry.getKey();
                Object value = configEntry.getValue();

                updatedConfigFile.set("language", "custom");
                updatedConfigFile.set(key, value);
            }

            saveFile(updatedConfigFile, "config.yml");

            createFile("languages/messages-custom.yml");
            final YamlConfiguration updatedMessagesFile = getFile("languages/messages-custom.yml");

            for (Map.Entry<String, Object> messagesEntry : messagesKeys.entrySet()) {
                String key = messagesEntry.getKey();
                Object value = messagesEntry.getValue();

                updatedMessagesFile.set(key, value);
            }

            saveFile(updatedMessagesFile, "languages/messages-custom.yml");

            createFile("location.yml");
            final YamlConfiguration updatedLocationFile = getFile("location.yml");

            for (Map.Entry<String, Object> locationEntry : locationKeys.entrySet()) {
                String key = "spawn." + locationEntry.getKey();
                Object value = locationEntry.getValue();

                updatedLocationFile.set(key, value);
            }

            saveFile(updatedLocationFile, "location.yml");

            plugin.getLogger().warning("Successfully converted old " + file);
            plugin.getLogger().warning("Please check if everything converted correctly!");
        }
    }
}
