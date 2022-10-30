package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SpawnCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // teleport to spawn - /spawn
            if (args.length == 0) {
                if (player.isOp() || player.hasPermission("spawn.use")) {
                    player.teleport(Spawn.getPlugin().getSpawn());

                    Spawn.getPlugin().teleportMessage(player);
                }
            // save current position as spawn in config - /spawn set
            } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                if (player.isOp() || player.hasPermission("spawn.set")) {
                    Spawn.getPlugin().getConfig().set("spawn.world", player.getWorld().getName());
                    Spawn.getPlugin().getConfig().set("spawn.x", player.getLocation().getX());
                    Spawn.getPlugin().getConfig().set("spawn.y", player.getLocation().getY());
                    Spawn.getPlugin().getConfig().set("spawn.z", player.getLocation().getZ());
                    Spawn.getPlugin().getConfig().set("spawn.yaw", player.getLocation().getYaw());
                    Spawn.getPlugin().getConfig().set("spawn.pitch", player.getLocation().getPitch());
                    Spawn.getPlugin().saveConfig();

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Spawn.getPlugin().getConfig().getString("messages.spawn-set"))));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Spawn.getPlugin().getConfig().getString("messages.no-permission"))));
                }
            // reload config - /spawn reload
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (player.isOp() || player.hasPermission("spawn.reload")) {
                    Spawn.getPlugin().reloadConfig();

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Spawn.getPlugin().getConfig().getString("messages.config-reload"))));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Spawn.getPlugin().getConfig().getString("messages.no-permission"))));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Spawn.getPlugin().getConfig().getString("messages.no-player"))));
        }
        return false;
    }
}
