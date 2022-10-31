package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnEvent implements Listener {

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (Spawn.getPlugin().getConfig().getBoolean("options.teleport-on-respawn")) {
            if (Spawn.getPlugin().getConfig().getBoolean("options.ignore-bed-spawn")) {
                if (Spawn.getPlugin().getConfig().getString("spawn.world") != null && Spawn.getPlugin().getConfig().getString("spawn.x") != null && Spawn.getPlugin().getConfig().getString("spawn.y") != null && Spawn.getPlugin().getConfig().getString("spawn.z") != null && Spawn.getPlugin().getConfig().getString("spawn.yaw") != null && Spawn.getPlugin().getConfig().getString("spawn.pitch") != null) {
                    event.setRespawnLocation(Spawn.getPlugin().getSpawn());
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Spawn.getPlugin().getConfig().getString("messages.no-spawn")));
                }
                Spawn.getPlugin().teleportMessage(player);

            } else if (player.getBedSpawnLocation() == null) {
                if (Spawn.getPlugin().getConfig().getString("spawn.world") != null && Spawn.getPlugin().getConfig().getString("spawn.x") != null && Spawn.getPlugin().getConfig().getString("spawn.y") != null && Spawn.getPlugin().getConfig().getString("spawn.z") != null && Spawn.getPlugin().getConfig().getString("spawn.yaw") != null && Spawn.getPlugin().getConfig().getString("spawn.pitch") != null) {
                    event.setRespawnLocation(Spawn.getPlugin().getSpawn());
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Spawn.getPlugin().getConfig().getString("messages.no-spawn")));
                }
                Spawn.getPlugin().teleportMessage(player);
            }
        }
    }
}
