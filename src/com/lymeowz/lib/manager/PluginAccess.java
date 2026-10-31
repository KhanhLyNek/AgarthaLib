package com.lymeowz.lib.manager;

import com.lymeowz.lib.LyMeowzLib;

/** Return the running library's existing plugin manager, never a new one. */
public final class PluginAccess {
    private PluginAccess() { }

    public static PluginServices of(final LyMeowzLib plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        return plugin.getPluginManager();
    }
}
