// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.bridge;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.lib.core.enums.main.Slot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public interface EquipmentTools
{
    ItemStack getEquipment(final Player p0, final int p1);
    
    ItemStack getEquipment(final Player p0, final Slot p1);
    
    ItemStack getEquipment(final LivingEntity p0, final int p1);
    
    ItemStack getEquipment(final LivingEntity p0, final Slot p1);
    
    ItemStack getEquipment(final PlayerInventory p0, final int p1);
    
    ItemStack getEquipment(final PlayerInventory p0, final Slot p1);
    
    void setEquipment(final Player p0, final ItemStack p1, final int p2);
    
    void setEquipment(final Player p0, final ItemStack p1, final Slot p2);
    
    void setEquipment(final LivingEntity p0, final ItemStack p1, final int p2);
    
    void setEquipment(final LivingEntity p0, final ItemStack p1, final Slot p2);
    
    void setEquipment(final PlayerInventory p0, final ItemStack p1, final int p2);
    
    void setEquipment(final PlayerInventory p0, final ItemStack p1, final Slot p2);
}
