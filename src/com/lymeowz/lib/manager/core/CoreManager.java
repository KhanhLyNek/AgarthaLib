// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.core;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public class CoreManager extends HandlerManager
{
    private final CoreMenuManager coreMenuManager;
    
    public CoreManager(final AgarthaLib plugin) {
        super(plugin);
        this.coreMenuManager = new CoreMenuManager(plugin);
    }
    
    public final CoreMenuManager getCoreMenuManager() {
        return this.coreMenuManager;
    }
}
