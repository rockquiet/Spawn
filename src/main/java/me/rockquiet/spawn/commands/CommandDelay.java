package me.rockquiet.spawn.commands;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.SpawnHandler;
import me.rockquiet.spawn.configuration.FileManager;
import me.rockquiet.spawn.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandDelay implements Listener {

    private final Map<UUID, BukkitTask> delay = new HashMap<>();

    private final Spawn plugin;
    private final FileManager fileManager;
    private final Messages messageManager;
    private final SpawnHandler spawnHandler;

    public CommandDelay(Spawn plugin,
                        FileManager fileManager,
                        Messages messageManager,
                        SpawnHandler spawnHandler) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.spawnHandler = spawnHandler;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public int getDelayTime() {
        YamlConfiguration config = fileManager.getYamlConfig();

        if (config.getBoolean("teleport-delay.enabled")) {
            return config.getInt("teleport-delay.seconds");
        } else {
            return 0;
        }
    }

    public void runDelay(Player player) {
        if (!spawnHandler.spawnExists()) {
            messageManager.sendMessage(player, "no-spawn");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        if (delay.containsKey(playerUUID)) {
            return;
        }

        int delayTime = getDelayTime();
        if (delayTime <= 0) return;

        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS) && fileManager.getYamlConfig().getBoolean("teleport-delay.blindness")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (delayTime + 1) * 20, 0, false, false));
        }

        delay.put(playerUUID, new BukkitRunnable() {
            int delayRemaining = delayTime;

            @Override
            public void run() {
                if (delayRemaining <= delayTime && delayRemaining >= 1) { // runs until timer reaches 1
                    messageManager.sendMessage(player, "delay-left", "%delay%", String.valueOf(delayRemaining));
                } else if (delayRemaining == 0) { // runs once
                    spawnHandler.teleportPlayer(player);
                    delay.remove(playerUUID);
                    cancel();
                }
                delayRemaining--;
            }
        }.runTaskTimer(plugin, 0, 20));
    }

    private void clearBlindness(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS) || !fileManager.getYamlConfig().getBoolean("teleport-delay.blindness")) {
            return;
        }

        // remove the blindness effect only if the duration is equal to or less than the configured delay time (1.10.x +)
        if (Spawn.getServerVersion().getMinor() >= 10 && player.getPotionEffect(PotionEffectType.BLINDNESS).getDuration() <= (getDelayTime() + 1) * 20) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("spawn.bypass.cancel-on-move") || (event.getFrom().distanceSquared(event.getTo()) < 0.01)) {
            return;
        }

        UUID playerUUID = player.getUniqueId();

        if (!delay.containsKey(playerUUID)) {
            return;
        }

        if (fileManager.getYamlConfig().getBoolean("teleport-delay.cancel-on-move")) {
            delay.get(playerUUID).cancel();
            delay.remove(playerUUID);

            clearBlindness(player);

            messageManager.sendMessage(player, "teleport-canceled");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (delay.containsKey(playerUUID)) {
            delay.get(playerUUID).cancel();
            delay.remove(playerUUID);

            clearBlindness(player);
        }
    }
}
