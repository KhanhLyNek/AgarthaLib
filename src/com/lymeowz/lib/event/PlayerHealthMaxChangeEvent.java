// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import org.bukkit.entity.Player;

public class PlayerHealthMaxChangeEvent extends com.lymeowz.lib.api.builder.event.PlayerHealthMaxChangeEvent
{
    public PlayerHealthMaxChangeEvent(final Player player, final double maxHealth) {
        super(player, maxHealth);
    }
}
