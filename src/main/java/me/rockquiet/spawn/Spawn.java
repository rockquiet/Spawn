package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.CommandDelay;
import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.events.TeleportOnJoinEvents;
import me.rockquiet.spawn.events.TeleportOnRespawnEvent;
import me.rockquiet.spawn.events.TeleportOutOfVoidEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spawn extends JavaPlugin {

    @Override
    public void onEnable() {
        final ConfigManager configManager = new ConfigManager(this);
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

        final Configuration config = configManager.getFile("config.yml");
        if (config.getBoolean("update-checks")) {
            new UpdateChecker(this, 106188).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    getLogger().info("An update is available! Latest version: " + version);
                }
            });
        }
    }
}
