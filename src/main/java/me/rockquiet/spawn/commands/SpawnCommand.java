package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import me.rockquiet.spawn.teleport.SpawnTeleport;
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

    private final SpawnTeleport spawnTeleport;
    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(FileManager fileManager,
                        Messages messageManager,
                        SpawnTeleport spawnTeleport,
                        CommandCooldown commandCooldown,
                        CommandDelay commandDelay) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnTeleport = spawnTeleport;

        this.commandCooldown = commandCooldown;
        this.commandDelay = commandDelay;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        YamlConfiguration config = fileManager.getConfig();

        // teleport to spawn - /spawn
        if (args.length == 0) {
            if (sender instanceof ConsoleCommandSender) {
                messageManager.sendMessage(sender, "no-player");
                return false;
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();

            if (!player.hasPermission("spawn.use")) {
                messageManager.sendMessage(player, "no-permission");
                return false; // stops here if player does not have permissions
            }
            if (!player.hasPermission("spawn.bypass.world-list") && !spawnTeleport.isEnabledInWorld(player.getWorld())) {
                messageManager.sendMessage(player, "world-disabled");
                return false; // stops here if plugin is disabled in current world
            }

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

        // subcommands
        if (args.length == 1) {
            // save current position as spawn in config - /spawn set
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof ConsoleCommandSender) {
                    messageManager.sendMessage(sender, "no-player");
                    return false;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("spawn.set")) {
                    messageManager.sendMessage(player, "no-permission");
                    return false; // stops here if player does not have permissions
                }
                if (!player.hasPermission("spawn.bypass.world-list") && !spawnTeleport.isEnabledInWorld(player.getWorld())) {
                    messageManager.sendMessage(player, "world-disabled");
                    return false; // stops here if plugin is disabled in current world
                }

                spawnTeleport.setSpawn(player.getLocation());
                messageManager.sendMessage(player, "spawn-set");
                return true;
            }

            // reload all files - /spawn reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player && !sender.hasPermission("spawn.reload")) {
                    messageManager.sendMessage(sender, "no-permission");
                    return false; // stops here if player does not have permissions
                }

                fileManager.reloadAll();
                messageManager.sendMessage(sender, "reload");
                return true;
            }

            // teleport another player to spawn - /spawn %player%
            if (sender instanceof Player && !sender.hasPermission("spawn.others")) {
                messageManager.sendMessage(sender, "no-permission");
                return false; // stops here if player does not have permissions
            }
            Player target = Bukkit.getServer().getPlayerExact(args[0]);
            if (target != null && target.isOnline()) {
                spawnTeleport.teleportPlayer(target);
                messageManager.sendMessage(sender, "teleport-other", "%player%", target.getName());
            } else {
                messageManager.sendMessage(sender, "player-not-found", "%player%", args[0]);
            }
            return true;
        }
        return false;
    }
}
