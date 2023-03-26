package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandCooldown;
import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.configuration.*;
import me.rockquiet.spawn.events.TeleportOnJoinEvents;
import me.rockquiet.spawn.events.TeleportOnRespawnEvent;
import me.rockquiet.spawn.events.TeleportOutOfVoidEvent;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Spawn extends JavaPlugin {

    @Override
    public void onEnable() {
        FileManager fileManager = new FileManager(this);
        FileUpdater fileUpdater = new FileUpdater(this, fileManager);

        Messages messageManager;
        if (Arrays.stream(Package.getPackages()).noneMatch(aPackage -> aPackage.getName().contains("io.papermc")) || Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1]) <= 17) {
            messageManager = new MessageManagerLegacy(fileManager);
        } else {
            messageManager = new MessageManager(fileManager);
        }

        SpawnTeleport spawnTeleport = new SpawnTeleport(this, fileManager, messageManager);
        CommandCooldown commandCooldown = new CommandCooldown(fileManager);
        CommandDelay commandDelay = new CommandDelay(this, fileManager, messageManager, spawnTeleport);

        // create all files and update them if outdated
        fileUpdater.updateFile("config.yml", 1);
        fileUpdater.updateFile("messages.yml", 1);

        // register commands with tabcomplete
        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand(fileManager, messageManager, spawnTeleport, commandCooldown, commandDelay));
        getCommand("spawn").setTabCompleter(tc);

        // register events
        Bukkit.getPluginManager().registerEvents(new TeleportOnJoinEvents(fileManager, spawnTeleport), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOutOfVoidEvent(fileManager, spawnTeleport), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOnRespawnEvent(fileManager, messageManager, spawnTeleport), this);
        Bukkit.getPluginManager().registerEvents(new CommandDelay(this, fileManager, messageManager, spawnTeleport), this);

        // check for new plugin versions
        YamlConfiguration config = fileManager.getConfig();
        if (config.getBoolean("plugin.update-checks")) {
            new UpdateChecker(this, 106188).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    getLogger().info("An update is available! Latest version: " + version);
                }
            });
        }
    }
}
