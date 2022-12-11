package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.ConfigManager;
import me.rockquiet.spawn.configuration.MessageManager;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnEvent implements Listener {

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final SpawnTeleport spawnTeleport;

    public TeleportOnRespawnEvent(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.messageManager = new MessageManager(plugin);
        this.spawnTeleport = new SpawnTeleport(plugin);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Configuration config = configManager.getFile("config.yml");

        Player player = event.getPlayer();

        if (config.getBoolean("teleport-on-respawn")) {
            if (spawnTeleport.spawnExists()) {
                if (player.getBedSpawnLocation() == null || (config.getBoolean("ignore-bed-spawn") && event.isBedSpawn()) || (config.getBoolean("ignore-anchor-spawn") && event.isAnchorSpawn())) {
                    event.setRespawnLocation(spawnTeleport.getSpawn());

                    spawnTeleport.spawnEffects(player);

                    messageManager.sendMessageToPlayer(player, "teleport");
                }
            } else {
                messageManager.sendMessageToPlayer(player, "no-spawn");
            }
        }
    }
}
