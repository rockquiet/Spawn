package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class CommandCooldown implements Listener {

    private final FileManager fileManager;
    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public CommandCooldown(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public int cooldownTime() {
        YamlConfiguration config = fileManager.getConfig();

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
        return cooldownTime() - getCooldown(playerUUID);
    }

    public boolean hasCooldown(UUID playerUUID) {
        return cooldown.containsKey(playerUUID);
    }

    public boolean isCooldownDone(UUID playerUUID) {
        if (!hasCooldown(playerUUID)) {
            return true;
        }
        return getCooldown(playerUUID) >= cooldownTime();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (hasCooldown(playerUUID)) {
            cooldown.remove(playerUUID);
        }
    }
}
