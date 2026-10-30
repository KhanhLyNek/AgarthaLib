package be.maximvdw.placeholderapi;

import org.bukkit.entity.Player;

public final class PlaceholderReplaceEvent {
    private final Player player;
    private final String placeholder;

    public PlaceholderReplaceEvent(final Player player, final String placeholder) {
        this.player = player;
        this.placeholder = placeholder;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
