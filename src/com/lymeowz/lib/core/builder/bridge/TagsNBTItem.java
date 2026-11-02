// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.bridge;

import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.enums.main.TagsAttribute;
import org.bukkit.inventory.ItemStack;

public interface TagsNBTItem
{
    void addNBT(final ItemStack p0, final TagsAttribute p1, final double p2, final Slot p3);
    
    void clearNBT(final ItemStack p0);
    
    void setUnbreakable(final ItemStack p0, final boolean p1);
    
    boolean isUnbreakable(final ItemStack p0);
}
