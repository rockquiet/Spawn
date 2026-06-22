package me.rockquiet.spawn.listeners;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import me.rockquiet.spawn.updater.Version;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportOnRespawnListener implements Listener {

    private static final Version ANCHOR_ADDED_VERSION = new Version(1, 16, 0);

    private final FileManager fileManager;
    private final Messages messageManager;
    private final SpawnHandler spawnHandler;

    public TeleportOnRespawnListener(FileManager fileManager,
                                     Messages messageManager,
                                     SpawnHandler spawnHandler) {
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnHandler = spawnHandler;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        YamlConfiguration config = fileManager.getYamlConfig();
        Player player = event.getPlayer();

        if (player.hasPermission("spawn.bypass.respawn-teleport") || !config.getBoolean("teleport-on-respawn.enabled")) {
            return;
        }

        if (!player.hasPermission("spawn.bypass.world-list") && !spawnHandler.isEnabledInWorld(player.getWorld())) {
            return;
        }

        if (event.isBedSpawn() && player.getBedSpawnLocation() != null && !config.getBoolean("teleport-on-respawn.ignore-bed-spawn")) {
            return;
        }

        if (Spawn.getServerVersion().compareTo(ANCHOR_ADDED_VERSION) >= 0 && event.isAnchorSpawn() && !config.getBoolean("teleport-on-respawn.ignore-anchor-spawn")) {
            return;
        }

        if (!spawnHandler.spawnExists()) {
            messageManager.sendMessage(player, "no-spawn");
            return;
        }

        event.setRespawnLocation(spawnHandler.getSpawn());

        spawnHandler.spawnParticles(player);
        spawnHandler.playSound(player);

        messageManager.sendMessage(player, "teleport");
    }
}
