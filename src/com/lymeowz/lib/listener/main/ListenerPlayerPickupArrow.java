// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.main;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerCommand;
import com.lymeowz.lib.utility.ProjectileUtil;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ListenerPlayerPickupArrow extends HandlerCommand implements Listener
{
    public ListenerPlayerPickupArrow(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerPickupArrow(final PlayerPickupArrowEvent event) {
        final AbstractArrow arrow = (AbstractArrow) event.getArrow();
        if (arrow.hasMetadata("Projectile:Pickable")) {
            final boolean pickable = ProjectileUtil.isPickable((Entity)arrow);
            event.setCancelled(!pickable);
        }
    }
}
