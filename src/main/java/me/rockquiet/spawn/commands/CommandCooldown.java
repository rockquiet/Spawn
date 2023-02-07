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

    public void setCooldown(UUID player, long time) {
        if (time < 1) {
            cooldown.remove(player);
        } else {
            cooldown.put(player, time);
        }
    }

    public Long getCooldown(UUID player) {
        return (System.currentTimeMillis() - cooldown.get(player));
    }

    public boolean hasCooldown(UUID player) {
        return cooldown.containsKey(player);
    }

    public boolean isCooldownDone(UUID player) {
        return TimeUnit.MILLISECONDS.toSeconds(getCooldown(player)) >= cooldownTime();
    }
}
