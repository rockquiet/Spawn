package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand implements CommandExecutor {

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
                        Spawn.getPlugin().teleportPlayer(player);
                    } else {
                        commandDelay.runDelay(player);
                    }
                } else {
                    if (!commandCooldown.hasCooldown(playerUUID) || commandCooldown.isCooldownDone(playerUUID)) {
                        commandCooldown.setCooldown(playerUUID, System.currentTimeMillis());

                        commandDelay.runDelay(player);
                    } else {
                        Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.cooldown-left", "%cooldown%", String.valueOf(commandCooldown.cooldownTime() - TimeUnit.MILLISECONDS.toSeconds(commandCooldown.getCooldown(playerUUID))));
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
                } else {
                    Spawn.getPlugin().sendMessageToSender(sender, "messages.no-player");
                }
            // reload config - /spawn reload
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.reload")) {
                        Spawn.getPlugin().reloadConfig();

                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.config-reload");
                    } else {
                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    Spawn.getPlugin().reloadConfig();

                    Spawn.getPlugin().sendMessageToSender(sender, "messages.config-reload");
                }
            // teleport another player to spawn - /spawn %player%
            } else if (target != null) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        if (target.isOnline()) {
                            Spawn.getPlugin().teleportPlayer(target);

                            Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.teleport-other", "%player%", target.getName());
                        }
                    } else {
                        Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    if (target.isOnline()) {
                        Spawn.getPlugin().teleportPlayer(target);

                        Spawn.getPlugin().sendPlaceholderMessageToSender(sender, "messages.teleport-other", "%player%", target.getName());
                    }
                }
            } else {
                Spawn.getPlugin().sendPlaceholderMessageToSender(sender, "messages.player-not-found", "%player%", args[0]);
            }
        } else {
            Spawn.getPlugin().sendMessageToSender(sender, "messages.no-player");
        }
        return false;
    }
}
