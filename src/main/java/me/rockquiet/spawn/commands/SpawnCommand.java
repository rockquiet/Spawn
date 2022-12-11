package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.ConfigManager;
import me.rockquiet.spawn.configuration.MessageManager;
import me.rockquiet.spawn.teleport.SpawnTeleport;
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
    private final MessageManager messageManager;

    private final SpawnTeleport spawnTeleport;
    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.messageManager = new MessageManager(plugin);

        this.spawnTeleport = new SpawnTeleport(plugin);
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
            if (player.hasPermission("spawn.use")) {
                if (player.hasPermission("spawn.bypass.cooldown")) {
                    if (player.hasPermission("spawn.bypass.delay")) {
                        spawnTeleport.teleportPlayer(player);
                    } else {
                        commandDelay.runDelay(player);
                    }
                } else if (!commandCooldown.hasCooldown(playerUUID) || commandCooldown.isCooldownDone(playerUUID)) {
                    commandCooldown.setCooldown(playerUUID, System.currentTimeMillis());

                    commandDelay.runDelay(player);
                } else {
                    messageManager.sendPlaceholderMessageToPlayer(player, "cooldown-left", "%cooldown%", String.valueOf(commandCooldown.cooldownTime() - TimeUnit.MILLISECONDS.toSeconds(commandCooldown.getCooldown(playerUUID))));
                }
            }
        } else if (args.length == 1) {
            Player target = Bukkit.getServer().getPlayerExact(args[0]);
            // save current position as spawn in config - /spawn set
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.set")) {
                        location.set("spawn.world", player.getWorld().getName());
                        location.set("spawn.x", player.getLocation().getX());
                        location.set("spawn.y", player.getLocation().getY());
                        location.set("spawn.z", player.getLocation().getZ());
                        location.set("spawn.yaw", player.getLocation().getYaw());
                        location.set("spawn.pitch", player.getLocation().getPitch());

                        configManager.saveFile(location, "location.yml");

                        configManager.getFile("location.yml");

                        messageManager.sendMessageToPlayer(player, "spawn-set");
                    } else {
                        messageManager.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    messageManager.sendMessageToSender(sender, "no-player");
                }
            // reload config - /spawn reload
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.reload")) {
                        configManager.reloadAllFiles();

                        messageManager.sendMessageToPlayer(player, "reload");
                    } else {
                        messageManager.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    configManager.reloadAllFiles();

                    messageManager.sendMessageToSender(sender, "reload");
                }
            // teleport another player to spawn - /spawn %player%
            } else if (target != null && target.isOnline()) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.others")) {
                        spawnTeleport.teleportPlayer(target);

                        messageManager.sendPlaceholderMessageToPlayer(player, "teleport-other", "%player%", target.getName());
                    } else {
                        messageManager.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    spawnTeleport.teleportPlayer(target);

                    messageManager.sendPlaceholderMessageToSender(sender, "teleport-other", "%player%", target.getName());
                }
            // target player does not exist - /spawn %player%
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.others")) {
                        messageManager.sendPlaceholderMessageToPlayer(player, "player-not-found", "%player%", args[0]);
                    } else {
                        messageManager.sendMessageToPlayer(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    messageManager.sendPlaceholderMessageToSender(sender, "player-not-found", "%player%", args[0]);
                }
            }
        } else {
            messageManager.sendMessageToSender(sender, "no-player");
        }
        return false;
    }
}
