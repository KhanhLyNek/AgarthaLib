// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.plugin;

import com.lymeowz.lib.metrics.service.BStats;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class MetricsManager extends HandlerManager
{
    protected MetricsManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract BStats getMetricsBStats();
}
