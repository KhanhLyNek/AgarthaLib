package api.praya.agarthalib.builder.support;

import api.praya.agarthalib.builder.support.main.Support;
import com.praya.agarthalib.AgarthaLib;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optional FeatherBoard integration without a hard dependency on its historic
 * API JAR. All original public signatures and the toggle-if-needed behavior
 * are retained. No scoreboard is changed if the current state is unknown.
 */
public class SupportFeatherBoard extends Support {
    private static final String API_CLASS = "be.maximvdw.featherboard.api.FeatherBoardAPI";

    private final Map<String, Method> methods = new ConcurrentHashMap<>();
    private volatile boolean unavailable;

    public SupportFeatherBoard(final AgarthaLib plugin, final Plugin source) {
        super(plugin, source);
    }

    public final boolean getPlayerScoreboardToggle(final Player player) {
        final Boolean state = readToggle(player);
        return Boolean.TRUE.equals(state);
    }

    public final void setPlayerScoreboardToggle(final Player player, final boolean toggle) {
        this.setPlayerScoreboardToggle(player, toggle, false);
    }

    public final void setPlayerScoreboardToggle(final Player player, final boolean toggle, final boolean silent) {
        if (player == null) {
            return;
        }
        // A failed isToggled() lookup must NEVER be interpreted as "disabled";
        // otherwise toggling can invert an already-correct scoreboard.
        final Boolean before = readToggle(player);
        if (before != null && before.booleanValue() != toggle) {
            invoke("toggle", new Class<?>[]{Player.class, boolean.class}, player, silent);
        }
    }

    public final void resetDefaultScoreboard(final Player player) {
        if (player != null) {
            invoke("resetDefaultScoreboard", new Class<?>[]{Player.class}, player);
        }
    }

    public final void removePlayerScoreboard(final Player player, final String scoreboard) {
        if (player != null && scoreboard != null) {
            invoke("removeScoreboardOverride", new Class<?>[]{Player.class, String.class}, player, scoreboard);
        }
    }

    public final void showPlayerScoreboard(final Player player, final String scoreboard) {
        if (player != null && scoreboard != null) {
            invoke("showScoreboard", new Class<?>[]{Player.class, String.class}, player, scoreboard);
        }
    }

    private Boolean readToggle(final Player player) {
        if (player == null) {
            return null;
        }
        final Object result = invoke("isToggled", new Class<?>[]{Player.class}, player);
        return result instanceof Boolean ? (Boolean) result : null;
    }

    private Object invoke(final String name, final Class<?>[] signature, final Object... args) {
        if (unavailable || source == null || !source.isEnabled()) {
            return null;
        }
        try {
            Method method = methods.get(name);
            if (method == null) {
                synchronized (methods) {
                    method = methods.get(name);
                    if (method == null) {
                        final Class<?> api = Class.forName(API_CLASS, false,
                                source.getClass().getClassLoader());
                        method = api.getMethod(name, signature);
                        methods.put(name, method);
                    }
                }
            }
            return method.invoke(null, args);
        } catch (final ClassNotFoundException | NoSuchMethodException | LinkageError ex) {
            // An incompatible external API is not repaired by silently changing
            // scoreboard state. SupportManagerAPI handles plugin disable/reload.
            unavailable = true;
            if (plugin != null) {
                plugin.getLogger().warning("FeatherBoard API unavailable: "
                        + ex.getClass().getSimpleName() + " (" + name + ")");
            }
            return null;
        } catch (final IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
            // Do not permanently disable the integration after a player-specific
            // or otherwise transient invocation failure.
            if (plugin != null) {
                plugin.getLogger().fine("FeatherBoard API call failed: "
                        + name + " (" + ex.getClass().getSimpleName() + ")");
            }
            return null;
        }
    }
}
