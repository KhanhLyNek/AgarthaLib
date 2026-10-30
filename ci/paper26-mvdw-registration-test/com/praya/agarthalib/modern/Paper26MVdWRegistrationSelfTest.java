package com.praya.agarthalib.modern;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/** Test the actual reflective registration with a CI-only external API. */
public final class Paper26MVdWRegistrationSelfTest {
    private static int assertions;

    private static void check(final boolean condition, final String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
        assertions++;
    }

    private static Plugin source(final ClassLoader loader, final boolean[] enabled) {
        return (Plugin) Proxy.newProxyInstance(loader, new Class<?>[]{Plugin.class},
                (p, method, args) -> {
                    if ("isEnabled".equals(method.getName())) {
                        return enabled[0];
                    }
                    if ("getLogger".equals(method.getName())) {
                        return Logger.getLogger("MVdW fixture");
                    }
                    throw new AssertionError("Unexpected plugin method: " + method.getName());
                });
    }

    public static void main(final String[] args) {
        final boolean[] enabled = {true};
        final Plugin owner = source(Plugin.class.getClassLoader(), enabled);
        final Plugin provider = source(Plugin.class.getClassLoader(), enabled);
        final Player player = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (p, m, a) -> {
                    throw new AssertionError("Player must be forwarded, not queried by proxy");
                });

        final String[] received = {null};
        final boolean success = Paper26MVdWRegistration.register(owner, provider, "myitems_*",
                (viewer, placeholder) -> {
                    check(viewer == player, "Original Player forwarded");
                    received[0] = placeholder;
                    return "value:" + placeholder;
                });
        check(success, "Valid provider registered");
        check(PlaceholderAPI.registrations == 1, "Exactly one registration");
        check(PlaceholderAPI.lastOwner == owner, "Registration ownership preserved");
        check("myitems_*".equals(PlaceholderAPI.lastIdentifier), "Wildcard ID preserved");
        check(PlaceholderAPI.lastReplacer != null, "Original callback interface implemented");

        final String value = PlaceholderAPI.lastReplacer.onPlaceholderReplace(
                new PlaceholderReplaceEvent(player, "myitems_damage"));
        check("value:myitems_damage".equals(value), "Callback result propagated");
        check("myitems_damage".equals(received[0]), "Original placeholder text propagated");

        enabled[0] = false;
        check(!Paper26MVdWRegistration.register(owner, provider, "myitems_*", (p, v) -> v),
                "Disabled provider is not registered");
        check(PlaceholderAPI.registrations == 1, "Disabled provider causes no new registrations");
        enabled[0] = true;

        check(!Paper26MVdWRegistration.register(null, provider, "myitems_*", (p, v) -> v),
                "Null owner blocked");
        check(!Paper26MVdWRegistration.register(owner, null, "myitems_*", (p, v) -> v),
                "Null provider blocked");
        check(!Paper26MVdWRegistration.register(owner, provider, null, (p, v) -> v),
                "Null identifier blocked");
        check(!Paper26MVdWRegistration.register(owner, provider, "myitems_*", null),
                "Null callback blocked");

        final ClassLoader missingApiLoader = new ClassLoader(Plugin.class.getClassLoader()) {
            @Override
            protected Class<?> loadClass(final String name, final boolean resolve)
                    throws ClassNotFoundException {
                if (name.startsWith("be.maximvdw.placeholderapi.")) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        };
        final Plugin incompatible = source(missingApiLoader, enabled);
        check(!Paper26MVdWRegistration.register(owner, incompatible, "myitems_*",
                (p, v) -> "not-used"), "Missing external classes fail closed");
        check(PlaceholderAPI.registrations == 1, "Missing API does not create fake registration");

        System.out.println("PASS: " + assertions + " MVdW registration/callback assertions");
    }
}
