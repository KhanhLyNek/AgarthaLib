package com.praya.agarthalib.modern;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import java.util.UUID;

/** Read-only MMOCore level/class. Do not change original Heroes health scaling. */
public final class Paper26MMOCoreProgress {
    private Paper26MMOCoreProgress() {}

    public static Integer level(final Plugin provider, final OfflinePlayer player) {
        if (provider == null || !provider.isEnabled() || player == null || !player.isOnline()) return null;
        return level(provider.getClass().getClassLoader(), player.getUniqueId());
    }

    public static String classId(final Plugin provider, final OfflinePlayer player) {
        if (provider == null || !provider.isEnabled() || player == null || !player.isOnline()) return null;
        return classId(provider.getClass().getClassLoader(), player.getUniqueId());
    }

    private static Object data(final ClassLoader loader, final UUID player) throws ReflectiveOperationException {
        final Class<?> type = Class.forName("net.Indyuce.mmocore.api.player.PlayerData", false, loader);
        return type.getMethod("get", UUID.class).invoke(null, player);
    }

    static Integer level(final ClassLoader loader, final UUID player) {
        if (loader == null || player == null) return null;
        try {
            final Object data = data(loader, player);
            if (data == null) return null;
            final Object value = data.getClass().getMethod("getLevel").invoke(data);
            return value instanceof Number n && n.intValue() >= 1 ? n.intValue() : null;
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return null;
        }
    }

    static String classId(final ClassLoader loader, final UUID player) {
        if (loader == null || player == null) return null;
        try {
            final Object data = data(loader, player);
            if (data == null) return null;
            final Object profess = data.getClass().getMethod("getProfess").invoke(data);
            if (profess == null) return null;
            final Object id = profess.getClass().getMethod("getId").invoke(profess);
            return id instanceof String name && !name.isBlank() ? name : null;
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return null;
        }
    }
}
