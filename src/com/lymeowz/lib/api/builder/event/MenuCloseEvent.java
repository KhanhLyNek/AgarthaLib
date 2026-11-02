// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.api.builder.event;

import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class MenuCloseEvent extends Event
{
    private static final HandlerList handlers;
    private final Player player;
    private final Menu menu;
    
    static {
        handlers = new HandlerList();
    }
    
    public MenuCloseEvent(final Player player, final Menu menu) {
        this.player = player;
        this.menu = menu;
    }
    
    public final Player getPlayer() {
        return this.player;
    }
    
    public final Menu getMenu() {
        return this.menu;
    }
    
    public HandlerList getHandlers() {
        return MenuCloseEvent.handlers;
    }
    
    public static final HandlerList getHandlerList() {
        return MenuCloseEvent.handlers;
    }
}
