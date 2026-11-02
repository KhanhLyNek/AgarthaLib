// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class PlayerRegionLeaveEvent extends com.lymeowz.lib.api.builder.event.PlayerRegionLeaveEvent
{
    public PlayerRegionLeaveEvent(final Player player, final ProtectedRegion region) {
        super(player, region);
    }
}
