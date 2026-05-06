package me.rockquiet.spawn.scheduler;

import me.rockquiet.spawn.Spawn;
import me.rockquiet.spawn.scheduler.folia.FoliaScheduler;
import me.rockquiet.spawn.scheduler.paper.PaperScheduler;
import me.rockquiet.spawn.scheduler.spigot.SpigotScheduler;
import org.bukkit.entity.Entity;

public abstract class PlatformScheduler {

    private final Spawn plugin;

    protected PlatformScheduler(Spawn plugin) {
        this.plugin = plugin;
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static PlatformScheduler createPlatformScheduler(Spawn plugin) {
        final boolean isRegionSchedulerAvailable = hasClass("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
        if (hasClass("io.papermc.paper.threadedregions.RegionizedServer") && isRegionSchedulerAvailable) {
            return new FoliaScheduler(plugin);
        } else if (plugin.isPaper() && isRegionSchedulerAvailable) {
            return new PaperScheduler(plugin);
        } else {
            return new SpigotScheduler(plugin);
        }
    }

    public Spawn getPlugin() {
        return plugin;
    }

    public abstract PlatformTask runTimerOnEntity(Entity entity, Runnable runnable, long delay, long period);

    public abstract void runAsync(Runnable runnable);
}

