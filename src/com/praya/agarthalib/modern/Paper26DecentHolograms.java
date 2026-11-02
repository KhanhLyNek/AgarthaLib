package com.praya.agarthalib.modern;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import java.util.List;

/**
 * Optional DecentHolograms transient hologram adapter.
 * This does not implement the incompatible old HolographicDisplays Hologram type.
 */
public final class Paper26DecentHolograms {
    private Paper26DecentHolograms() {}

    public static boolean createTransient(final Plugin provider, final String id,
                                          final Location location, final List<String> lines) {
        if (provider == null || !provider.isEnabled() || location == null || location.getWorld() == null)
            return false;
        return createTransient(provider.getClass().getClassLoader(), id, location, lines);
    }

    static boolean createTransient(final ClassLoader loader, final String id,
                                   final Location location, final List<String> lines) {
        if (loader == null || id == null || !id.matches("[A-Za-z0-9_-]{1,64}")
                || location == null || lines == null || lines.isEmpty()
                || lines.stream().anyMatch(s -> s == null)) return false;
        try {
            final Class<?> api = Class.forName("eu.decentsoftware.holograms.api.DHAPI", false, loader);
            // Avoid duplicate ID exceptions and never overwrite another plugin's hologram.
            if (api.getMethod("getHologram", String.class).invoke(null, id) != null) return false;
            final Object created = api.getMethod("createHologram",
                    String.class, Location.class, boolean.class, List.class)
                    .invoke(null, id, location, false, List.copyOf(lines));
            return created != null;
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return false;
        }
    }
}
