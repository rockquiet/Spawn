package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public void setCooldown(UUID playerUUID, long time) {
        if (time < 1) {
            cooldown.remove(playerUUID);
        } else {
            cooldown.put(playerUUID, time);
        }
    }

    public long getCooldown(UUID playerUUID) {
        return System.currentTimeMillis() - cooldown.get(playerUUID);
    }

    public boolean hasCooldown(UUID playerUUID) {
        return cooldown.containsKey(playerUUID);
    }

    public boolean isCooldownDone(UUID playerUUID) {
        if (!hasCooldown(playerUUID)) {
            return true;
        }
        return TimeUnit.MILLISECONDS.toSeconds(getCooldown(playerUUID)) >= cooldownTime();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (hasCooldown(playerUUID)) {
            cooldown.remove(playerUUID);
        }
    }
}
