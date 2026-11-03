package com.lymeowz.lib.core.builder.stats;

import java.lang.reflect.Proxy;
import org.bukkit.plugin.Plugin;

/**
 * Pure JVM regression: external plugin stat contributions have neutral
 * defaults, correctly aggregate, and never throw on absent plugin names.
 */
public final class Paper26StatsEntitySelfTest {
    private static int assertions;

    private static void equal(double actual, double expected, String label) {
        assertions++;
        if (!Double.isFinite(actual) || Math.abs(actual - expected) > 0.000001) {
            throw new AssertionError(label + ": expected " + expected + ", actual " + actual);
        }
    }

    private static Plugin plugin(final String name) {
        return (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(),
                new Class<?>[]{Plugin.class},
                (proxy, method, args) -> {
                    if ("getName".equals(method.getName())) return name;
                    if ("toString".equals(method.getName())) return name;
                    if ("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
                    if ("equals".equals(method.getName())) return proxy == args[0];
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    public static void main(String[] args) {
        final Plugin one = plugin("StatsSourceA");
        final Plugin two = plugin("StatsSourceB");
        final Stats additive = new Stats(StatsType.BASE_ADDITIONAL_DAMAGE);
        equal(additive.getTotalValueByPlugin("missing"), 0, "unregistered additive source");
        equal(additive.getTotalValueByPlugin((Plugin) null), 0, "null additive source");
        equal(additive.getTotalValueByID("missing"), 0, "unregistered additive ID");
        equal(additive.getTotalValue(), 0, "unregistered additive global total");
        additive.setValue(one, "weapon", 10);
        additive.setValue(one, "gem", 2.5);
        additive.setValue(two, "weapon", 3);
        equal(additive.getTotalValueByPlugin("statsSourceA"), 12.5, "case-insensitive source sum");
        equal(additive.getTotalValueByPlugin(one), 12.5, "plugin instance source sum");
        equal(additive.getTotalValueByPlugin("unknown"), 0, "missing additive after registration");
        equal(additive.getTotalValueByID("weapon"), 13, "cross-source same ID aggregation");
        equal(additive.getTotalValue(), 15.5, "global additive total");

        final Stats multiplier = new Stats(StatsType.FINAL_MULTIPLIER);
        equal(multiplier.getTotalValueByPlugin("unknown"), 1, "missing multiplier uses identity");
        equal(multiplier.getTotalValueByPlugin((String) null), 1, "null multiplier source");
        equal(multiplier.getTotalValueByID("missing"), 1, "missing multiplier ID");
        equal(multiplier.getTotalValue(), 1, "unregistered global multiplier");
        multiplier.setValue(one, "weapon", 2);
        multiplier.setValue(one, "gem", 1.5);
        multiplier.setValue(two, "weapon", 1.2);
        equal(multiplier.getTotalValueByPlugin("StatsSourceA"), 3, "within-source product");
        equal(multiplier.getTotalValueByPlugin("unknown"), 1, "missing multiplier after registration");
        equal(multiplier.getTotalValueByID("weapon"), 2.4, "across-source multiplier ID");
        equal(multiplier.getTotalValue(), 3.6, "global multiplier product");
        System.out.println("PASS: " + assertions
                + " Lib Stats API assertions (neutral lookup, addition, multiplication)");
    }
}
