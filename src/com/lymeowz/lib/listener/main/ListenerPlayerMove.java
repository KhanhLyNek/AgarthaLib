// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.main;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.lib.event.PlayerRegionEvent;
import java.util.Iterator;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.lymeowz.lib.AgarthaLibConfig;
import com.lymeowz.lib.api.builder.support.SupportWorldGuard;
import com.lymeowz.lib.api.manager.plugin.SupportManagerAPI;
import com.lymeowz.lib.api.manager.plugin.PluginManagerAPI;
import org.bukkit.event.Event;
import com.lymeowz.lib.utility.ServerEventUtil;
import com.lymeowz.lib.event.PlayerRegionLeaveEvent;
import com.lymeowz.lib.event.PlayerRegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.lymeowz.lib.api.main.AgarthaLibAPI;
import org.bukkit.event.player.PlayerMoveEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerCommand;

public class ListenerPlayerMove extends HandlerCommand implements Listener
{
    public ListenerPlayerMove(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerCheckRegion(final PlayerMoveEvent event) {
        final AgarthaLibAPI agarthaLibAPI = AgarthaLibAPI.getInstance();
        final PluginManagerAPI pluginManagerAPI = agarthaLibAPI.getPluginManagerAPI();
        final SupportManagerAPI supportManagerAPI = pluginManagerAPI.getSupportManager();
        final SupportWorldGuard supportWorldGuard = supportManagerAPI.getSupportWorldGuard();
        final AgarthaLibConfig mainConfig = this.plugin.getMainConfig();
        if (!event.isCancelled()) {
            final boolean enableRegionEvent = mainConfig.isEventEnableRegion();
            if (enableRegionEvent && supportWorldGuard != null) {
                final Player player = event.getPlayer();
                final Location from = event.getFrom();
                final Location to = event.getTo();
                final World world = player.getWorld();
                final int xFrom = from.getBlockX();
                final int yFrom = from.getBlockY();
                final int zFrom = from.getBlockZ();
                final int xTo = to.getBlockX();
                final int yTo = to.getBlockY();
                final int zTo = to.getBlockZ();
                for (final ProtectedRegion region : supportWorldGuard.getRegions(world)) {
                    final boolean fromRegion = region.contains(xFrom, yFrom, zFrom);
                    final boolean toRegion = region.contains(xTo, yTo, zTo);
                    PlayerRegionEvent playerRegionEvent = null;
                    if (!fromRegion && toRegion) {
                        playerRegionEvent = new PlayerRegionEnterEvent(player, region);
                    }
                    else if (fromRegion && !toRegion) {
                        playerRegionEvent = new PlayerRegionLeaveEvent(player, region);
                    }
                    if (playerRegionEvent != null) {
                        ServerEventUtil.callEvent((Event)playerRegionEvent);
                        if (!playerRegionEvent.isCancelled()) {
                            continue;
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
