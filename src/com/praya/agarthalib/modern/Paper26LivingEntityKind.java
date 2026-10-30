package com.praya.agarthalib.modern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Uses the server's actual Paper entity-class registry instead of legacy
 * enum-name lists that predate modern mobs and display entities.
 *
 * Keep the historic deliberate ARMOR_STAND exclusion: armor stands implement
 * LivingEntity in Bukkit, but the original MyItems combat helper did not
 * treat them as valid living targets.
 */
public final class Paper26LivingEntityKind {
    private Paper26LivingEntityKind() {
    }

    public static boolean isLivingType(final EntityType type) {
        if (type == null || type == EntityType.ARMOR_STAND) {
            return false;
        }
        final Class<? extends Entity> entityClass = type.getEntityClass();
        return entityClass != null && LivingEntity.class.isAssignableFrom(entityClass);
    }

    public static boolean isLivingEntity(final Entity entity) {
        return entity instanceof LivingEntity && isLivingType(entity.getType());
    }
}
