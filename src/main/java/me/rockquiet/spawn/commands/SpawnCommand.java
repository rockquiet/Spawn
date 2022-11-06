package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                // teleport to spawn - /spawn
                if (player.isOp() || player.hasPermission("spawn.use")) {
                    Spawn.getPlugin().teleportPlayer(player);
                }
            } else if (args.length == 1) {
                Player target = Bukkit.getServer().getPlayerExact(args[0]);
                // save current position as spawn in config - /spawn set
                if (args[0].equalsIgnoreCase("set")) {
                    if (player.isOp() || player.hasPermission("spawn.set")) {
                        Spawn.getPlugin().getConfig().set("spawn.world", player.getWorld().getName());
                        Spawn.getPlugin().getConfig().set("spawn.x", player.getLocation().getX());
                        Spawn.getPlugin().getConfig().set("spawn.y", player.getLocation().getY());
                        Spawn.getPlugin().getConfig().set("spawn.z", player.getLocation().getZ());
                        Spawn.getPlugin().getConfig().set("spawn.yaw", player.getLocation().getYaw());
                        Spawn.getPlugin().getConfig().set("spawn.pitch", player.getLocation().getPitch());
                        Spawn.getPlugin().saveConfig();

                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.spawn-set");
                    } else {
                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-permission");
                    }
                // reload config - /spawn reload
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (player.isOp() || player.hasPermission("spawn.reload")) {
                        Spawn.getPlugin().reloadConfig();

                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.config-reload");
                    } else {
                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-permission");
                    }
                // teleport another player to spawn - /spawn %player%
                } else if (target != null) {
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        if (target.isOnline()) {
                            Spawn.getPlugin().teleportPlayer(target);

                            Spawn.getPlugin().sendMessageToPlayer(player, "messages.teleport-other");
                        }
                    } else {
                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else {
                    Spawn.getPlugin().sendMessageToPlayer(player, "messages.player-not-found");
                }
            }
        } else {
            Spawn.getPlugin().sendMessageToSender(sender, "messages.no-player");
        }
        return false;
    }
}
