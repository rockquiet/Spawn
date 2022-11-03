package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportOutOfVoidEvent implements Listener {

    @EventHandler
    public void playerInVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (Spawn.getPlugin().getConfig().getBoolean("options.teleport-out-of-void")) {
            if (player.getLocation().getBlockY() <= Spawn.getPlugin().getConfig().getInt("options.void-check-height")) {
                Spawn.getPlugin().teleportPlayer(player);
            }
        }
    }
}
