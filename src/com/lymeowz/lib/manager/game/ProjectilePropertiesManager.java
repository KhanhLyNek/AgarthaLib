// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.game;

import org.bukkit.entity.Projectile;
import com.lymeowz.lib.projectile.ProjectileProperties;
import java.util.UUID;
import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class ProjectilePropertiesManager extends HandlerManager implements com.lymeowz.lib.projectile.ProjectileRegistry
{
    protected ProjectilePropertiesManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract Collection<UUID> getProjectileIds();

    @Override
    public final Collection<UUID> ids() {
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(getProjectileIds()));
    }

    @Override
    public final com.lymeowz.lib.projectile.ProjectileDefinition find(final Projectile projectile) {
        // The legacy default lookup never creates a projectile entry.
        return getProjectileProperties(projectile);
    }
    
    public abstract Collection<ProjectileProperties> getAllProjectileProperties();
    
    public abstract ProjectileProperties getProjectileProperties(final Projectile p0);
    
    public abstract ProjectileProperties getProjectileProperties(final Projectile p0, final boolean p1);
}
