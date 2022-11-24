package me.rockquiet.spawn.events;

import me.rockquiet.spawn.ConfigManger;
import me.rockquiet.spawn.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnEvent implements Listener {

    private final ConfigManger config = new ConfigManger();
    private final Util util = new Util();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("options.teleport-on-respawn")) {
            if (config.getBoolean("options.ignore-bed-spawn")) {
                if (util.spawnExists()) {
                    event.setRespawnLocation(util.getSpawn());

                    util.spawnEffects(player);

                    util.sendMessageToPlayer(player, "messages.teleport");
                } else {
                    util.sendMessageToPlayer(player, "messages.no-spawn");
                }
            } else if (player.getBedSpawnLocation() == null) {
                if (util.spawnExists()) {
                    event.setRespawnLocation(util.getSpawn());

                    util.spawnEffects(player);

                    util.sendMessageToPlayer(player, "messages.teleport");
                } else {
                    util.sendMessageToPlayer(player, "messages.no-spawn");
                }
            }
        }
    }
}
