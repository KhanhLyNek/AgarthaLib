// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.api.manager.player;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public class PlayerManagerAPI extends HandlerManager
{
    private final PlayerActionbarManagerAPI playerActionbarManager;
    
    public PlayerManagerAPI(final AgarthaLib plugin) {
        super(plugin);
        this.playerActionbarManager = new PlayerActionbarManagerAPI(plugin);
    }
    
    public final PlayerActionbarManagerAPI getPlayerActionbarManager() {
        return this.playerActionbarManager;
    }
}
