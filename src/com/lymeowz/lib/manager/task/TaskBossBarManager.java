// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.task.TaskBossBarPlayer;
import org.bukkit.Bukkit;
import com.lymeowz.lib.utility.ServerUtil;
import com.lymeowz.lib.core.enums.main.VersionNMS;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.scheduler.BukkitTask;
import com.lymeowz.lib.handler.HandlerManager;

public class TaskBossBarManager extends HandlerManager
{
    private BukkitTask taskBossBarPlayer;
    
    protected TaskBossBarManager(final AgarthaLib plugin) {
        super(plugin);
        this.reloadTaskBossBarPlayer();
    }
    
    public final void reloadTaskBossBarPlayer() {
        if (this.taskBossBarPlayer != null) {
            this.taskBossBarPlayer.cancel();
        }
        this.taskBossBarPlayer = this.createTaskBossBarPlayer();
    }
    
    private final BukkitTask createTaskBossBarPlayer() {
        if (!ServerUtil.isCompatible(VersionNMS.V1_9_R2)) {
            final BukkitScheduler scheduler = Bukkit.getScheduler();
            final Runnable runnable = new TaskBossBarPlayer(this.plugin);
            final int delay = 0;
            final int period = 1;
            final BukkitTask task = scheduler.runTaskTimer((Plugin)this.plugin, runnable, 0L, 1L);
            return task;
        }
        return null;
    }
}
