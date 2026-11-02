package com.praya.agarthalib.modern;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.util.UUID;

/** Read-only, optional MythicLib stats. Null means unavailable, never a fake zero. */
public final class Paper26MythicLibStats {
    private Paper26MythicLibStats() {}
    public static Double read(final Plugin provider, final OfflinePlayer player, final String stat) {
        if (provider == null || !provider.isEnabled() || player == null || !player.isOnline()
                || stat == null || stat.isBlank()) return null;
        return read(provider.getClass().getClassLoader(), player.getUniqueId(), stat);
    }
    static Double read(final ClassLoader loader, final UUID id, final String stat) {
        if (loader == null || id == null || stat == null || stat.isBlank()) return null;
        try {
            final Class<?> type = Class.forName("io.lumine.mythic.lib.api.player.MMOPlayerData", false, loader);
            final Method get = type.getMethod("get", UUID.class);
            final Object data = get.invoke(null, id);
            if (data == null) return null;
            final Object map = type.getMethod("getStatMap").invoke(data);
            if (map == null) return null;
            final Object value = map.getClass().getMethod("getStat", String.class).invoke(map, stat);
            if (!(value instanceof Number n) || !Double.isFinite(n.doubleValue())) return null;
            return n.doubleValue();
        } catch (ReflectiveOperationException | ClassCastException | LinkageError ex) {
            return null;
        }
    }
}
