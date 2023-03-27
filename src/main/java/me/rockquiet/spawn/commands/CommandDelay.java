package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import me.rockquiet.spawn.teleport.SpawnTeleport;
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
    private final SpawnTeleport spawnTeleport;

    public CommandDelay(Spawn plugin,
                        FileManager fileManager,
                        Messages messageManager,
                        SpawnTeleport spawnTeleport) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnTeleport = spawnTeleport;
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
        UUID playerUUID = player.getUniqueId();
        if (spawnTeleport.spawnExists()) {
            if (!delay.containsKey(playerUUID)) {
                delay.put(playerUUID, new BukkitRunnable() {
                    int delayRemaining = delayTime();

                    @Override
                    public void run() {
                        if (delayRemaining <= delayTime() && delayRemaining >= 1) { // runs until timer reached 1
                            messageManager.sendMessage(player, "delay-left", "%delay%", String.valueOf(delayRemaining));
                        } else if (delayRemaining == 0) { // runs once
                            spawnTeleport.teleportPlayer(player);
                            delay.remove(playerUUID);
                            cancel();
                        }
                        delayRemaining--;
                    }
                }.runTaskTimer(plugin, 0, 20));
            }
        } else {
            messageManager.sendMessage(player, "no-spawn");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (delay.containsKey(event.getPlayer().getUniqueId()) && !player.hasPermission("spawn.bypass.cancel-on-move") && (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ())) {
            YamlConfiguration config = fileManager.getConfig();

            BukkitTask delayTask = delay.get(playerUUID);

            if (delay.containsKey(playerUUID) && config.getBoolean("teleport-delay.cancel-on-move")) {
                delayTask.cancel();
                delay.remove(playerUUID);
                messageManager.sendMessage(player, "teleport-canceled");
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BukkitTask delayTask = delay.get(playerUUID);

        if (delay.containsKey(playerUUID)) {
            delayTask.cancel();
            delay.remove(playerUUID);
        }
    }
}
