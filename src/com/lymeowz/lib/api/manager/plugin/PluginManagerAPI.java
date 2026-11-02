// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.api.manager.plugin;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public class PluginManagerAPI extends HandlerManager
{
    private final SupportManagerAPI supportManager;
    
    public PluginManagerAPI(final AgarthaLib plugin) {
        super(plugin);
        this.supportManager = new SupportManagerAPI(plugin);
    }
    
    public final SupportManagerAPI getSupportManager() {
        return this.supportManager;
    }
}
