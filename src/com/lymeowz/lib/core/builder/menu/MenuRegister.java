// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.menu;

import com.lymeowz.lib.manager.core.CoreMenuManager;
import com.lymeowz.lib.manager.core.CoreManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.plugin.Plugin;

public class MenuRegister
{
    private final Plugin plugin;
    private final MenuBuilder builder;
    
    public MenuRegister(final Plugin plugin, final MenuBuilder builder) {
        this.plugin = plugin;
        this.builder = builder;
    }
    
    public final Plugin getSourcePlugin() {
        return this.plugin;
    }
    
    public final MenuBuilder getBuilder() {
        return this.builder;
    }
    
    public final boolean register() {
        final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getProvidingPlugin((Class)AgarthaLib.class);
        final CoreManager coreManager = plugin.getCoreManager();
        final CoreMenuManager coreMenuManager = coreManager.getCoreMenuManager();
        return coreMenuManager.registerMenu(this);
    }
}
