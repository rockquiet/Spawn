package me.rockquiet.spawn.configuration;

import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Upgrades a config.yml from v1.4.1 or below
 */
public class LegacyConfigUpgrade {

    private static final String CONFIG_SECTION = "options";
    private static final String MESSAGES_SECTION = "messages";
    private static final String LOCATION_SECTION = "spawn";

    private final Spawn plugin;
    private final FileManager fileManager;

    public LegacyConfigUpgrade(Spawn plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public static boolean isLegacyConfig(ConfigFile configFile) {
        final boolean hasLegacyConfigSections = configFile.containsIgnoreDefault(CONFIG_SECTION)
                && configFile.containsIgnoreDefault(MESSAGES_SECTION)
                && configFile.containsIgnoreDefault(LOCATION_SECTION);
        final boolean hasFileVersion = configFile.containsIgnoreDefault(ConfigFile.FILE_VERSION);

        return hasLegacyConfigSections && !hasFileVersion;
    }

    public void start() {
        final String fileName = fileManager.getConfig().getFileName();
        plugin.getLogger().warning("Found legacy " + fileName + " - The plugin will now upgrade it to the latest format");
        final YamlConfiguration legacyFileConfig = fileManager.getConfig().get();

        final Map<String, Object> optionsMap = legacyFileConfig.getConfigurationSection(CONFIG_SECTION).getKeys(false)
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> legacyFileConfig.getConfigurationSection(CONFIG_SECTION).get(key)
                ));
        upgradeConfigSection(optionsMap);

        final Map<String, Object> messagesMap = legacyFileConfig.getConfigurationSection(MESSAGES_SECTION).getKeys(false)
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> legacyFileConfig.getConfigurationSection(MESSAGES_SECTION).get(key)
                ));
        messagesMap.remove("config-reload");
        upgradeSection(fileManager.getMessages(), messagesMap, "");

        final Map<String, Object> locationMap = legacyFileConfig.getConfigurationSection(LOCATION_SECTION).getKeys(false)
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> legacyFileConfig.getConfigurationSection(LOCATION_SECTION).get(key)
                ));
        if (!locationMap.isEmpty()) {
            upgradeSection(fileManager.getLocation(), locationMap, LOCATION_SECTION);
        }

        fileManager.reloadAll();
        plugin.getLogger().warning("Successfully upgraded legacy " + fileName);
        plugin.getLogger().warning("Please check if everything upgraded correctly!");
    }

    private void upgradeConfigSection(Map<String, Object> optionsMap) {
        final ConfigFile config = fileManager.getConfig();
        // move old config.yml into backup folder
        config.backup();
        config.delete();
        // get latest file packaged in jar
        final YamlConfiguration yamlConfiguration = config.get();

        final Map<String, String> values = new HashMap<>(10);
        values.put("fall-damage", "fall-damage.enabled");
        values.put("teleport-on-join", "teleport-on-join.enabled");
        values.put("teleport-on-first-join", "teleport-on-join.only-first-join");
        values.put("cancel-on-move", "teleport-delay.cancel-on-move");
        values.put("teleport-out-of-void", "teleport-out-of-void.enabled");
        values.put("void-check-height", "teleport-out-of-void.check-height");
        values.put("teleport-on-respawn", "teleport-on-respawn.enabled");
        values.put("ignore-bed-spawn", "teleport-on-respawn.ignore-bed-spawn");
        values.put("particle", "particles.particle");
        values.put("sound", "sounds.sound");
        values.put("sound-pitch", "sounds.pitch");
        values.forEach((legacyKey, key) -> renameKey(yamlConfiguration, optionsMap, legacyKey, key));

        renameKeyUpdateOrDisable(yamlConfiguration, optionsMap, "teleport-cooldown", "teleport-cooldown.enabled", "teleport-cooldown.seconds");
        renameKeyUpdateOrDisable(yamlConfiguration, optionsMap, "teleport-delay", "teleport-delay.enabled", "teleport-delay.seconds");
        renameKeyUpdateOrDisable(yamlConfiguration, optionsMap, "particle-amount", "particles.enabled", "particles.amount");
        renameKeyUpdateOrDisable(yamlConfiguration, optionsMap, "sound-volume", "sounds.enabled", "sounds.volume");

        config.save();
    }

    private void renameKey(YamlConfiguration config, Map<String, Object> optionsMap, String legacyKey, String key) {
        if (optionsMap.containsKey(legacyKey)) {
            config.set(key, optionsMap.get(legacyKey));
        }
    }

    private void renameKeyUpdateOrDisable(YamlConfiguration config, Map<String, Object> optionsMap, String legacyKey, String enabledKey, String valueKey) {
        if (optionsMap.containsKey(legacyKey)) {
            final Number number = (Number) optionsMap.get(legacyKey);
            if (number.doubleValue() <= 0) {
                // value <= 0 -> false
                config.set(enabledKey, false);
            } else {
                config.set(valueKey, number);
            }
        }
    }

    private void upgradeSection(ConfigFile configFile, Map<String, Object> sectionMap, String sectionName) {
        final YamlConfiguration yamlConfiguration = configFile.get();

        for (Map.Entry<String, Object> entry : sectionMap.entrySet()) {
            String key = sectionName.isEmpty() ? entry.getKey() : sectionName + "." + entry.getKey();
            Object value = entry.getValue();

            yamlConfiguration.set(key, value);
        }

        configFile.save();
    }
}
