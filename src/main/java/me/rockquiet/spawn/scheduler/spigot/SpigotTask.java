package me.rockquiet.spawn.scheduler.spigot;

import me.rockquiet.spawn.scheduler.PlatformTask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements PlatformTask {

    private final BukkitTask task;

    public SpigotTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
