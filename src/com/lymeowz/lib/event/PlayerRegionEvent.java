// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class PlayerRegionEvent extends com.lymeowz.lib.api.builder.event.PlayerRegionEvent
{
    public PlayerRegionEvent(final Player player, final ProtectedRegion region) {
        super(player, region);
    }
}
