// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.main;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.lib.core.builder.stats.StatsEntity;
import com.lymeowz.lib.effect.EffectEntity;
import org.bukkit.entity.Entity;
import com.lymeowz.lib.manager.game.StatsEntityManager;
import com.lymeowz.lib.manager.game.EffectEntityManager;
import com.lymeowz.lib.manager.game.GameManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerEntityDeath extends HandlerListener implements Listener
{
    public ListenerEntityDeath(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void entityDeathEvent(final EntityDeathEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final EffectEntityManager effectEntityManager = gameManager.getEffectEntityManager();
        final StatsEntityManager statsEntityManager = gameManager.getStatsEntityManager();
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof LivingEntity) {
            final LivingEntity victims = (LivingEntity)entity;
            final EffectEntity effectEntity = effectEntityManager.getEffectEntity(victims);
            final StatsEntity statsEntity = statsEntityManager.getStatsEntity(victims);
            if (effectEntity != null) {
                effectEntity.unregister();
            }
            if (statsEntity != null) {
                statsEntity.unregister();
            }
        }
    }
}
