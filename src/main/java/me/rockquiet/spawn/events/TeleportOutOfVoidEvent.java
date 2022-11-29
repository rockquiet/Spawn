package me.rockquiet.spawn.events;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportOutOfVoidEvent implements Listener {

    private final ConfigManager config = new ConfigManager();
    private final Util util = new Util();

    @EventHandler
    public void playerInVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("options.teleport-out-of-void")) {
            if (player.getLocation().getBlockY() <= config.getInt("options.void-check-height")) {
                util.teleportPlayer(player);
            }
        }
    }
}
