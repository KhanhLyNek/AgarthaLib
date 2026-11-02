// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.game;

import com.lymeowz.lib.menu.MenuAgarthaLib;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class MenuManager extends HandlerManager
{
    protected MenuManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract MenuAgarthaLib getMenuAgarthaLib();
}
