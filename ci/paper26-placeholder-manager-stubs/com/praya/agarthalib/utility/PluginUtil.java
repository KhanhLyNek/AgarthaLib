package com.praya.agarthalib.utility;

import org.bukkit.plugin.Plugin;

/** CI-only signature fixture; never included in a production plugin JAR. */
public final class PluginUtil {
    private PluginUtil() {}
    public static Plugin getPlugin(final String name) {
        return null;
    }
}
