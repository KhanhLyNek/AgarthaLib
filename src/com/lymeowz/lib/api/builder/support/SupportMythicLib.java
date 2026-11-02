package com.lymeowz.lib.api.builder.support;

import com.lymeowz.lib.api.builder.support.main.Support;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.modern.Paper26MythicLibStats;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/** Optional, read-only MythicLib integration. No binary dependency at load time. */
public final class SupportMythicLib extends Support {
    public SupportMythicLib(final AgarthaLib plugin, final Plugin source) {
        super(plugin, source);
    }

    /**
     * @return known stat value, or null if the provider, player, or stat is
     * missing. Never return a fabricated zero on integration failures.
     */
    public Double getPlayerStat(final OfflinePlayer player, final String statId) {
        return Paper26MythicLibStats.read(this.source, player, statId);
    }
}
