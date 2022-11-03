package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // teleport to spawn - /spawn
            if (args.length == 0) {
                if (player.isOp() || player.hasPermission("spawn.use")) {
                    Spawn.getPlugin().teleportPlayer(player);
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

                    Spawn.getPlugin().sendMessage(player, "messages.spawn-set");
                } else {
                    Spawn.getPlugin().sendMessage(player, "messages.no-permission");
                }
            // reload config - /spawn reload
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (player.isOp() || player.hasPermission("spawn.reload")) {
                    Spawn.getPlugin().reloadConfig();

                    Spawn.getPlugin().sendMessage(player, "messages.config-reload");
                } else {
                    Spawn.getPlugin().sendMessage(player, "messages.no-permission");
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Spawn.getPlugin().getConfig().getString("messages.no-player")));
        }
        return false;
    }
}
