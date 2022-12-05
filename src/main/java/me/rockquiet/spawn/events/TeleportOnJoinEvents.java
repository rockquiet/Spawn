package me.rockquiet.spawn.events;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.Util;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportOnJoinEvents implements Listener {

    private final ConfigManager configManager;
    private final Util util;

    public TeleportOnJoinEvents(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
        this.util = new Util(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Configuration config = configManager.getFile("config.yml");

        Player player = event.getPlayer();
        if (config.getBoolean("teleport-on-join") || (!player.hasPlayedBefore() && config.getBoolean("teleport-on-first-join"))) {
            util.teleportPlayer(player);
        }
    }
}
