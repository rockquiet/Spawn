package me.rockquiet.spawn.events;

import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportOutOfVoidEvent implements Listener {

    private final FileManager fileManager;
    private final SpawnTeleport spawnTeleport;

    public TeleportOutOfVoidEvent(FileManager fileManager,
                                  SpawnTeleport spawnTeleport) {
        this.fileManager = fileManager;
        this.spawnTeleport = spawnTeleport;
    }

    @EventHandler
    public void playerInVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("spawn.bypass.void-teleport") && (event.getFrom().getY() != event.getTo().getY()) && (event.getFrom().distance(event.getTo()) > 0.1)) {
            YamlConfiguration config = fileManager.getConfig();

            if ((player.hasPermission("spawn.bypass.world-list") || spawnTeleport.isEnabledInWorld(player.getWorld()))
                    && config.getBoolean("teleport-out-of-void.enabled")
                    && (player.getLocation().getBlockY() <= config.getInt("teleport-out-of-void.check-height"))
            ) {
                spawnTeleport.teleportPlayer(player);
            }
        }
    }
}
