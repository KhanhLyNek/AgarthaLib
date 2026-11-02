package com.praya.agarthalib.modern;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;

/**
 * Optional MVdWPlaceholderAPI registration through its original binary
 * interface. The third-party classes are only loaded through the actual
 * plugin class loader when MVdW is installed and enabled.
 */
public final class Paper26MVdWRegistration {
    private Paper26MVdWRegistration() {
    }

    /**
     * @return true only when the provider accepted the registration.
     * An unsupported provider never registers a fabricated placeholder.
     */
    public static boolean register(final Plugin owner, final Plugin provider,
                                   final String identifier,
                                   final BiFunction<Player, String, String> replacement) {
        if (owner == null || provider == null || !provider.isEnabled()
                || identifier == null || replacement == null) {
            return false;
        }
        try {
            final ClassLoader loader = provider.getClass().getClassLoader();
            final Class<?> api = Class.forName(
                    "be.maximvdw.placeholderapi.PlaceholderAPI", false, loader);
            final Class<?> callback = Class.forName(
                    "be.maximvdw.placeholderapi.PlaceholderReplacer", false, loader);

            final Object proxy = Proxy.newProxyInstance(
                    loader,
                    new Class<?>[]{callback},
                    (instance, method, args) -> {
                        if (method.getDeclaringClass() == Object.class) {
                            return switch (method.getName()) {
                                case "equals" -> instance == args[0];
                                case "hashCode" -> System.identityHashCode(instance);
                                case "toString" -> "AgarthaLib MVdW placeholder " + identifier;
                                default -> null;
                            };
                        }
                        if (!"onPlaceholderReplace".equals(method.getName())
                                || args == null || args.length != 1 || args[0] == null) {
                            throw new UnsupportedOperationException(
                                    "Unknown MVdW placeholder callback " + method.getName());
                        }
                        final Object event = args[0];
                        final Method getPlayer = event.getClass().getMethod("getPlayer");
                        final Method getPlaceholder = event.getClass().getMethod("getPlaceholder");
                        return replacement.apply(
                                (Player) getPlayer.invoke(event),
                                (String) getPlaceholder.invoke(event));
                    });

            final Method register = api.getMethod(
                    "registerPlaceholder", Plugin.class, String.class, callback);
            register.invoke(null, owner, identifier, proxy);
            return true;
        } catch (final ClassNotFoundException | NoSuchMethodException
                       | IllegalAccessException | InvocationTargetException
                       | IllegalArgumentException | LinkageError ex) {
            owner.getLogger().warning("MVdW placeholder registration failed for "
                    + identifier + ": " + ex.getClass().getSimpleName());
            return false;
        }
    }
}
