package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final Util util;
    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.util = new Util(plugin);
        this.commandCooldown = new CommandCooldown(plugin);
        this.commandDelay = new CommandDelay(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final YamlConfiguration location = configManager.getFile("location.yml");

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
                        util.sendPlaceholderMessageToPlayer(player, "cooldown-left", "%cooldown%", String.valueOf(commandCooldown.cooldownTime() - TimeUnit.MILLISECONDS.toSeconds(commandCooldown.getCooldown(playerUUID))));
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
                        location.set("spawn.world", player.getWorld().getName());
                        location.set("spawn.x", player.getLocation().getX());
                        location.set("spawn.y", player.getLocation().getY());
                        location.set("spawn.z", player.getLocation().getZ());
                        location.set("spawn.yaw", player.getLocation().getYaw());
                        location.set("spawn.pitch", player.getLocation().getPitch());
                        // config.save();
                        configManager.saveFile(location, "location.yml");

                        configManager.getFile("location.yml");

                        util.sendMessageToPlayer(player, "spawn-set");
                    } else {
                        util.sendMessageToPlayer(player, "no-permission");
                    }
                } else {
                    util.sendMessageToSender(sender, "no-player");
                }
            // reload config - /spawn reload
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.reload")) {
                        configManager.getFile("config.yml");
                        configManager.getFile("location.yml");
                        configManager.getFile("languages/messages-en.yml");
                        configManager.getFile("languages/messages-de.yml");
                        configManager.getFile("languages/messages-custom.yml");

                        util.sendMessageToPlayer(player, "reload");
                    } else {
                        util.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    configManager.getFile("config.yml");
                    configManager.getFile("location.yml");
                    configManager.getFile("languages/messages-en.yml");
                    configManager.getFile("languages/messages-de.yml");
                    configManager.getFile("languages/messages-custom.yml");

                    util.sendMessageToSender(sender, "reload");
                }
            // teleport another player to spawn - /spawn %player%
            } else if (target != null) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        if (target.isOnline()) {
                            util.teleportPlayer(target);

                            util.sendPlaceholderMessageToPlayer(player, "teleport-other", "%player%", target.getName());
                        }
                    } else {
                        util.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    if (target.isOnline()) {
                        util.teleportPlayer(target);

                        util.sendPlaceholderMessageToSender(sender, "teleport-other", "%player%", target.getName());
                    }
                }
            // target player does not exist - /spawn %player%
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp() || player.hasPermission("spawn.others")) {
                        util.sendPlaceholderMessageToPlayer(player, "player-not-found", "%player%", args[0]);
                    } else {
                        util.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    util.sendPlaceholderMessageToSender(sender, "player-not-found", "%player%", args[0]);
                }
            }
        } else {
            util.sendMessageToSender(sender, "no-player");
        }
        return false;
    }
}
