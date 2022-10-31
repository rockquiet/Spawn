package me.rockquiet.spawn;

import me.rockquiet.spawn.commands.SpawnCommand;
import me.rockquiet.spawn.commands.TabComplete;
import me.rockquiet.spawn.events.TeleportOnJoinEvents;
import me.rockquiet.spawn.events.TeleportOnRespawnEvent;
import me.rockquiet.spawn.events.TeleportOutOfVoidEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Spawn extends JavaPlugin {

    private static Spawn plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        TabCompleter tc = new TabComplete();
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("spawn").setTabCompleter(tc);

        Bukkit.getPluginManager().registerEvents(new TeleportOnJoinEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOutOfVoidEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportOnRespawnEvent(), this);
    }

    public static Spawn getPlugin() {
        return plugin;
    }

    public Location getSpawn() {
        reloadConfig();
        if (getConfig().getString("spawn.world") != null && getConfig().getString("spawn.x") != null && getConfig().getString("spawn.y") != null && getConfig().getString("spawn.z") != null && getConfig().getString("spawn.yaw") != null && getConfig().getString("spawn.pitch") != null) {
            World world = Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("spawn.world")));
            double x = getConfig().getDouble("spawn.x");
            double y = getConfig().getDouble("spawn.y");
            double z = getConfig().getDouble("spawn.z");
            float yaw = (float) getConfig().getDouble("spawn.yaw");
            float pitch = (float) getConfig().getDouble("spawn.pitch");

            return new Location(world, x, y, z, yaw, pitch);
        }
        Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("messages.no-spawn"))));
        return null;
    }

    public void teleportMessage(Player player) {
        if (getConfig().getBoolean("options.message-on-teleport")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("messages.teleport"))));
        }
    }
}
