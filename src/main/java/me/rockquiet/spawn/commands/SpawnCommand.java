package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.configuration.FileManager;
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

    private final FileManager fileManager;
    private final MessageManager messageManager;

    private final SpawnTeleport spawnTeleport;
    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(FileManager fileManager,
                        MessageManager messageManager,
                        SpawnTeleport spawnTeleport,
                        CommandCooldown commandCooldown,
                        CommandDelay commandDelay) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnTeleport = spawnTeleport;

        this.spawnTeleport = new SpawnTeleport(plugin);
        this.commandCooldown = new CommandCooldown(plugin);
        this.commandDelay = new CommandDelay(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        YamlConfiguration config = fileManager.getConfig();
        YamlConfiguration location = fileManager.getLocation();

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            // teleport to spawn - /spawn
            if (player.hasPermission("spawn.use")) {
                if (player.hasPermission("spawn.bypass.cooldown") || !config.getBoolean("teleport-cooldown.enabled")) {
                    if (player.hasPermission("spawn.bypass.delay") || !config.getBoolean("teleport-delay.enabled")) {
                        spawnTeleport.teleportPlayer(player);
                    } else {
                        commandDelay.runDelay(player);
                    }
                } else if (!commandCooldown.hasCooldown(playerUUID) || commandCooldown.isCooldownDone(playerUUID)) {
                    commandCooldown.setCooldown(playerUUID, System.currentTimeMillis());

                    commandDelay.runDelay(player);
                } else {
                    messageManager.sendMessage(player, "cooldown-left", "%cooldown%", String.valueOf(commandCooldown.cooldownTime() - TimeUnit.MILLISECONDS.toSeconds(commandCooldown.getCooldown(playerUUID))));
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

                        fileManager.save(location, "location.yml");

                        fileManager.reloadLocation();

                        messageManager.sendMessage(player, "spawn-set");
                    } else {
                        messageManager.sendMessage(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    messageManager.sendMessage(sender, "no-player");
                }
                // reload config - /spawn reload
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.reload")) {
                        fileManager.reloadAll();

                        messageManager.sendMessage(player, "reload");
                    } else {
                        messageManager.sendMessage(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    fileManager.reloadAll();

                    messageManager.sendMessage(sender, "reload");
                }
                // teleport another player to spawn - /spawn %player%
            } else if (target != null && target.isOnline()) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.others")) {
                        spawnTeleport.teleportPlayer(target);

                        messageManager.sendMessage(player, "teleport-other", "%player%", target.getName());
                    } else {
                        messageManager.sendMessage(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    spawnTeleport.teleportPlayer(target);

                    messageManager.sendMessage(sender, "teleport-other", "%player%", target.getName());
                }
                // target player does not exist - /spawn %player%
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("spawn.others")) {
                        messageManager.sendMessage(player, "player-not-found", "%player%", args[0]);
                    } else {
                        messageManager.sendMessage(player, "no-permission");
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    messageManager.sendMessage(sender, "player-not-found", "%player%", args[0]);
                }
            }
        } else {
            messageManager.sendMessage(sender, "no-player");
        }
        return false;
    }
}
