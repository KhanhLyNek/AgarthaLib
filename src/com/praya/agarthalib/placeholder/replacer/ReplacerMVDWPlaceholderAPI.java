package com.praya.agarthalib.placeholder.replacer;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.manager.plugin.PlaceholderManager;
import com.praya.agarthalib.modern.Paper26MVdWRegistration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Original AgarthaLib MVdW placeholder registration and replacement semantics.
 *
 * Uses MVdW's actual PlaceholderReplacer interface when the plugin is enabled,
 * without linking its discontinued Java API at AgarthaLib load time.
 */
public class ReplacerMVDWPlaceholderAPI {
    private final AgarthaLib plugin;
    private final String placeholder;

    public ReplacerMVDWPlaceholderAPI(final AgarthaLib plugin, final String placeholder) {
        this.plugin = plugin;
        this.placeholder = placeholder;
    }

    public final String getPlaceholder() {
        return this.placeholder;
    }

    public final void register() {
        final Plugin source = Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI");
        final String identifier = String.valueOf(this.getPlaceholder()) + "_*";
        Paper26MVdWRegistration.register(plugin, source, identifier,
                (final Player player, final String originalPlaceholder) -> {
                    final PlaceholderManager manager =
                            plugin.getPluginManager().getPlaceholderManager();
                    // Keep the historical wildcard suffix split unchanged.
                    final String suffix = originalPlaceholder
                            .split(String.valueOf(getPlaceholder()) + "_")[1];
                    return manager.getReplacement(player, suffix);
                });
    }
}
