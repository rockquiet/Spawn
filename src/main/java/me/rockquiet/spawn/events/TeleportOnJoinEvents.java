package me.rockquiet.spawn.events;

import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnJoinEvents implements Listener {

    private final FileManager fileManager;
    private final SpawnTeleport spawnTeleport;

    public TeleportOnJoinEvents(FileManager fileManager,
                                SpawnTeleport spawnTeleport) {
        this.fileManager = fileManager;
        this.spawnTeleport = spawnTeleport;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        YamlConfiguration config = fileManager.getConfig();

        Player player = event.getPlayer();
        if ((config.getBoolean("teleport-on-join.enabled") && !config.getBoolean("teleport-on-join.only-first-join")) || (!player.hasPlayedBefore() && config.getBoolean("teleport-on-join.enabled") && config.getBoolean("teleport-on-join.only-first-join"))) {
            spawnTeleport.teleportPlayer(player);
        }
    }
}
