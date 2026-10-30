package com.praya.agarthalib.placeholder.replacer;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.manager.plugin.PlaceholderManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Built-in PlaceholderAPI expansion.
 *
 * The historic placeholder prefix and suffix still flow to the same
 * PlaceholderManager; only the removed PlaceholderHook registration is
 * replaced with the supported 2.12.x expansion API.
 */
public class ReplacerPlaceholderAPI extends PlaceholderExpansion {
    private final String placeholder;
    private final AgarthaLib plugin;

    public ReplacerPlaceholderAPI(final AgarthaLib plugin, final String placeholder) {
        this.plugin = plugin;
        this.placeholder = placeholder;
    }

    public final String getPlaceholder() {
        return this.placeholder;
    }

    /** Legacy public registration entrypoint used by PlaceholderManager. */
    public final boolean hook() {
        return this.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.placeholder;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(final OfflinePlayer player, @NotNull final String identifier) {
        // The original PlaceholderHook contract accepted a live Player.
        // Do not silently substitute offline or another online player's data.
        final Player online = player != null && player.isOnline() ? player.getPlayer() : null;
        return onPlaceholderRequest(online, identifier);
    }

    @Override
    public String onPlaceholderRequest(final Player player, @NotNull final String identifier) {
        final PlaceholderManager manager =
                this.plugin.getPluginManager().getPlaceholderManager();
        return manager.getReplacement(player, identifier);
    }
}
