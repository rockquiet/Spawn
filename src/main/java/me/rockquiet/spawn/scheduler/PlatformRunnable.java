package me.rockquiet.spawn.scheduler;

import me.rockquiet.spawn.Spawn;
import org.bukkit.entity.Entity;

public abstract class PlatformRunnable implements Runnable {

    private PlatformTask task;

    public synchronized void cancel() throws IllegalStateException {
        checkScheduled();
        task.cancel();
    }

    public synchronized PlatformTask runTaskTimer(Spawn plugin, Entity entity, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(plugin.getScheduler().runTimerOnEntity(entity, this, delay, period));
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (task != null) {
            throw new IllegalStateException("Already scheduled");
        }
    }

    private PlatformTask setupTask(final PlatformTask task) {
        this.task = task;
        return task;
    }
}
