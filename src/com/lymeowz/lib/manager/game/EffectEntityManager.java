// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.game;

import org.bukkit.entity.LivingEntity;
import com.lymeowz.lib.effect.EffectEntity;
import java.util.UUID;
import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class EffectEntityManager extends HandlerManager
{
    protected EffectEntityManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract Collection<UUID> getEntityIds();
    
    public abstract Collection<EffectEntity> getAllEffectEntity();
    
    public abstract EffectEntity getEffectEntity(final LivingEntity p0);
    
    public abstract EffectEntity getEffectEntity(final LivingEntity p0, final boolean p1);
}
