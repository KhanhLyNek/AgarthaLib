// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;

public class MenuOpenEvent extends com.lymeowz.lib.api.builder.event.MenuOpenEvent
{
    public MenuOpenEvent(final Player player, final Menu menu) {
        super(player, menu);
    }
}
