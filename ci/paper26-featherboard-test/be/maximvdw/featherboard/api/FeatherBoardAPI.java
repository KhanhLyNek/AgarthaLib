package be.maximvdw.featherboard.api;

import org.bukkit.entity.Player;
import java.util.IdentityHashMap;
import java.util.Map;

/** CI-only fake: NOT included in AgarthaLib's production source set or JAR. */
public final class FeatherBoardAPI {
    private static final Map<Player, Boolean> states = new IdentityHashMap<>();
    public static int toggleCalls;
    public static int resetCalls;
    public static int removeCalls;
    public static int showCalls;
    public static boolean lastSilent;
    public static String lastName;
    private FeatherBoardAPI() {}

    public static boolean isToggled(final Player player) {
        return states.getOrDefault(player, false);
    }

    public static void toggle(final Player player, final boolean silent) {
        toggleCalls++;
        lastSilent = silent;
        states.put(player, !isToggled(player));
    }

    public static void resetDefaultScoreboard(final Player player) {
        resetCalls++;
    }

    public static void removeScoreboardOverride(final Player player, final String name) {
        removeCalls++;
        lastName = name;
    }

    public static void showScoreboard(final Player player, final String name) {
        showCalls++;
        lastName = name;
    }
}
