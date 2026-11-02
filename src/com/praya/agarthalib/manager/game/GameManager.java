// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.manager.game;

import core.praya.agarthalib.builder.stats.StatsEntityMemory;
import com.praya.agarthalib.effect.EffectEntityMemory;
import com.praya.agarthalib.effect.EffectTypeMemory;
import com.praya.agarthalib.projectile.ProjectilePropertiesMemory;
import com.praya.agarthalib.block.BlockBreakMemory;
import com.praya.agarthalib.menu.MenuMemory;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerManager;

public class GameManager extends HandlerManager implements com.lymeowz.lib.manager.GameServices
{
    private final MenuManager menuManager;
    private final BlockBreakManager blockBreakManager;
    private final ProjectilePropertiesManager projectilePropertiesManager;
    private final EffectTypeManager effectTypeManager;
    private final EffectEntityManager effectEntityManager;
    private final StatsEntityManager statsEntityManager;
    
    public GameManager(final AgarthaLib plugin) {
        super(plugin);
        this.menuManager = MenuMemory.getInstance();
        this.blockBreakManager = BlockBreakMemory.getInstance();
        this.projectilePropertiesManager = ProjectilePropertiesMemory.getInstance();
        this.effectTypeManager = EffectTypeMemory.getInstance();
        this.effectEntityManager = EffectEntityMemory.getInstance();
        this.statsEntityManager = StatsEntityMemory.getInstance();
    }
    
    public final MenuManager getMenuManager() {
        return this.menuManager;
    }
    
    public final BlockBreakManager getBlockBreakManager() {
        return this.blockBreakManager;
    }
    
    public final ProjectilePropertiesManager getProjectilePropertiesManager() {
        return this.projectilePropertiesManager;
    }
    
    public final EffectTypeManager getEffectTypeManager() {
        return this.effectTypeManager;
    }
    
    public final EffectEntityManager getEffectEntityManager() {
        return this.effectEntityManager;
    }
    
    public final StatsEntityManager getStatsEntityManager() {
        return this.statsEntityManager;
    }

    @Override
    public final com.lymeowz.lib.projectile.ProjectileRegistry projectiles() {
        return this.projectilePropertiesManager;
    }

    @Override
    public final com.lymeowz.lib.effect.EffectRegistry effects() {
        return this.effectTypeManager;
    }

    @Override
    public final java.util.Collection<String> effectTypeIds() {
        // The legacy API returns a live, mutable keySet. Canonical callers
        // should not be able to remove a registered effect by clearing it.
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(this.effectTypeManager.getEffectTypeIds()));
    }

    @Override
    public final boolean hasEffectType(final String id) {
        return this.effectTypeManager.isExists(id);
    }

    @Override
    public final int trackedEntityStatsCount() {
        return this.statsEntityManager.getEntityIds().size();
    }

    @Override
    public final int trackedProjectileCount() {
        return this.projectilePropertiesManager.getProjectileIds().size();
    }
}
