package com.praya.agarthalib.modern;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Read-only claimed-area detection. null => unavailable; false => wilderness.
 * Do not assume this authorizes block breaking, PvP or guild membership.
 */
public final class Paper26LandsClaims {
    private Paper26LandsClaims() {}

    public static Boolean isClaimed(final Plugin provider, final Plugin owner, final Location location) {
        if (provider == null || !provider.isEnabled() || owner == null || !owner.isEnabled()
                || location == null || location.getWorld() == null) return null;
        return isClaimed(provider.getClass().getClassLoader(), owner, location);
    }

    static Boolean isClaimed(final ClassLoader loader, final Plugin owner, final Location location) {
        if (loader == null || owner == null || location == null) return null;
        try {
            final Class<?> integration = Class.forName(
                    "me.angeschossen.lands.api.integration.LandsIntegration", false, loader);
            final Object api = integration.getMethod("of", Plugin.class).invoke(null, owner);
            if (api == null) return null;
            final Object area = integration.getMethod("getArea", Location.class).invoke(api, location);
            return area != null;
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return null;
        }
    }
}
