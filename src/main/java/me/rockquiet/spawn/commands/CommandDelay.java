package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class CommandDelay implements Listener {

    private final Map<Player, BukkitTask> delay = new HashMap<>();

    public int delayTime() {
        if (Spawn.getPlugin().getConfig().getInt("options.teleport-delay") >= 1) {
            return Spawn.getPlugin().getConfig().getInt("options.teleport-delay");
        } else
            return 0;
    }

    public void runDelay(Player player) {
        if (!delay.containsKey(player)) {

            Location pos = player.getLocation();

            delay.put(player, new BukkitRunnable() {
                int delayRemaining = delayTime();

                @Override
                public void run() {
                    if (delayRemaining <= delayTime() && delayRemaining >= 1) { // runs until timer reached 1
                        if (Spawn.getPlugin().getConfig().getBoolean("options.cancel-on-move")) {
                            if (player.isOp() || player.hasPermission("spawn.bypass.cancel-on-move")) {
                                Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.delay-left", "%delay%", String.valueOf(delayRemaining));
                            } else {
                                if (player.getLocation().getBlockX() == pos.getBlockX() && player.getLocation().getBlockY() == pos.getBlockY() && player.getLocation().getBlockZ() == pos.getBlockZ()) {
                                    Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.delay-left", "%delay%", String.valueOf(delayRemaining));
                                } else {
                                    cancel();
                                    delay.remove(player);
                                    Spawn.getPlugin().sendMessageToPlayer(player, "messages.teleport-canceled");
                                }
                            }
                        } else {
                            Spawn.getPlugin().sendPlaceholderMessageToPlayer(player, "messages.delay-left", "%delay%", String.valueOf(delayRemaining));
                        }
                    } else if (delayRemaining == 0) { // runs once
                        if (Spawn.getPlugin().getConfig().getBoolean("options.cancel-on-move")) {
                            if (player.isOp() || player.hasPermission("spawn.bypass.cancel-on-move")) {
                                delay.remove(player);
                                Spawn.getPlugin().teleportPlayer(player);
                            } else {
                                if (player.getLocation().getBlockX() == pos.getBlockX() && player.getLocation().getBlockY() == pos.getBlockY() && player.getLocation().getBlockZ() == pos.getBlockZ()) {
                                    delay.remove(player);
                                    Spawn.getPlugin().teleportPlayer(player);
                                } else {
                                    cancel();
                                    delay.remove(player);
                                    Spawn.getPlugin().sendMessageToPlayer(player, "messages.teleport-canceled");
                                }
                            }
                        } else {
                            delay.remove(player);
                            Spawn.getPlugin().teleportPlayer(player);
                        }
                    }
                    delayRemaining--;
                }
            }.runTaskTimer(Spawn.getPlugin(), 0, 20));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitTask delayTask = delay.get(player);

        if (delayTask != null && delay.containsKey(player)) {
            delayTask.cancel();
            delay.remove(player);
        }
    }
}
