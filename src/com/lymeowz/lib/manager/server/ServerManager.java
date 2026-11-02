// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.server;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class ServerManager extends HandlerManager
{
    protected ServerManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract ServerPlayerManager getServerPlayerManager();
    
    public abstract ServerWorldManager getServerWorldManager();
}
