// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.game;

import com.lymeowz.lib.effect.EffectType;
import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class EffectTypeManager extends HandlerManager implements com.lymeowz.lib.effect.EffectRegistry
{
    protected EffectTypeManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract Collection<String> getEffectTypeIds();
    
    public abstract Collection<EffectType> getAllEffectType();
    
    public abstract EffectType getEffectType(final String p0);
    
    public abstract boolean isRegistered(final EffectType p0);

    @Override
    public final Collection<String> ids() {
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(getEffectTypeIds()));
    }

    @Override
    public final com.lymeowz.lib.effect.EffectDefinition find(final String id) {
        // Delegate to the existing case-insensitive legacy registry.
        return getEffectType(id);
    }
    
    public final boolean isExists(final String id) {
        return this.getEffectType(id) != null;
    }
}
