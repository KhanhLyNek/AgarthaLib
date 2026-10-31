package com.lymeowz.lib.manager;

import com.lymeowz.lib.LyMeowzLib;

/** Resolve the live library services without any legacy public types. */
public final class GameAccess {
    private GameAccess() { }

    public static GameServices of(final LyMeowzLib plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        return plugin.getGameManager();
    }
}
