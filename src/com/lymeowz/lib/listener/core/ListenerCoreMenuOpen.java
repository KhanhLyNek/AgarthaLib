// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.core;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import com.lymeowz.lib.utility.ServerEventUtil;
import com.lymeowz.lib.event.MenuOpenEvent;
import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerCoreMenuOpen extends HandlerListener implements Listener
{
    public ListenerCoreMenuOpen(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void menuOpenEvent(final InventoryOpenEvent event) {
        if (!event.isCancelled()) {
            final HumanEntity human = event.getPlayer();
            if (human instanceof Player) {
                final Player player = (Player)human;
                final Inventory inventory = event.getInventory();
                final InventoryHolder holder = inventory.getHolder();
                if (holder != null && holder instanceof Menu) {
                    final Menu menu = (Menu)holder;
                    final MenuOpenEvent menuOpenEvent = new MenuOpenEvent(player, menu);
                    ServerEventUtil.callEvent(menuOpenEvent);
                    if (menuOpenEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
