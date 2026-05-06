package me.rockquiet.spawn.scheduler.paper;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.scheduler.PlatformScheduler;
import me.rockquiet.spawn.scheduler.PlatformTask;
import org.bukkit.entity.Entity;

public class PaperScheduler extends PlatformScheduler {

    private final AsyncScheduler asyncScheduler;

    public PaperScheduler(Spawn plugin) {
        super(plugin);
        this.asyncScheduler = plugin.getServer().getAsyncScheduler();
    }

    @Override
    public PlatformTask runTimerOnEntity(Entity entity, Runnable runnable, long delay, long period) {
        long finalDelay = Math.max(delay, 1L);
        return new PaperTask(entity.getScheduler().runAtFixedRate(getPlugin(), scheduledTask -> runnable.run(), null, finalDelay, period));
    }

    @Override
    public void runAsync(Runnable runnable) {
        asyncScheduler.runNow(getPlugin(), scheduledTask -> runnable.run());
    }
}