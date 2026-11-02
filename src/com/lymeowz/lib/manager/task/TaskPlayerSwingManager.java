// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.task.TaskPlayerSwing;
import org.bukkit.Bukkit;
import com.lymeowz.lib.utility.ServerUtil;
import com.lymeowz.lib.core.enums.main.VersionNMS;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.scheduler.BukkitTask;
import com.lymeowz.lib.handler.HandlerManager;

public class TaskPlayerSwingManager extends HandlerManager
{
    private BukkitTask taskPlayerSwing;
    
    protected TaskPlayerSwingManager(final AgarthaLib plugin) {
        super(plugin);
        this.reloadTaskPlayerSwing();
    }
    
    public final void reloadTaskPlayerSwing() {
        if (this.taskPlayerSwing != null) {
            this.taskPlayerSwing.cancel();
        }
        this.taskPlayerSwing = this.createTaskPlayerSwing();
    }
    
    private final BukkitTask createTaskPlayerSwing() {
        if (ServerUtil.isCompatible(VersionNMS.V1_9_R2)) {
            final BukkitScheduler scheduler = Bukkit.getScheduler();
            final Runnable runnable = new TaskPlayerSwing(this.plugin);
            final int delay = 0;
            final int period = 1;
            final BukkitTask task = scheduler.runTaskTimer((Plugin)this.plugin, runnable, 0L, 1L);
            return task;
        }
        return null;
    }
}
