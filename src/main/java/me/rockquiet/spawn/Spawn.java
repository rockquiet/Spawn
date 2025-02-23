package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandCooldown;
import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.configuration.*;
import me.rockquiet.spawn.listeners.TeleportOnJoinListener;
import me.rockquiet.spawn.listeners.TeleportOnRespawnListener;
import me.rockquiet.spawn.listeners.TeleportOnWorldChangeListener;
import me.rockquiet.spawn.listeners.TeleportOutOfVoidListener;
import me.rockquiet.spawn.updater.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Spawn extends JavaPlugin {

    public final static boolean isFolia = classExist("io.papermc.paper.threadedregions.RegionizedServerInitEvent");

    private static boolean classExist(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
    
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
        fileUpdater.updateFile("config.yml", 5);
        fileUpdater.updateFile("messages.yml", 3);

        // register commands with tabcomplete
        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand(fileManager, messageManager, spawnHandler, commandCooldown, commandDelay));
        getCommand("spawn").setTabCompleter(tc);

        // register events
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TeleportOnJoinListener(fileManager, spawnHandler), this);
        pluginManager.registerEvents(new TeleportOutOfVoidListener(fileManager, spawnHandler), this);
        pluginManager.registerEvents(new TeleportOnRespawnListener(fileManager, messageManager, spawnHandler), this);
        pluginManager.registerEvents(new TeleportOnWorldChangeListener(fileManager, spawnHandler), this);
        pluginManager.registerEvents(commandCooldown, this);
        pluginManager.registerEvents(commandDelay, this);

        boolean updateChecks = fileManager.getConfig().getBoolean("plugin.update-checks");

        // bstats
        Metrics metrics = new Metrics(this, 19297);
        metrics.addCustomChart(new SimplePie("update_checks", () -> String.valueOf(updateChecks)));

        // check for new plugin versions
        if (updateChecks) {
            new UpdateChecker(this);
        }
    }
}
