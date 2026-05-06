package me.rockquiet.spawn.scheduler.spigot;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.scheduler.PlatformScheduler;
import me.rockquiet.spawn.scheduler.PlatformTask;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

public class SpigotScheduler extends PlatformScheduler {

    private final BukkitScheduler scheduler;

    public SpigotScheduler(Spawn plugin) {
        super(plugin);
        this.scheduler = plugin.getServer().getScheduler();
    }

    private PlatformTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new SpigotTask(scheduler.runTaskTimer(getPlugin(), runnable, delay, period));
    }

    @Override
    public PlatformTask runTimerOnEntity(Entity entity, Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    @Override
    public void runAsync(Runnable runnable) {
        scheduler.runTaskAsynchronously(getPlugin(), runnable);
    }
}
