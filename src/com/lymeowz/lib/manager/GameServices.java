package com.lymeowz.lib.manager;

import java.util.Collection;

/**
 * Read-only public projection of the original library game manager.
 * Original effect, projectile and entity-stats managers remain the owners;
 * no duplicate manager is created.
 */
public interface GameServices {
    com.lymeowz.lib.effect.EffectRegistry effects();
    com.lymeowz.lib.projectile.ProjectileRegistry projectiles();
    Collection<String> effectTypeIds();
    boolean hasEffectType(String id);
    int trackedEntityStatsCount();
    int trackedProjectileCount();
}
