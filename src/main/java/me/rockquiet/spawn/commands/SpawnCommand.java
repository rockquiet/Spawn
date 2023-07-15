package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand implements CommandExecutor {

    private final FileManager fileManager;
    private final Messages messageManager;

    private final SpawnHandler spawnHandler;
    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(FileManager fileManager,
                        Messages messageManager,
                        SpawnHandler spawnHandler,
                        CommandCooldown commandCooldown,
                        CommandDelay commandDelay) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnHandler = spawnHandler;

        this.commandCooldown = commandCooldown;
        this.commandDelay = commandDelay;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        YamlConfiguration config = fileManager.getConfig();

        // teleport to spawn - /spawn
        if (args.length == 0) {
            if (isConsole(sender)) return false; // stop if sender is console

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();

            if (hasNoPerms(player, "spawn.use")) return false; // stop if player does not have the permission
            if (isWorldDisabled(player)) return false; // stop if plugin is disabled in current world
            if (isProhibitedGameMode(player)) return false; // stop if the player is in wrong gamemode

            if (player.hasPermission("spawn.bypass.cooldown") || !config.getBoolean("teleport-cooldown.enabled")) {
                if (player.hasPermission("spawn.bypass.delay") || !config.getBoolean("teleport-delay.enabled")) {
                    spawnHandler.teleportPlayer(player);
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

        // subcommands
        if (args.length == 1) {
            // save current position as spawn in config - /spawn set
            if (args[0].equalsIgnoreCase("set")) {
                if (isConsole(sender)) return false; // stop if sender is console

                Player player = (Player) sender;
                if (hasNoPerms(player, "spawn.set")) return false; // stop if player does not have the permission
                if (isWorldDisabled(player)) return false; // stop if plugin is disabled in current world

                spawnHandler.setSpawn(player.getLocation());
                messageManager.sendMessage(player, "spawn-set");
                return true;
            }

            // reload all files - /spawn reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (hasNoPerms(sender, "spawn.reload")) return false; // stop if player does not have the permission

                fileManager.reloadAll();
                messageManager.sendMessage(sender, "reload");
                return true;
            }

            // teleport another player to spawn - /spawn %player%
            if (hasNoPerms(sender, "spawn.others")) return false; // stop if player does not have the permission

            Player target = Bukkit.getServer().getPlayerExact(args[0]);
            if (target != null && target.isOnline()) {
                // if the player teleports themselves with this command, check their gamemode
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.equals(target) && isProhibitedGameMode(player)) {
                        return false; // stop if the player is in wrong gamemode
                    }
                }

                spawnHandler.teleportPlayer(target);
                messageManager.sendMessage(sender, "teleport-other", "%player%", target.getName());
            } else {
                messageManager.sendMessage(sender, "player-not-found", "%player%", args[0]);
            }
            return true;
        }
        return false;
    }

    private boolean isConsole(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            messageManager.sendMessage(sender, "no-player");
            return true;
        }
        return false;
    }

    private boolean hasNoPerms(CommandSender sender, String permission) {
        if (sender instanceof Player && !sender.hasPermission(permission)) {
            messageManager.sendMessage(sender, "no-permission");
            return true;
        }
        return false;
    }

    private boolean isWorldDisabled(Player player) {
        if (!player.hasPermission("spawn.bypass.world-list") && !spawnHandler.isEnabledInWorld(player.getWorld())) {
            messageManager.sendMessage(player, "world-disabled");
            return true;
        }
        return false;
    }

    private boolean isProhibitedGameMode(Player player) {
        if (!spawnHandler.isAllowedGameMode(player)) {
            messageManager.sendMessage(player, "gamemode-restricted", "%gamemode%", player.getGameMode().toString());
            return true;
        }
        return false;
    }
}
