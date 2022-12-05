package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.Util;
import org.bukkit.configuration.Configuration;
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

    private final Spawn plugin;
    private final ConfigManager configManager;
    private final Util util;

    public CommandDelay(Spawn plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
        this.util = new Util(plugin);
    }

    private static final Map<UUID, BukkitTask> delay = new HashMap<>();

    public int delayTime() {
        final Configuration config = configManager.getFile("config.yml");

        if (config.getInt("teleport-delay") >= 1) {
            return config.getInt("teleport-delay");
        } else
            return 0;
    }

    public void runDelay(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (util.spawnExists()) {
            if (!delay.containsKey(playerUUID)) {
                delay.put(playerUUID, new BukkitRunnable() {
                    int delayRemaining = delayTime();
                    @Override
                    public void run() {
                        if (delayRemaining <= delayTime() && delayRemaining >= 1) { // runs until timer reached 1
                            util.sendPlaceholderMessageToPlayer(player, "delay-left", "%delay%", String.valueOf(delayRemaining));
                        } else if (delayRemaining == 0) { // runs once
                            util.teleportPlayer(player);
                            delay.remove(playerUUID);
                            cancel();
                        }
                        delayRemaining--;
                    }
                }.runTaskTimer(plugin, 0, 20));
            }
        } else {
            util.sendMessageToPlayer(player, "no-spawn");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Configuration config = configManager.getFile("config.yml");

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BukkitTask delayTask = delay.get(playerUUID);

        if (delay.containsKey(playerUUID) && config.getBoolean("cancel-on-move") && !player.hasPermission("spawn.bypass.cancel-on-move")) {
            delayTask.cancel();
            delay.remove(playerUUID);
            util.sendMessageToPlayer(player, "teleport-canceled");
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
