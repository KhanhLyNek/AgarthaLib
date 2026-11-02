// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.item;

import org.bukkit.inventory.ItemStack;

public interface Item
{
    ItemStack getItemStack();
    
    void setItemStack(final ItemStack p0);
    
    void addItemStack(final ItemStack p0);
    
    void removeItemStack(final ItemStack p0);
    
    void clearItemStack();
}
