// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib;

import com.lymeowz.lib.server.ServerWorldMemory;
import com.lymeowz.lib.server.ServerPlayerMemory;
import com.lymeowz.lib.manager.server.ServerWorldManager;
import com.lymeowz.lib.manager.server.ServerPlayerManager;
import com.lymeowz.lib.manager.server.ServerManager;

public final class AgarthaServerMemory extends ServerManager
{
    private final ServerPlayerManager serverPlayerManager;
    private final ServerWorldManager serverWorldManager;
    
    protected AgarthaServerMemory(final AgarthaLib plugin) {
        super(plugin);
        this.serverPlayerManager = ServerPlayerMemory.getInstance();
        this.serverWorldManager = ServerWorldMemory.getInstance();
    }
    
    @Override
    public ServerPlayerManager getServerPlayerManager() {
        return this.serverPlayerManager;
    }
    
    @Override
    public ServerWorldManager getServerWorldManager() {
        return this.serverWorldManager;
    }
}
