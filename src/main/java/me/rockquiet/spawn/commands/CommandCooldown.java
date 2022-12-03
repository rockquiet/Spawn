package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.ConfigManager;
import me.rockquiet.spawn.Spawn;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandCooldown {

    private final ConfigManager configManager;

    public CommandCooldown(Spawn plugin) {
        this.configManager = new ConfigManager(plugin);
    }

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public int cooldownTime() {
        final Configuration config = configManager.getFile("config.yml");

        if (config.getInt("teleport-cooldown") >= 1) {
            return config.getInt("teleport-cooldown");
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
