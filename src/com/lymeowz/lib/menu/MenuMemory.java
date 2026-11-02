// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.core.builder.menu.MenuExecutor;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.manager.game.MenuManager;

public class MenuMemory extends MenuManager
{
    private final MenuAgarthaLib menuAgarthaLib;
    
    private MenuMemory(final AgarthaLib plugin) {
        super(plugin);
        final MenuExecutor executorAgarthaLib = new MenuAgarthaLibExecutor(plugin);
        this.menuAgarthaLib = new MenuAgarthaLib(plugin, executorAgarthaLib);
    }
    
    public static final MenuMemory getInstance() {
        return MenuMemorySingleton.instance;
    }
    
    @Override
    public MenuAgarthaLib getMenuAgarthaLib() {
        return this.menuAgarthaLib;
    }
    
    private static class MenuMemorySingleton
    {
        private static final MenuMemory instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            instance = new MenuMemory(plugin);
        }
    }
}
