// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.api.builder.event;

import com.lymeowz.lib.utility.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import com.lymeowz.lib.event.PlayerHealthEvent;

public class PlayerHealthChangeEvent extends PlayerHealthEvent
{
    private static final HandlerList handlers;
    private double health;
    
    static {
        handlers = new HandlerList();
    }
    
    public PlayerHealthChangeEvent(final Player player, final double health) {
        super(player);
        this.health = MathUtil.limitDouble(health, 0.0, health);
    }
    
    public final double getHealth() {
        return this.health;
    }
    
    public final void setHealth(final double health) {
        this.health = MathUtil.limitDouble(health, 0.0, health);
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerHealthChangeEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerHealthChangeEvent.handlers;
    }
}
