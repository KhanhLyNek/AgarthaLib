package com.lymeowz.lib.packet.livingentity;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerPacket;
import com.lymeowz.lib.modern.Paper26LivingEntityKind;
import com.lymeowz.lib.core.builder.bridge.LivingEntityTools;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Paper 26.3 implementation of the unchanged legacy LivingEntityTools API.
 * No NMS, CraftBukkit or dependency on optional RPG/class plugins.
 */
public final class PacketLivingEntity_Paper26_3 extends HandlerPacket implements LivingEntityTools {
    public PacketLivingEntity_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }

    @Override
    public boolean isLivingEntity(final Entity entity) {
        return Paper26LivingEntityKind.isLivingEntity(entity);
    }

    @Override
    public boolean isLivingEntity(final EntityType type) {
        return Paper26LivingEntityKind.isLivingType(type);
    }
}
