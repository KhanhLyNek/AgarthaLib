// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.event;

import com.lymeowz.lib.core.builder.stats.StatsEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageCalculationEvent extends com.lymeowz.lib.api.builder.event.DamageCalculationEvent
{
    public DamageCalculationEvent(final EntityDamageEvent.DamageCause cause, final Entity victims, final Entity attacker, final StatsEntity statsEntity, final double damage) {
        super(cause, victims, attacker, statsEntity, damage);
    }
}
