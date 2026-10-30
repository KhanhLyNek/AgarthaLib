package api.praya.agarthalib.builder.support;

import api.praya.agarthalib.builder.support.main.Support;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.modern.Paper26MythicLibStats;
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
