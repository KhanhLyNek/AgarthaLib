package be.maximvdw.placeholderapi;

import org.bukkit.plugin.Plugin;

public final class PlaceholderAPI {
    public static Plugin lastOwner;
    public static String lastIdentifier;
    public static PlaceholderReplacer lastReplacer;
    public static int registrations;

    private PlaceholderAPI() {
    }

    public static void registerPlaceholder(final Plugin owner, final String identifier,
                                            final PlaceholderReplacer replacer) {
        lastOwner = owner;
        lastIdentifier = identifier;
        lastReplacer = replacer;
        registrations++;
    }
}
