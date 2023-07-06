package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandDelay implements Listener {

    private static final Map<UUID, BukkitTask> delay = new HashMap<>();
    private final Spawn plugin;
    private final FileManager fileManager;
    private final Messages messageManager;
    private final SpawnHandler spawnHandler;

    public CommandDelay(Spawn plugin,
                        FileManager fileManager,
                        Messages messageManager,
                        SpawnHandler spawnHandler) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnHandler = spawnHandler;
    }

    public int delayTime() {
        YamlConfiguration config = fileManager.getConfig();

        if (config.getBoolean("teleport-delay.enabled")) {
            return config.getInt("teleport-delay.seconds");
        } else {
            return 0;
        }
    }

    public void runDelay(Player player) {
        if (!spawnHandler.spawnExists()) {
            messageManager.sendMessage(player, "no-spawn");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        if (!delay.containsKey(playerUUID)) {
            delay.put(playerUUID, new BukkitRunnable() {
                int delayRemaining = delayTime();

                @Override
                public void run() {
                    if (delayRemaining <= delayTime() && delayRemaining >= 1) { // runs until timer reached 1
                        messageManager.sendMessage(player, "delay-left", "%delay%", String.valueOf(delayRemaining));
                    } else if (delayRemaining == 0) { // runs once
                        spawnHandler.teleportPlayer(player);
                        delay.remove(playerUUID);
                        cancel();
                    }
                    delayRemaining--;
                }
            }.runTaskTimer(plugin, 0, 20));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("spawn.bypass.cancel-on-move") && (event.getFrom().distanceSquared(event.getTo()) < 0.01)) {
            return;
        }

        UUID playerUUID = player.getUniqueId();

        if (!delay.containsKey(playerUUID)) {
            return;
        }

        YamlConfiguration config = fileManager.getConfig();

        if (config.getBoolean("teleport-delay.cancel-on-move")) {
            delay.get(playerUUID).cancel();
            delay.remove(playerUUID);
            messageManager.sendMessage(player, "teleport-canceled");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (delay.containsKey(playerUUID)) {
            delay.get(playerUUID).cancel();
            delay.remove(playerUUID);
        }
    }
}
