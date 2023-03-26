package me.rockquiet.spawn.events;

import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnEvent implements Listener {

    private final FileManager fileManager;
    private final Messages messageManager;
    private final SpawnTeleport spawnTeleport;

    public TeleportOnRespawnEvent(FileManager fileManager,
                                  Messages messageManager,
                                  SpawnTeleport spawnTeleport) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnTeleport = spawnTeleport;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        YamlConfiguration config = fileManager.getConfig();

        Player player = event.getPlayer();

        if (config.getBoolean("teleport-on-respawn.enabled")) {
            if (spawnTeleport.spawnExists()) {
                if ((player.hasPermission("spawn.bypass.world-list") || spawnTeleport.isEnabledInWorld(player.getWorld()))
                        && player.getBedSpawnLocation() == null
                        || (config.getBoolean("teleport-on-respawn.ignore-bed-spawn") && event.isBedSpawn())
                        || (config.getBoolean("teleport-on-respawn.ignore-anchor-spawn") && event.isAnchorSpawn())
                ) {
                    event.setRespawnLocation(spawnTeleport.getSpawn());

                    spawnTeleport.spawnEffects(player);

                    messageManager.sendMessage(player, "teleport");
                }
            } else {
                messageManager.sendMessage(player, "no-spawn");
            }
        }
    }
}
