// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.custom;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.lib.utility.EntityUtil;
import com.lymeowz.lib.event.PlayerHealthChangeEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerPlayerHealthChange extends HandlerListener implements Listener
{
    public ListenerPlayerHealthChange(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerHealthChange(final PlayerHealthChangeEvent event) {
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final double health = event.getHealth();
            EntityUtil.setHealth((LivingEntity)player, health);
        }
    }
}
