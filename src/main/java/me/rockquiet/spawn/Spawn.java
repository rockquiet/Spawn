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
        World world = Bukkit.getWorld(getConfig().getString("spawn.world"));
        double x = getConfig().getDouble("spawn.x");
        double y = getConfig().getDouble("spawn.y");
        double z = getConfig().getDouble("spawn.z");
        float yaw = (float) getConfig().getDouble("spawn.yaw");
        float pitch = (float) getConfig().getDouble("spawn.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void teleportPlayer(Player player) {
        if (getConfig().getString("spawn.world") != null && getConfig().getString("spawn.x") != null && getConfig().getString("spawn.y") != null && getConfig().getString("spawn.z") != null && getConfig().getString("spawn.yaw") != null && getConfig().getString("spawn.pitch") != null) {
            if (!getConfig().getBoolean("options.fall-damage")) {
                player.setFallDistance(0F);
            }
            player.teleport(getSpawn());
            Spawn.getPlugin().sendMessage(player, "messages.teleport");
        } else {
            sendMessage(player, "messages.no-spawn");
        }
    }

    public void sendMessage(Player player, String message) {
        if (getConfig().getString(message).isEmpty() || getConfig().getString(message) == null) {
            // do not send a message to the player
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString(message)));
        }
    }
}
