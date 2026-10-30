package com.praya.agarthalib.modern;

import api.praya.agarthalib.builder.support.SupportEssentials;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

/**
 * Tests the REAL Essentials adapter against CI-local reflective API fixtures.
 * These fixtures are never added to the production source set or plugin JAR.
 */
public final class Paper26EssentialsWarpSelfTest {
    private static int assertions;

    public interface EssentialsSource extends Plugin {
        FakeWarps getWarps();
    }

    public static final class WarpNotFoundException extends Exception {}
    public static final class InvalidWorldException extends Exception {}

    public static final class FakeWarps {
        private final World first;
        private final World second;
        private final List<String> names = List.of("spawn", "market", "missing", "badworld");

        public FakeWarps(final World first, final World second) {
            this.first = first;
            this.second = second;
        }

        public Collection<String> getList() {
            return names;
        }

        public Location getWarp(final String name) throws WarpNotFoundException, InvalidWorldException {
            if ("spawn".equals(name)) {
                return new Location(first, 1, 64, 3);
            }
            if ("market".equals(name)) {
                return new Location(second, 8, 75, 9);
            }
            if ("badworld".equals(name)) {
                throw new InvalidWorldException();
            }
            throw new WarpNotFoundException();
        }
    }

    private static void check(final boolean condition, final String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        assertions++;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(final Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, m, a) -> {
                    if ("equals".equals(m.getName())) {
                        return p == a[0];
                    }
                    if ("hashCode".equals(m.getName())) {
                        return System.identityHashCode(p);
                    }
                    if ("toString".equals(m.getName())) {
                        return type.getSimpleName() + " fixture";
                    }
                    throw new AssertionError("Unexpected World API invocation: " + m.getName());
                });
    }

    public static void main(final String[] args) {
        final World worldA = proxy(World.class);
        final World worldB = proxy(World.class);
        final FakeWarps warpApi = new FakeWarps(worldA, worldB);
        final boolean[] enabled = {true};

        final EssentialsSource source = (EssentialsSource) Proxy.newProxyInstance(
                EssentialsSource.class.getClassLoader(), new Class<?>[]{EssentialsSource.class},
                (p, m, a) -> {
                    return switch (m.getName()) {
                        case "getWarps" -> warpApi;
                        case "isEnabled" -> enabled[0];
                        case "getName" -> "Essentials";
                        default -> throw new AssertionError("Unexpected plugin method " + m.getName());
                    };
                });

        final SupportEssentials bridge = new SupportEssentials(null, source);
        check(bridge.getWarpNames() == warpApi.getList(), "Warp collection identity preserved");
        check(bridge.getWarpNames().size() == 4, "All source names retained");
        check(bridge.getWarpNames(worldA).equals(List.of("spawn")), "Names filtered by first world");
        check(bridge.getWarpNames(worldB).equals(List.of("market")), "Names filtered by second world");
        check(bridge.getWarpNames((World) null).isEmpty(), "Null world yields no names");
        check(bridge.getWarp("spawn").getWorld() == worldA, "Warp coordinates/source world preserved");
        check(bridge.getWarp("market").getBlockX() == 8, "Second warp coordinates preserved");
        check(bridge.getWarp("missing") == null, "Missing warp exception swallowed");
        check(bridge.getWarp("badworld") == null, "Invalid world exception swallowed");
        check(bridge.getWarps().size() == 2, "Invalid warps excluded from locations");
        check(bridge.getWarps(worldA).size() == 1, "World location filtering");
        check(bridge.getWarps(worldB).size() == 1, "Second world location filtering");
        check(bridge.getWarps((World) null).isEmpty(), "Null-world location filtering");

        enabled[0] = false;
        check(bridge.getWarpNames().isEmpty(), "Disabled plugin returns no warp names");
        check(bridge.getWarp("spawn") == null, "Disabled plugin returns no warp location");
        check(bridge.getWarps().isEmpty(), "Disabled plugin returns no locations");

        enabled[0] = true;
        check(bridge.getWarpNames().size() == 4, "Re-enabled plugin can be used again");

        // A plugin with no getWarps method must never trigger a synthetic warp
        // or throw an NCDFE before the optional Essentials dependency loads.
        final Plugin missingApi = (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class},
                (p, m, a) -> {
                    if ("isEnabled".equals(m.getName())) {
                        return true;
                    }
                    throw new AssertionError("Unexpected missing-API method: " + m.getName());
                });
        final SupportEssentials missing = new SupportEssentials(null, missingApi);
        check(missing.getWarpNames().isEmpty(), "Missing optional API leaves warp list empty");
        check(missing.getWarp("spawn") == null, "Missing optional API does not invent a location");

        System.out.println("PASS: " + assertions + " Essentials warp forwarding assertions");
    }
}
