package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class CommandCooldown implements Listener {

    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private final FileManager fileManager;

    public CommandCooldown(Spawn plugin, FileManager fileManager) {
        this.fileManager = fileManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public int getCooldownTime() {
        YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("teleport-cooldown.enabled")) {
            return config.getInt("teleport-cooldown.seconds");
        } else {
            return 0;
        }
    }

    public void setCooldown(UUID playerUUID) {
        cooldown.put(playerUUID, Instant.now().getEpochSecond());
    }

    public long getCooldown(UUID playerUUID) {
        return Instant.now().getEpochSecond() - cooldown.get(playerUUID);
    }

    public long getRemainingCooldown(UUID playerUUID) {
        return getCooldownTime() - getCooldown(playerUUID);
    }

    public boolean isCooldownDone(UUID playerUUID) {
        if (!cooldown.containsKey(playerUUID)) {
            return true;
        }
        return getCooldown(playerUUID) >= getCooldownTime();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        cooldown.remove(event.getPlayer().getUniqueId());
    }
}
