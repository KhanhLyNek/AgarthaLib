// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ItemLoresChangeEvent extends com.lymeowz.lib.api.builder.event.ItemLoresChangeEvent
{
    public ItemLoresChangeEvent(final ItemStack item, final List<String> lores) {
        super(item, lores);
    }
}
