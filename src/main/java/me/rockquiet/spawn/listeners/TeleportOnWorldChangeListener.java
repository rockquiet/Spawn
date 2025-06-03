package me.rockquiet.spawn.listeners;

import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnWorldChangeListener implements Listener {

    private final FileManager fileManager;
    private final SpawnHandler spawnHandler;

    public TeleportOnWorldChangeListener(FileManager fileManager,
                                         SpawnHandler spawnHandler) {
        this.fileManager = fileManager;
        this.spawnHandler = spawnHandler;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        onWorldChange(event.getPlayer(), false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        onWorldChange(event.getPlayer(), true);
    }

    private void onWorldChange(Player player, boolean isJoining) {
        YamlConfiguration config = fileManager.getYamlConfig();

        if (player.hasPermission("spawn.bypass.world-change") || !config.getBoolean("teleport-on-world-change.enabled")) {
            return;
        }

        if (!player.hasPermission("spawn.bypass.world-list") && !spawnHandler.isEnabledInWorld(player.getWorld())) {
            return;
        }

        if (player.getWorld().equals(spawnHandler.getSpawn().getWorld())) {
            return;
        }

        if (isJoining && !config.getBoolean("teleport-on-world-change.check-on-join")) {
            return;
        }

        spawnHandler.teleportPlayer(player);
    }
}
