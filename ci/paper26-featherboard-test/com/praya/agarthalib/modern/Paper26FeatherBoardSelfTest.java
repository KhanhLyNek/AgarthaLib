package com.praya.agarthalib.modern;

import api.praya.agarthalib.builder.support.SupportFeatherBoard;
import be.maximvdw.featherboard.api.FeatherBoardAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;

/** Exercise the REAL adapter against an explicitly CI-only FeatherBoard API. */
public final class Paper26FeatherBoardSelfTest {
    private static int assertions;

    private static void check(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        assertions++;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(final Class<T> type, final java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    public static void main(final String[] args) {
        final boolean[] enabled = {true};
        final Plugin source = proxy(Plugin.class, (p, m, a) -> {
            if ("isEnabled".equals(m.getName())) {
                return enabled[0];
            }
            if ("getName".equals(m.getName())) {
                return "FeatherBoard";
            }
            throw new AssertionError("Unexpected test Plugin method: " + m.getName());
        });
        final Player player = proxy(Player.class, (p, m, a) -> {
            throw new AssertionError("FeatherBoard adapter must pass Player through without invoking: " + m.getName());
        });

        final SupportFeatherBoard hook = new SupportFeatherBoard(null, source);
        check(!hook.getPlayerScoreboardToggle(player), "Default scoreboard toggle is false");
        check(!hook.getPlayerScoreboardToggle(null), "Null player returns false");

        hook.setPlayerScoreboardToggle(player, true, true);
        check(FeatherBoardAPI.toggleCalls == 1, "Enable toggles once");
        check(FeatherBoardAPI.lastSilent, "Silent toggle flag preserved");
        check(hook.getPlayerScoreboardToggle(player), "Read after enabling");

        hook.setPlayerScoreboardToggle(player, true, false);
        check(FeatherBoardAPI.toggleCalls == 1, "Already enabled must NOT toggle");
        hook.setPlayerScoreboardToggle(player, false);
        check(FeatherBoardAPI.toggleCalls == 2, "Disable toggles once");
        check(!FeatherBoardAPI.lastSilent, "Default silent flag remains false");

        hook.resetDefaultScoreboard(player);
        check(FeatherBoardAPI.resetCalls == 1, "Reset forwarded");

        hook.removePlayerScoreboard(player, "legacy-board");
        check(FeatherBoardAPI.removeCalls == 1, "Remove forwarded");
        check("legacy-board".equals(FeatherBoardAPI.lastName), "Remove board name preserved");

        hook.showPlayerScoreboard(player, "sidebar");
        check(FeatherBoardAPI.showCalls == 1, "Show forwarded");
        check("sidebar".equals(FeatherBoardAPI.lastName), "Show board name preserved");

        enabled[0] = false;
        hook.setPlayerScoreboardToggle(player, true, true);
        hook.resetDefaultScoreboard(player);
        check(FeatherBoardAPI.toggleCalls == 2, "Disabled plugin must not toggle");
        check(FeatherBoardAPI.resetCalls == 1, "Disabled plugin must not reset");

        enabled[0] = true;
        hook.setPlayerScoreboardToggle(null, true, true);
        hook.showPlayerScoreboard(player, null);
        hook.removePlayerScoreboard(null, "ignored");
        check(FeatherBoardAPI.toggleCalls == 2, "Null player ignored");
        check(FeatherBoardAPI.showCalls == 1 && FeatherBoardAPI.removeCalls == 1,
                "Null scoreboard and player ignored");

        // Simulate an enabled optional plugin whose class loader cannot see
        // the old FeatherBoard API. There must be no blind scoreboard toggle.
        final ClassLoader missingApiLoader = new ClassLoader(Plugin.class.getClassLoader()) {
            @Override
            protected Class<?> loadClass(final String name, final boolean resolve)
                    throws ClassNotFoundException {
                if ("be.maximvdw.featherboard.api.FeatherBoardAPI".equals(name)) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        };
        final Plugin incompatibleSource = (Plugin) Proxy.newProxyInstance(
                missingApiLoader, new Class<?>[]{Plugin.class},
                (p, m, a) -> {
                    if ("isEnabled".equals(m.getName())) {
                        return true;
                    }
                    throw new AssertionError("Unexpected missing-API source method: " + m.getName());
                });
        final SupportFeatherBoard missing = new SupportFeatherBoard(null, incompatibleSource);
        check(!missing.getPlayerScoreboardToggle(player), "Missing API cannot report enabled");
        missing.setPlayerScoreboardToggle(player, true);
        missing.resetDefaultScoreboard(player);
        check(FeatherBoardAPI.toggleCalls == 2 && FeatherBoardAPI.resetCalls == 1,
                "Missing external API must not mutate the scoreboard");

        System.out.println("PASS: " + assertions + " FeatherBoard public API behavior assertions");
    }
}
