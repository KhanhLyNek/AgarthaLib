package com.praya.agarthalib.modern;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Paper26PlaceholderValuesSelfTest {
    private static int assertions;
    private static void same(final String expected, final String actual, final String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " got " + actual);
        }
        assertions++;
    }

    public static void main(final String[] args) {
        final Map<String, String> configured = new LinkedHashMap<>();
        configured.put("prefix", "&8[&aAgartha&8]&r");
        configured.put("Sign_Done", "&8&l[&a✔&8&l]");
        configured.put("unicode", "★ ❤ ✚");
        configured.put("raw", "%player_name% {mvdw_suffix}");
        configured.put("empty", "");

        same("&8[&aAgartha&8]&r", Paper26PlaceholderValues.get(configured, "prefix"), "exact");
        same("&8[&aAgartha&8]&r", Paper26PlaceholderValues.get(configured, "PREFIX"), "uppercase");
        same("&8&l[&a✔&8&l]", Paper26PlaceholderValues.get(configured, "sign_done"), "case");
        same("★ ❤ ✚", Paper26PlaceholderValues.get(configured, "UnIcOdE"), "unicode");
        same("%player_name% {mvdw_suffix}", Paper26PlaceholderValues.get(configured, "raw"), "no recursive formatting");
        same("", Paper26PlaceholderValues.get(configured, "empty"), "empty configured value");
        same(null, Paper26PlaceholderValues.get(configured, "missing"), "unknown unchanged");
        same(null, Paper26PlaceholderValues.get(configured, null), "null identifier");
        same(null, Paper26PlaceholderValues.get(null, "prefix"), "null map");
        same(null, Paper26PlaceholderValues.get(Map.of(), "prefix"), "empty map");
        same(null, Paper26PlaceholderValues.get(configured, " prefix "), "no silent trim");
        same(null, Paper26PlaceholderValues.get(configured, "icon_star"), "do not invent aliases");
        System.out.println("PASS: " + assertions + " legacy placeholder/YAML lookup assertions");
    }
}
