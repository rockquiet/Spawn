package me.rockquiet.spawn.configuration;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUpdater {

    private final Spawn plugin;
    private final FileManager fileManager;

    public FileUpdater(Spawn plugin,
                       FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public void updateFile(String file, int fileVersion) {
        try {
            final File outdatedFile = new File(fileManager.getDataFolder() + file);

            if (!outdatedFile.exists()) {
                fileManager.create(file);
                return;
            }

            final YamlConfiguration outdatedFileConfig = YamlConfiguration.loadConfiguration(outdatedFile);

            if (outdatedFileConfig.contains("file-version") && outdatedFileConfig.getInt("file-version") < fileVersion) {
                // backup old file
                fileManager.backup(file, file + "_" + outdatedFileConfig.getInt("file-version") + "_old", false);

                // add new file entries
                ConfigUpdater.update(plugin, file, new File(plugin.getDataFolder(), file));

                // update file version value
                final YamlConfiguration updatedFileConfig = YamlConfiguration.loadConfiguration(new File(fileManager.getDataFolder() + file));
                updatedFileConfig.set("file-version", fileVersion);

                fileManager.save(updatedFileConfig, file);
                plugin.getLogger().info("Successfully updated " + file);
            } else if (!outdatedFileConfig.contains("file-version")) {
                convertLegacyConfig(file);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Unable to update " + file);
            plugin.getLogger().warning("Exception: " + e);
        }
    }

    // convert config.yml from v1.4.1 or below
    public void convertLegacyConfig(String file) {
        final File legacyFile = new File(fileManager.getDataFolder() + file);
        final YamlConfiguration legacyFileConfig = YamlConfiguration.loadConfiguration(legacyFile);

        if (legacyFileConfig.contains("options") && legacyFileConfig.contains("messages") && legacyFileConfig.contains("spawn")) {
            final HashMap<String, Object> options = new HashMap<>();
            final HashMap<String, Object> messages = new HashMap<>();
            final HashMap<String, Object> location = new HashMap<>();

            for (String key : legacyFileConfig.getConfigurationSection("options").getKeys(false)) {
                options.put(key, legacyFileConfig.getConfigurationSection("options").get(key));
            }
            for (String key : legacyFileConfig.getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, legacyFileConfig.getConfigurationSection("messages").get(key));
            }
            messages.remove("config-reload");
            for (String key : legacyFileConfig.getConfigurationSection("spawn").getKeys(false)) {
                location.put(key, legacyFileConfig.getConfigurationSection("spawn").get(key));
            }

            // move old config.yml into backup folder
            fileManager.backup(file, "old_config.yml", true);
            // fileManager.delete("config.yml");


            fileManager.create("config.yml"); // get latest file packaged in jar
            YamlConfiguration updatedConfigFile = fileManager.getConfig();

            // TODO there is probably a better way doing this
            if (options.containsKey("fall-damage")) {
                // options.fall-damage -> fall-damage.enabled
                updatedConfigFile.set("fall-damage.enabled", options.get("fall-damage"));
            }

            if (options.containsKey("teleport-on-join")) {
                // options.teleport-on-join -> teleport-on-join.enabled
                updatedConfigFile.set("teleport-on-join.enabled", options.get("teleport-on-join"));
            }

            // options.teleport-on-first-join -> teleport-on-join.only-first-join
            if (options.containsKey("teleport-on-first-join") && options.get("teleport-on-first-join").equals(true)) {
                updatedConfigFile.set("teleport-on-join.enabled", true);
                updatedConfigFile.set("teleport-on-join.only-first-join", true);
            }

            if (options.containsKey("teleport-cooldown")) {
                // options.teleport-cooldown -> teleport-cooldown.seconds
                updatedConfigFile.set("teleport-cooldown.seconds", options.get("teleport-cooldown"));
            }
            // options.teleport-cooldown: 0 -> teleport-cooldown.enabled: false
            if (options.containsKey("teleport-cooldown") && options.get("teleport-cooldown").equals(0)) {
                updatedConfigFile.set("teleport-cooldown.enabled", false);
            }

            if (options.containsKey("teleport-delay")) {
                // options.teleport-delay -> teleport-delay.seconds
                updatedConfigFile.set("teleport-delay.seconds", options.get("teleport-delay"));
            }
            if (options.containsKey("cancel-on-move")) {
                // options.cancel-on-move -> teleport-delay.cancel-on-move
                updatedConfigFile.set("teleport-delay.cancel-on-move", options.get("cancel-on-move"));
            }
            // options.teleport-cooldown: 0 -> teleport-cooldown.enabled: false
            if (options.containsKey("teleport-delay") && options.get("teleport-delay").equals(0)) {
                updatedConfigFile.set("teleport-delay.enabled", false);
            }

            if (options.containsKey("teleport-out-of-void")) {
                // options.teleport-out-of-void -> teleport-out-of-void.enabled
                updatedConfigFile.set("teleport-out-of-void.enabled", options.get("teleport-out-of-void"));
            }
            if (options.containsKey("teleport-out-of-void")) {
                // options.void-check-height -> teleport-out-of-void.check-height
                updatedConfigFile.set("teleport-out-of-void.check-height", options.get("teleport-out-of-void"));
            }

            if (options.containsKey("teleport-on-respawn")) {
                // options.teleport-on-respawn -> teleport-on-respawn.enabled
                updatedConfigFile.set("teleport-on-respawn.enabled", options.get("teleport-on-respawn"));
            }
            if (options.containsKey("ignore-bed-spawn")) {
                // options.ignore-bed-spawn -> teleport-on-respawn.ignore-bed-spawn
                updatedConfigFile.set("teleport-on-respawn.ignore-bed-spawn", options.get("ignore-bed-spawn"));
            }

            if (options.containsKey("particle")) {
                // options.particle -> particles.particle
                updatedConfigFile.set("particles.particle", options.get("particle"));
            }
            if (options.containsKey("particle-amount")) {
                // options.particle-amount -> particles.amount
                updatedConfigFile.set("particles.amount", options.get("particle-amount"));
            }
            // options.particle-amount: 0 -> particles.enabled: false
            if (options.containsKey("particle-amount") && options.get("particle-amount").equals(0)) {
                updatedConfigFile.set("particles.enabled", false);
            }

            if (options.containsKey("sound")) {
                // options.sound -> sounds.sound
                updatedConfigFile.set("sounds.sound", options.get("sound"));
            }
            if (options.containsKey("sound-volume")) {
                // options.sound-volume -> sounds.volume
                updatedConfigFile.set("sounds.volume", options.get("sound-volume"));
            }
            if (options.containsKey("sound-pitch")) {
                // options.sound-pitch -> sounds.pitch
                updatedConfigFile.set("sounds.pitch", options.get("sound-pitch"));
            }
            // options.sound-volume: 0 -> sounds.enabled: false
            if (options.containsKey("sound-volume") && options.get("sound-volume").equals(0)) {
                updatedConfigFile.set("sounds.enabled", false);
            }


            fileManager.save(updatedConfigFile, "config.yml");

            fileManager.create("messages.yml");
            YamlConfiguration updatedMessagesFile = fileManager.getMessages();

            for (Map.Entry<String, Object> messagesEntry : messages.entrySet()) {
                String key = messagesEntry.getKey();
                Object value = messagesEntry.getValue();

                updatedMessagesFile.set(key, value);
            }

            fileManager.save(updatedMessagesFile, "messages.yml");

            if (!location.isEmpty()) {
                fileManager.create("location.yml");
                YamlConfiguration updatedLocationFile = fileManager.getLocation();

                for (Map.Entry<String, Object> locationEntry : location.entrySet()) {
                    String key = "spawn." + locationEntry.getKey();
                    Object value = locationEntry.getValue();

                    updatedLocationFile.set(key, value);
                }

                fileManager.save(updatedLocationFile, "location.yml");
            }

            plugin.getLogger().warning("Successfully converted old " + file);
            plugin.getLogger().warning("Please check if everything converted correctly!");
            plugin.getLogger().warning("The old " + file + " can be found in the backup directory.");
        } else {
            fileManager.backup(file, "broken_config.yml", true);

            fileManager.create("config.yml");
            fileManager.create("messages.yml");

            plugin.getLogger().severe(file + " was recreated due to an error!");
            plugin.getLogger().severe("The broken " + file + " can be found in the backup directory.");
        }
    }
}
