package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
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

    public int delayTime() {
        if (Spawn.getPlugin().getConfig().getInt("options.teleport-delay") >= 1) {
            return Spawn.getPlugin().getConfig().getInt("options.teleport-delay");
        } else
            return 0;
    }

    public void runDelay(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (Spawn.getPlugin().checkSpawnLocation()) {
            if (!delay.containsKey(playerUUID)) {
                delay.put(playerUUID, new BukkitRunnable() {
                    int delayRemaining = delayTime();
                    @Override
                    public void run() {
                        if (delayRemaining <= delayTime() && delayRemaining >= 1) { // runs until timer reached 1
                            Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.delay-left", "%delay%", String.valueOf(delayRemaining));
                        } else if (delayRemaining == 0) { // runs once
                            Spawn.getPlugin().teleportPlayer(player);
                            delay.remove(playerUUID);
                            cancel();
                        }
                        delayRemaining--;
                    }
                }.runTaskTimer(Spawn.getPlugin(), 0, 20));
            }
        } else {
            Spawn.getPlugin().sendMessageToPlayer(player, "messages.no-spawn");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BukkitTask delayTask = delay.get(playerUUID);

        if (Spawn.getPlugin().getConfig().getBoolean("options.cancel-on-move")) {
            if (!player.isOp() || !player.hasPermission("spawn.bypass.cancel-on-move")) {
                if (delay.containsKey(playerUUID)) {
                    delayTask.cancel();
                    delay.remove(playerUUID);
                    Spawn.getPlugin().sendMessageToPlayer(player, "messages.teleport-canceled");
                }
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
