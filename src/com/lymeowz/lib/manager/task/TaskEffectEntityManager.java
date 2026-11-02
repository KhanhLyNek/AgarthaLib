// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.task.TaskEffectEntity;
import org.bukkit.Bukkit;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.scheduler.BukkitTask;
import com.lymeowz.lib.handler.HandlerManager;

public class TaskEffectEntityManager extends HandlerManager
{
    private BukkitTask taskEffectEntity;
    
    protected TaskEffectEntityManager(final AgarthaLib plugin) {
        super(plugin);
        this.reloadTaskEffectEntity();
    }
    
    public final void reloadTaskEffectEntity() {
        if (this.taskEffectEntity != null) {
            this.taskEffectEntity.cancel();
        }
        this.taskEffectEntity = this.createTaskEffectEntity();
    }
    
    private final BukkitTask createTaskEffectEntity() {
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final Runnable runnable = new TaskEffectEntity(this.plugin);
        final int delay = 0;
        final int period = 20;
        final BukkitTask task = scheduler.runTaskTimer((Plugin)this.plugin, runnable, 0L, 20L);
        return task;
    }
}
