package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.ConfigManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandCooldown {

    private final ConfigManager config = new ConfigManager();

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public int cooldownTime() {
        if (config.getInt("options.teleport-cooldown") >= 1) {
            return config.getInt("options.teleport-cooldown");
        } else
            return 0;
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
