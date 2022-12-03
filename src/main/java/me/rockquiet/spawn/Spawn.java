package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.events.TeleportOnJoinEvents;
import me.rockquiet.spawn.events.TeleportOnRespawnEvent;
import me.rockquiet.spawn.events.TeleportOutOfVoidEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spawn extends JavaPlugin {

    @Override
    public void onEnable() {
        final ConfigManager configManager = new ConfigManager(this);
        configManager.createFile("config.yml");
        configManager.createFile("location.yml");
        configManager.createFile("languages/messages-en.yml");
        configManager.createFile("languages/messages-de.yml");
        configManager.createFile("languages/messages-custom.yml");

        configManager.updateFile("config.yml", 1);
        configManager.updateFile("languages/messages-en.yml", 1);
        configManager.updateFile("languages/messages-de.yml", 1);
        configManager.updateFile("languages/messages-custom.yml", 1);

        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("spawn").setTabCompleter(tc);

        Bukkit.getPluginManager().registerEvents(new TeleportOnJoinEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOutOfVoidEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOnRespawnEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandDelay(this), this);

    public static Spawn getPlugin() {
        return plugin;
    }
}
