package me.rockquiet.spawn.events;

import me.rockquiet.spawn.ConfigManger;
import me.rockquiet.spawn.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnJoinEvents implements Listener {

    private final ConfigManger config = new ConfigManger();
    private final Util util = new Util();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            if (config.getBoolean("options.teleport-on-join")) {
                util.teleportPlayer(player);
            }
        } else if (config.getBoolean("options.teleport-on-first-join")) {
            util.teleportPlayer(player);
        }
    }
}
