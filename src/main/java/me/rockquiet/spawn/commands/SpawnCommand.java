package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
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

import java.util.Locale;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {

    private final FileManager fileManager;
    private final Messages messageManager;
    private final SpawnHandler spawnHandler;

    private final CommandCooldown commandCooldown;
    private final CommandDelay commandDelay;

    public SpawnCommand(Spawn plugin, FileManager fileManager, Messages messageManager, SpawnHandler spawnHandler) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnHandler = spawnHandler;

        this.commandCooldown = new CommandCooldown(plugin, fileManager);
        this.commandDelay = new CommandDelay(plugin, fileManager, messageManager, spawnHandler);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // teleport to spawn - /spawn
        if (args.length == 0) {
            return spawn(sender);
        }

        // subcommands
        if (args.length == 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "set":
                    // save current position as spawn in config - /spawn set
                    return setSpawn(sender);
                case "reload":
                    // reload all files - /spawn reload
                    return reload(sender);
                default:
                    // teleport another player to spawn - /spawn %player%
                    if (!args[0].isEmpty()) {
                        return spawnOther(sender, args);
                    }
            }
        }

        return false;
    }

    private boolean spawn(CommandSender sender) {
        if (isConsole(sender)) return false;
        YamlConfiguration config = fileManager.getYamlConfig();

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (hasNoPerms(player, "spawn.use")) return false;
        if (isWorldDisabled(player)) return false;
        if (isProhibitedGameMode(player)) return false;

        if (!player.hasPermission("spawn.bypass.cooldown") && config.getBoolean("teleport-cooldown.enabled") && commandCooldown.getCooldownTime() > 0) {
            if (commandCooldown.isCooldownDone(playerUUID)) {
                commandCooldown.setCooldown(playerUUID);
            } else {
                // stop if player has active cooldown
                messageManager.sendMessage(player, "cooldown-left", "%cooldown%", String.valueOf(commandCooldown.getRemainingCooldown(playerUUID)));
                return false;
            }
        }

        if (!player.hasPermission("spawn.bypass.delay") && config.getBoolean("teleport-delay.enabled") && commandDelay.getDelayTime() > 0) {
            commandDelay.runDelay(player);
        } else {
            spawnHandler.teleportPlayer(player);
        }
        return true;
    }

    private boolean spawnOther(CommandSender sender, String[] args) {
        if (hasNoPerms(sender, "spawn.others")) return false;

        final Player target = Bukkit.getServer().getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            messageManager.sendMessage(sender, "player-not-found", "%player%", args[0]);
            return false;
        }

        // if the player teleports themselves with this command, check their gamemode
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.equals(target) && isProhibitedGameMode(player)) {
                return false;
            }
        }

        spawnHandler.teleportPlayer(target);
        messageManager.sendMessage(sender, "teleport-other", "%player%", target.getName());
        return true;
    }

    private boolean setSpawn(CommandSender sender) {
        if (isConsole(sender)) return false;

        final Player player = (Player) sender;
        if (hasNoPerms(player, "spawn.set")) return false;
        if (isWorldDisabled(player)) return false;

        spawnHandler.setSpawn(player.getLocation(), true);
        messageManager.sendMessage(player, "spawn-set");
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (hasNoPerms(sender, "spawn.reload")) return false;

        fileManager.reloadAll();
        spawnHandler.setSpawn(spawnHandler.loadSpawn(), false);
        messageManager.sendMessage(sender, "reload");
        return true;
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
