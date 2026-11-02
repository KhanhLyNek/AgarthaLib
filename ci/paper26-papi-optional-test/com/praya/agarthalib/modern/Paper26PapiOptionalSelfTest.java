package com.praya.agarthalib.modern;

import api.praya.agarthalib.builder.support.SupportPlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;

/** Exercises the actual optional PlaceholderAPI bridge when provider is disabled. */
public final class Paper26PapiOptionalSelfTest {
    private static int cases;

    private static void same(final Object expected, final Object actual, final String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + ", received " + actual);
        }
        cases++;
    }

    public static void main(final String[] args) {
        final Plugin disabled = (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class},
                (proxy, method, arguments) -> {
                    if ("isEnabled".equals(method.getName())) {
                        return false;
                    }
                    throw new AssertionError("Disabled provider must not be called: " + method.getName());
                });
        final OfflinePlayer absent = (OfflinePlayer) Proxy.newProxyInstance(
                OfflinePlayer.class.getClassLoader(), new Class<?>[]{OfflinePlayer.class},
                (proxy, method, arguments) -> {
                    throw new AssertionError("Disabled provider must not inspect player");
                });

        final SupportPlaceholderAPI bridge = new SupportPlaceholderAPI(null, disabled);
        same("%player_name%", bridge.setPlaceholders(absent, "%player_name%"),
                "Disabled provider retains exact placeholder");
        same("&6%vault_eco_balance%", bridge.setPlaceholders(null, "&6%vault_eco_balance%"),
                "Disabled provider retains colors and Vault expansion");
        same("", bridge.setPlaceholders(absent, ""), "Empty content preserved");
        same(null, bridge.setPlaceholders(absent, null), "Null content preserved");
        same("%%escaped%%", new SupportPlaceholderAPI(null, null)
                .setPlaceholders(absent, "%%escaped%%"), "Missing provider preserves input");

        System.out.println("PASS: " + cases + " optional PlaceholderAPI fallback assertions");
    }
}
