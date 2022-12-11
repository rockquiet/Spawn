package me.rockquiet.spawn.events;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.ConfigManager;
import me.rockquiet.spawn.teleport.SpawnTeleport;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportOutOfVoidEvent implements Listener {

    private final ConfigManager configManager;
    private final SpawnTeleport spawnTeleport;

    public TeleportOutOfVoidEvent(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.spawnTeleport = new SpawnTeleport(plugin);
    }

    @EventHandler
    public void playerInVoid(PlayerMoveEvent event) {
        final Configuration config = configManager.getFile("config.yml");

        Player player = event.getPlayer();

        if (config.getBoolean("teleport-out-of-void") && (player.getLocation().getBlockY() <= config.getInt("void-check-height"))) {
            spawnTeleport.teleportPlayer(player);
        }
    }
}
