package me.rockquiet.spawn.listeners;

import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnJoinListener implements Listener {

    private final FileManager fileManager;
    private final SpawnHandler spawnHandler;

    public TeleportOnJoinListener(FileManager fileManager,
                                  SpawnHandler spawnHandler) {
        this.fileManager = fileManager;
        this.spawnHandler = spawnHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        YamlConfiguration config = fileManager.getYamlConfig();
        Player player = event.getPlayer();

        if (player.hasPermission("spawn.bypass.join-teleport") || !config.getBoolean("teleport-on-join.enabled")) {
            return;
        }

        if (!player.hasPermission("spawn.bypass.world-list") && !spawnHandler.isEnabledInWorld(player.getWorld())) {
            return;
        }

        if (player.hasPlayedBefore() && config.getBoolean("teleport-on-join.only-first-join")) {
            return;
        }

        spawnHandler.teleportPlayer(player);
    }
}
