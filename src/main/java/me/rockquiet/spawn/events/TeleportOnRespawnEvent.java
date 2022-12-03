package me.rockquiet.spawn.events;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.Util;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnEvent implements Listener {

    private final ConfigManager configManager;
    private final Util util;

    public TeleportOnRespawnEvent(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.util = new Util(plugin);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Configuration config = configManager.getFile("config.yml");

        Player player = event.getPlayer();

        if (config.getBoolean("teleport-on-respawn")) {
            if (config.getBoolean("ignore-bed-spawn")) {
                if (util.spawnExists()) {
                    event.setRespawnLocation(util.getSpawn());

                    util.spawnEffects(player);

                    util.sendMessageToPlayer(player, "teleport");
                } else {
                    util.sendMessageToPlayer(player, "no-spawn");
                }
            } else if (player.getBedSpawnLocation() == null) {
                if (util.spawnExists()) {
                    event.setRespawnLocation(util.getSpawn());

                    util.spawnEffects(player);

                    util.sendMessageToPlayer(player, "teleport");
                } else {
                    util.sendMessageToPlayer(player, "no-spawn");
                }
            }
        }
    }
}
