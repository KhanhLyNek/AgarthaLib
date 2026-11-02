package com.lymeowz.lib.projectile;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.Projectile;

/** Read-only, non-generating projection of the original projectile registry. */
public interface ProjectileRegistry {
    Collection<UUID> ids();
    ProjectileDefinition find(Projectile projectile);
}
