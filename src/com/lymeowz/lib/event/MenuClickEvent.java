// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.lymeowz.lib.core.builder.menu.MenuSlotAction;
import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;

public class MenuClickEvent extends com.lymeowz.lib.api.builder.event.MenuClickEvent
{
    public MenuClickEvent(final Player player, final Menu menu, final MenuSlotAction.ActionType actionType, final int slot) {
        super(player, menu, actionType, slot);
    }
}
