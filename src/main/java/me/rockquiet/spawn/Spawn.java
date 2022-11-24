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

    private static Spawn plugin;
    private final ConfigManger config = new ConfigManger();

    @Override
    public void onEnable() {
        plugin = this;

        config.load();

        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("spawn").setTabCompleter(tc);

        Bukkit.getPluginManager().registerEvents(new TeleportOnJoinEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOutOfVoidEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOnRespawnEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CommandDelay(), this);
    }

    public static Spawn getPlugin() {
        return plugin;
    }
}
