package me.rockquiet.spawn.configuration;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ConfigFile {

    static final String FILE_VERSION = "file-version";

    private final Spawn plugin;

    private final Path dataFolderPath;
    private final String fileName;
    private final Path filePath;
    private YamlConfiguration config;

    public ConfigFile(Spawn plugin, Path dataFolderPath, String name) {
        this.plugin = plugin;
        this.dataFolderPath = dataFolderPath;
        this.fileName = name + ".yml";
        this.filePath = dataFolderPath.resolve(this.fileName);

        reload();
    }

    public String getFileName() {
        return fileName;
    }

    public YamlConfiguration get() {
        if (config == null) {
            reload();
        }
        return config;
    }

    // #contains(String path, boolean ignoreDefault) is 1.9.2+
    public boolean containsIgnoreDefault(String path) {
        return config.get(path, null) != null;
    }

    public void reload() {
        if (Files.notExists(filePath)) {
            create();
        }
        config = YamlConfiguration.loadConfiguration(filePath.toFile());

        // load default values
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream == null) return;

            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                config.setDefaults(YamlConfiguration.loadConfiguration(reader));
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to load defaults for file: " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void create() {
        // create the plugin directory if it doesn't exist
        if (!Files.isDirectory(dataFolderPath)) {
            try {
                Files.createDirectory(dataFolderPath);
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to create plugin directory: " + dataFolderPath);
                plugin.getLogger().severe(e.getMessage());
            }
        }

        // copy embedded file to the plugin directory
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (final IOException e) {
            plugin.getLogger().severe("Unable to create file: " + fileName);
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void update(int bundledFileVersion) {
        if (Files.notExists(filePath)) {
            create();
            return;
        }

        if (!containsIgnoreDefault(FILE_VERSION)) return;
        final int currentFileVersion = config.getInt(FILE_VERSION);

        try {
            if (currentFileVersion < bundledFileVersion) {
                backup();

                ConfigUpdater.update(plugin, fileName, filePath.toFile());
                config.set(FILE_VERSION, bundledFileVersion);

                save();
                reload();
                plugin.getLogger().info("Successfully updated " + fileName + " (" + currentFileVersion + " -> " + bundledFileVersion + ")");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to update " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void save() {
        try {
            config.save(filePath.toFile());
        } catch (final IOException e) {
            plugin.getLogger().severe("Unable to save " + fileName);
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void backup() {
        final Path backupsPath = dataFolderPath.resolve("backups");
        try {
            plugin.getLogger().info("Backing up " + fileName + " into the backups directory...");
            if (!Files.exists(backupsPath)) Files.createDirectory(backupsPath);
            final Path fileBackupPath = backupsPath.resolve(new SimpleDateFormat("'" + fileName + "_'yyyyMMdd-HHmm'.yml'").format(new Date()));
            Files.copy(filePath, fileBackupPath);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to backup " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    void delete() {
        try {
            Files.deleteIfExists(filePath);
            config = null; // invalidate previous configuration
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to delete " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }
}
