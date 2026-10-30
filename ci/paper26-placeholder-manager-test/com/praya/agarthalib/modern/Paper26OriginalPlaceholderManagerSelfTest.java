package com.praya.agarthalib.modern;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.placeholder.PlaceholderMemory;

import java.lang.reflect.Constructor;

/**
 * The REAL original PlaceholderMemory + PlaceholderManager source compiled
 * against CI-only supporting types. Verifies the formerly null-only callback.
 */
public final class Paper26OriginalPlaceholderManagerSelfTest {
    private static int assertions;

    private static void same(final String expected, final String actual, final String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + ", actual " + actual);
        }
        assertions++;
    }

    public static void main(final String[] args) throws Exception {
        // Constructor is private in the unchanged singleton. Use reflection
        // only in the fixture, never in plugin runtime code.
        final Constructor<PlaceholderMemory> constructor =
                PlaceholderMemory.class.getDeclaredConstructor(AgarthaLib.class);
        constructor.setAccessible(true);
        final PlaceholderMemory actual = constructor.newInstance((AgarthaLib) null);

        actual.getPlaceholderConfig().setFixtureValue("prefix", "&8[&aAgartha&8]&r");
        actual.getPlaceholderConfig().setFixtureValue("Sign_Done", "✔");
        actual.getPlaceholderConfig().setFixtureValue("nested", "%player_name%");
        actual.getPlaceholderConfig().setFixtureValue("empty", "");

        same("&8[&aAgartha&8]&r", actual.getPlaceholder("prefix"), "raw config lookup");
        same("&8[&aAgartha&8]&r", actual.getPlaceholder("PREFIX"), "mixed-case original config lookup");
        same("&8[&aAgartha&8]&r", actual.getReplacement(null, "PrEfIx"), "callback reads YAML value");
        same("✔", actual.getReplacement(null, "SIGN_DONE"), "original callback case folding");
        same("%player_name%", actual.getReplacement(null, "nested"), "no recursive conversion");
        same("", actual.getReplacement(null, "EMPTY"), "empty value remains valid");
        same(null, actual.getReplacement(null, "absent"), "missing values remain unresolved");
        same(null, actual.getPlaceholder(null), "null identifier");
        same(null, actual.getReplacement(null, null), "null replacement");
        if (actual.getPlaceholderIds().size() != 4) {
            throw new AssertionError("Original placeholder IDs lost: " + actual.getPlaceholderIds());
        }
        assertions++;
        System.out.println("PASS: " + assertions
                + " REAL PlaceholderManager/PlaceholderMemory regression assertions");
    }
}
