// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.task;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public class TaskManager extends HandlerManager
{
    private final TaskBossBarManager taskBossBarManager;
    private final TaskEffectEntityManager taskEffectEntityManager;
    private final TaskPlayerSwingManager taskPlayerSwingManager;
    
    public TaskManager(final AgarthaLib plugin) {
        super(plugin);
        this.taskBossBarManager = new TaskBossBarManager(plugin);
        this.taskEffectEntityManager = new TaskEffectEntityManager(plugin);
        this.taskPlayerSwingManager = new TaskPlayerSwingManager(plugin);
    }
    
    public final TaskBossBarManager getTaskBossBarManager() {
        return this.taskBossBarManager;
    }
    
    public final TaskEffectEntityManager getTaskEffectEntityManager() {
        return this.taskEffectEntityManager;
    }
    
    public final TaskPlayerSwingManager getTaskPlayerSwingManager() {
        return this.taskPlayerSwingManager;
    }
}
