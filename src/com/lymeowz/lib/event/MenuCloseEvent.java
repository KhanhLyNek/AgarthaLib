// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;

public class MenuCloseEvent extends com.lymeowz.lib.api.builder.event.MenuCloseEvent
{
    public MenuCloseEvent(final Player player, final Menu menu) {
        super(player, menu);
    }
}
