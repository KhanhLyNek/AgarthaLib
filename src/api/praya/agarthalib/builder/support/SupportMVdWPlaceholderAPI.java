// Legacy public class name retained for MyItems and AgarthaLib callers.
// This optional adapter has no hard compile-time dependency on MVdWPlaceholderAPI.

package api.praya.agarthalib.builder.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import com.praya.agarthalib.AgarthaLib;
import api.praya.agarthalib.builder.support.main.SupportPlaceholder;
import api.praya.agarthalib.builder.support.main.Support;

public class SupportMVdWPlaceholderAPI extends Support implements SupportPlaceholder {
    private volatile Method replacePlaceholders;
    private volatile boolean unavailable;

    public SupportMVdWPlaceholderAPI(final AgarthaLib plugin, final Plugin source) {
        super(plugin, source);
    }

    /**
     * Preserve the original contract: the MVdW API receives the exact
     * OfflinePlayer and original string, with no replacement syntax rewrite.
     * Never route {mvdw} placeholders through PAPI's %name% syntax silently.
     */
    @Override
    public String setPlaceholders(final OfflinePlayer player, final String text) {
        if (text == null || source == null || !source.isEnabled() || unavailable) {
            return text;
        }
        try {
            Method method = replacePlaceholders;
            if (method == null) {
                synchronized (this) {
                    method = replacePlaceholders;
                    if (method == null) {
                        final Class<?> api = Class.forName(
                                "be.maximvdw.placeholderapi.PlaceholderAPI",
                                false,
                                source.getClass().getClassLoader());
                        method = api.getMethod("replacePlaceholders", OfflinePlayer.class, String.class);
                        replacePlaceholders = method;
                    }
                }
            }
            final Object resolved = method.invoke(null, player, text);
            return resolved instanceof String ? (String) resolved : text;
        } catch (final ClassNotFoundException | NoSuchMethodException | LinkageError ex) {
            // Missing APIs cannot be recovered without the external plugin
            // reloading, at which point SupportManagerAPI creates a new hook.
            unavailable = true;
            plugin.getLogger().warning("MVdWPlaceholderAPI hook unavailable: " + ex.getClass().getSimpleName());
            return text;
        } catch (final IllegalAccessException | InvocationTargetException ex) {
            // Invocation problems can be player-specific or transient: do not
            // permanently disable the placeholder hook after a single error.
            plugin.getLogger().fine("MVdW placeholder invocation failed: " + ex.getClass().getSimpleName());
            return text;
        }
    }
}
