package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandCooldown;
import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.configuration.*;
import me.rockquiet.spawn.listeners.TeleportOnJoinListener;
import me.rockquiet.spawn.listeners.TeleportOnRespawnListener;
import me.rockquiet.spawn.listeners.TeleportOutOfVoidListener;
import me.rockquiet.spawn.updater.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Spawn extends JavaPlugin {

    @Override
    public void onEnable() {
        FileManager fileManager = new FileManager(this);

        Messages messageManager;
        String bukkitVersion = Bukkit.getBukkitVersion();
        if (Arrays.stream(Package.getPackages()).noneMatch(aPackage -> aPackage.getName().contains("io.papermc")) || Integer.parseInt(bukkitVersion.split("\\.")[1].replace("-R0", "")) <= 18 && !bukkitVersion.contains("1.18.2")) {
            messageManager = new MessageManagerLegacy(fileManager);
        } else {
            messageManager = new MessageManager(fileManager);
        }

        SpawnHandler spawnHandler = new SpawnHandler(this, fileManager, messageManager);
        CommandCooldown commandCooldown = new CommandCooldown(fileManager);
        CommandDelay commandDelay = new CommandDelay(this, fileManager, messageManager, spawnHandler);

        // create all files and update them if outdated
        FileUpdater fileUpdater = new FileUpdater(this, fileManager);
        fileUpdater.updateFile("config.yml", 2);
        fileUpdater.updateFile("messages.yml", 2);

        // register commands with tabcomplete
        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand(fileManager, messageManager, spawnHandler, commandCooldown, commandDelay));
        getCommand("spawn").setTabCompleter(tc);

        // register events
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TeleportOnJoinListener(fileManager, spawnHandler), this);
        pluginManager.registerEvents(new TeleportOutOfVoidListener(fileManager, spawnHandler), this);
        pluginManager.registerEvents(new TeleportOnRespawnListener(fileManager, messageManager, spawnHandler), this);
        pluginManager.registerEvents(new CommandDelay(this, fileManager, messageManager, spawnHandler), this);

        // check for new plugin versions
        YamlConfiguration config = fileManager.getConfig();
        if (config.getBoolean("plugin.update-checks")) {
            new UpdateChecker(this);
        }
    }
}
