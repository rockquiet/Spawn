package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.ConfigManger;
import me.rockquiet.spawn.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand implements CommandExecutor {

    private final ConfigManger config = new ConfigManger();
    private final Util util = new Util();
    private final CommandCooldown commandCooldown = new CommandCooldown();
    private final CommandDelay commandDelay = new CommandDelay();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            // teleport to spawn - /spawn
            if (player.isOp() || player.hasPermission("spawn.use")) {
                if (player.isOp() || player.hasPermission("spawn.bypass.cooldown")) {
                    if (player.isOp() || player.hasPermission("spawn.bypass.delay")) {
                        util.teleportPlayer(player);
                    } else {
                        commandDelay.runDelay(player);
                    }
                } else {
                    if (!commandCooldown.hasCooldown(playerUUID) || commandCooldown.isCooldownDone(playerUUID)) {
                        commandCooldown.setCooldown(playerUUID, System.currentTimeMillis());

                        commandDelay.runDelay(player);
                    } else {
                        util.sendPlaceholderMessageToPlayer(player, "messages.cooldown-left", "%cooldown%", String.valueOf(commandCooldown.cooldownTime() - TimeUnit.MILLISECONDS.toSeconds(commandCooldown.getCooldown(playerUUID))));
                    }
                }
            }
        } else if (args.length == 1) {
            Player target = Bukkit.getServer().getPlayerExact(args[0]);
            // save current position as spawn in config - /spawn set
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.set")) {
                        config.set("spawn.world", player.getWorld().getName());
                        config.set("spawn.x", player.getLocation().getX());
                        config.set("spawn.y", player.getLocation().getY());
                        config.set("spawn.z", player.getLocation().getZ());
                        config.set("spawn.yaw", player.getLocation().getYaw());
                        config.set("spawn.pitch", player.getLocation().getPitch());
                        config.save();

                        util.sendMessageToPlayer(player, "messages.spawn-set");
                    } else {
                        util.sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else {
                    util.sendMessageToSender(sender, "messages.no-player");
                }
            // reload config - /spawn reload
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.reload")) {
                        config.reload();

                        util.sendMessageToPlayer(player, "messages.config-reload");
                    } else {
                        util.sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    config.reload();

                    util.sendMessageToSender(sender, "messages.config-reload");
                }
            // teleport another player to spawn - /spawn %player%
            } else if (target != null) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        if (target.isOnline()) {
                            util.teleportPlayer(target);

                            util.sendPlaceholderMessageToPlayer(player, "messages.teleport-other", "%player%", target.getName());
                        }
                    } else {
                        util.sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    if (target.isOnline()) {
                        util.teleportPlayer(target);

                        util.sendPlaceholderMessageToSender(sender, "messages.teleport-other", "%player%", target.getName());
                    }
                }
            // target player does not exist - /spawn %player%
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        util.sendPlaceholderMessageToPlayer(player, "messages.player-not-found", "%player%", args[0]);
                    } else {
                        util.sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    util.sendPlaceholderMessageToSender(sender, "messages.player-not-found", "%player%", args[0]);
                }
            }
        } else {
            util.sendMessageToSender(sender, "messages.no-player");
        }
        return false;
    }
}
