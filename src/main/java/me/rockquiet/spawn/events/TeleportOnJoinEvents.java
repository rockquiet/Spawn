package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnJoinEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            if (Spawn.getPlugin().getConfig().getBoolean("options.teleport-on-join")) {
                Spawn.getPlugin().teleportPlayer(player);
            }
        } else if (Spawn.getPlugin().getConfig().getBoolean("options.teleport-on-first-join")) {
            Spawn.getPlugin().teleportPlayer(player);
        }
    }
}
