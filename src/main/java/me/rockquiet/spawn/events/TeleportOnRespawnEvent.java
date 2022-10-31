package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
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
                event.setRespawnLocation(Spawn.getPlugin().getSpawn());

                Spawn.getPlugin().teleportMessage(player);

            } else if (player.getBedSpawnLocation() == null) {
                event.setRespawnLocation(Spawn.getPlugin().getSpawn());

                Spawn.getPlugin().teleportMessage(player);
            }
        }
    }
}
