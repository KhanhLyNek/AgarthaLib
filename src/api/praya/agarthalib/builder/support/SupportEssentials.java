package api.praya.agarthalib.builder.support;

import api.praya.agarthalib.builder.support.main.Support;
import com.praya.agarthalib.AgarthaLib;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Optional Essentials/EssentialsX warp API adapter for Paper 26.3.
 *
 * Preserves the original public return types and the external Essentials warp
 * source. No plugin data is copied, converted, or silently replaced by a
 * different warp provider. Only the optional binary dependency is removed.
 */
public class SupportEssentials extends Support {
    private volatile Method warpsGetter;
    private volatile Method warpNamesGetter;
    private volatile Method warpGetter;
    private volatile boolean unavailable;

    public SupportEssentials(final AgarthaLib plugin, final Plugin source) {
        super(plugin, source);
    }

    public final Collection<String> getWarpNames() {
        final Object warps = warps();
        if (warps == null) {
            return Collections.emptyList();
        }
        try {
            final Method method = namesMethod(warps.getClass());
            final Object names = method.invoke(warps);
            if (!(names instanceof Collection<?>)) {
                return Collections.emptyList();
            }
            // The original Essentials adapter returned the API's collection
            // directly, so do not copy or filter it behind callers' backs.
            @SuppressWarnings("unchecked")
            final Collection<String> original = (Collection<String>) names;
            return original;
        } catch (final ReflectiveOperationException | LinkageError ex) {
            failure("getList", ex);
            return Collections.emptyList();
        }
    }

    public final Collection<String> getWarpNames(final World world) {
        final Collection<String> result = new ArrayList<>();
        if (world != null) {
            for (final String name : getWarpNames()) {
                final Location location = getWarp(name);
                if (location != null && world.equals(location.getWorld())) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    public final Location getWarp(final String name) {
        final Object warps = warps();
        if (warps == null) {
            return null;
        }
        try {
            final Method method = warpMethod(warps.getClass());
            final Object location = method.invoke(warps, name);
            return location instanceof Location ? (Location) location : null;
        } catch (final InvocationTargetException ex) {
            // The historical adapter swallowed exactly these two checked
            // Essentials exceptions for invalid/nonexistent warp locations.
            final Throwable cause = ex.getCause();
            if (cause != null && (isException(cause, "WarpNotFoundException")
                    || isException(cause, "InvalidWorldException"))) {
                return null;
            }
            failure("getWarp", ex);
            return null;
        } catch (final ReflectiveOperationException | LinkageError ex) {
            failure("getWarp", ex);
            return null;
        }
    }

    public final Collection<Location> getWarps() {
        final Collection<Location> result = new ArrayList<>();
        for (final String name : getWarpNames()) {
            final Location location = getWarp(name);
            if (location != null) {
                result.add(location);
            }
        }
        return result;
    }

    public final Collection<Location> getWarps(final World world) {
        final Collection<Location> result = new ArrayList<>();
        if (world != null) {
            for (final Location location : getWarps()) {
                if (world.equals(location.getWorld())) {
                    result.add(location);
                }
            }
        }
        return result;
    }

    private Object warps() {
        if (unavailable || source == null || !source.isEnabled()) {
            return null;
        }
        try {
            Method method = warpsGetter;
            if (method == null) {
                synchronized (this) {
                    method = warpsGetter;
                    if (method == null) {
                        method = source.getClass().getMethod("getWarps");
                        warpsGetter = method;
                    }
                }
            }
            return method.invoke(source);
        } catch (final ReflectiveOperationException | LinkageError ex) {
            failure("getWarps", ex);
            return null;
        }
    }

    private Method namesMethod(final Class<?> warpsType) throws NoSuchMethodException {
        Method method = warpNamesGetter;
        if (method == null || method.getDeclaringClass() != warpsType) {
            synchronized (this) {
                method = warpNamesGetter;
                if (method == null || method.getDeclaringClass() != warpsType) {
                    method = warpsType.getMethod("getList");
                    warpNamesGetter = method;
                }
            }
        }
        return method;
    }

    private Method warpMethod(final Class<?> warpsType) throws NoSuchMethodException {
        Method method = warpGetter;
        if (method == null || method.getDeclaringClass() != warpsType) {
            synchronized (this) {
                method = warpGetter;
                if (method == null || method.getDeclaringClass() != warpsType) {
                    method = warpsType.getMethod("getWarp", String.class);
                    warpGetter = method;
                }
            }
        }
        return method;
    }

    private static boolean isException(final Throwable exception, final String simpleName) {
        for (Class<?> type = exception.getClass(); type != null; type = type.getSuperclass()) {
            if (simpleName.equals(type.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    private void failure(final String operation, final Throwable ex) {
        if (ex instanceof NoSuchMethodException || ex instanceof LinkageError) {
            unavailable = true;
        }
        if (plugin != null) {
            plugin.getLogger().fine("Optional Essentials warp API " + operation
                    + " unavailable: " + ex.getClass().getSimpleName());
        }
    }
}
