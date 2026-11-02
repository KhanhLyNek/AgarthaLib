// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import org.bukkit.entity.Player;

public class PlayerHealthChangeEvent extends com.lymeowz.lib.api.builder.event.PlayerHealthChangeEvent
{
    public PlayerHealthChangeEvent(final Player player, final double health) {
        super(player, health);
    }
}
