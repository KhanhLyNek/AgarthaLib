package com.praya.agarthalib.packet.equipment;

import com.praya.agarthalib.AgarthaLib;
import core.praya.agarthalib.enums.main.Slot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Paper implementation reuses the existing Bukkit-only equipment operations.
 * The 1.9 adapter contains no NMS; it already preserves all legacy slot IDs.
 * Guard the nullable equipment handle on modern entities.
 */
public final class PacketEquipment_Paper26_3 extends PacketEquipment_1_9 {
    public PacketEquipment_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }
    @Override
    public ItemStack getEquipment(final LivingEntity entity, final Slot slot) {
        if (entity == null || entity.getEquipment() == null) return null;
        return super.getEquipment(entity, slot);
    }
    @Override
    public void setEquipment(final LivingEntity entity, final ItemStack item, final Slot slot) {
        if (entity == null) return;
        final EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;
        super.setEquipment(entity, item, slot);
    }
}