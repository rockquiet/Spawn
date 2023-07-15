package me.rockquiet.spawn.listeners;

import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportOutOfVoidListener implements Listener {

    private final FileManager fileManager;
    private final SpawnHandler spawnHandler;

    public TeleportOutOfVoidListener(FileManager fileManager,
                                     SpawnHandler spawnHandler) {
        this.fileManager = fileManager;
        this.spawnHandler = spawnHandler;
    }

    @EventHandler
    public void playerInVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("spawn.bypass.void-teleport") || event.getFrom().getY() == event.getTo().getY() || (event.getFrom().distanceSquared(event.getTo()) < 0.01)) {
            return;
        }

        if (!player.hasPermission("spawn.bypass.world-list") && !spawnHandler.isEnabledInWorld(player.getWorld())) {
            return;
        }

        YamlConfiguration config = fileManager.getConfig();

        if (config.getBoolean("teleport-out-of-void.enabled") && (player.getLocation().getBlockY() <= config.getInt("teleport-out-of-void.check-height"))) {
            spawnHandler.teleportPlayer(player);
        }
    }
}
