package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.configuration.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandCooldown {

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

    public Long getCooldown(UUID playerUUID) {
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
}
