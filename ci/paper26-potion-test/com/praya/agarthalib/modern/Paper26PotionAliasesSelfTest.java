package com.praya.agarthalib.modern;

import java.util.Locale;

/** Executable Java 25 regression checks for original MyItems potion IDs. */
public final class Paper26PotionAliasesSelfTest {
    private static int assertions;

    private static void same(final String expected, final String actual, final String input) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("potion '" + input + "' expected '"
                    + expected + "' but received '" + actual + "'");
        }
        assertions++;
    }

    public static void main(final String[] args) {
        final String[] originalIds = {
                null, "speed", "slowness", "haste", "mining_fatigue", "strength",
                "instant_health", "instant_damage", "jump_boost", "nausea",
                "regeneration", "resistance", "fire_resistance", "water_breathing",
                "invisibility", "blindness", "night_vision", "hunger", "weakness",
                "poison", "wither", "health_boost", "absorption", "saturation",
                "glowing", "levitation", "luck", "unluck", "slow_falling",
                "conduit_power", "dolphins_grace", "bad_omen", "hero_of_the_village"
        };

        for (int i = 1; i < originalIds.length; i++) {
            same(originalIds[i], Paper26PotionAliases.effectKey(Integer.toString(i)), Integer.toString(i));
            same(originalIds[i], Paper26PotionAliases.effectKey("0" + i), "0" + i);
        }
        same(null, Paper26PotionAliases.effectKey("0"), "0");
        same(null, Paper26PotionAliases.effectKey("33"), "33");
        same(null, Paper26PotionAliases.effectKey("-1"), "-1");
        same(null, Paper26PotionAliases.effectKey("99999999999999999999"), "overflow");
        same(null, Paper26PotionAliases.effectKey(""), "empty");
        same(null, Paper26PotionAliases.effectKey(null), "null");
        same("slowness", Paper26PotionAliases.effectKey("SLOW"), "SLOW");
        same("slowness", Paper26PotionAliases.effectKey("minecraft:slow"), "minecraft:slow");
        same("haste", Paper26PotionAliases.effectKey("FAST_DIGGING"), "FAST_DIGGING");
        same("mining_fatigue", Paper26PotionAliases.effectKey("SLOW_DIGGING"), "SLOW_DIGGING");
        same("strength", Paper26PotionAliases.effectKey("INCREASE_DAMAGE"), "INCREASE_DAMAGE");
        same("resistance", Paper26PotionAliases.effectKey("DAMAGE_RESISTANCE"), "DAMAGE_RESISTANCE");
        same("instant_health", Paper26PotionAliases.effectKey("HEAL"), "HEAL");
        same("instant_damage", Paper26PotionAliases.effectKey("HARM"), "HARM");
        same("jump_boost", Paper26PotionAliases.effectKey("JUMP"), "JUMP");
        same("nausea", Paper26PotionAliases.effectKey("CONFUSION"), "CONFUSION");
        same("poison", Paper26PotionAliases.effectKey(" minecraft:POISON "), "namespaced poison");
        same("trial_omen", Paper26PotionAliases.effectKey("TRIAL_OMEN"), "modern passthrough");
        same("slowness", Paper26PotionAliases.effectKey("Slowness"), "case stable");
        same("fire_resistance", Paper26PotionAliases.effectKey("Fire Resistance"), "spaces");

        final String[][] potionTypes = {
                {"INSTANT_DAMAGE", "HARMING"},
                {"INSTANT_HEAL", "HEALING"},
                {"JUMP", "LEAPING"},
                {"REGEN", "REGENERATION"},
                {"SPEED", "SWIFTNESS"},
                {"AWKWARD", "AWKWARD"},
                {"WATER", "WATER"},
                {"FIRE_RESISTANCE", "FIRE_RESISTANCE"},
                {"STRONG_HARMING", "STRONG_HARMING"}
        };
        for (final String[] sample : potionTypes) {
            same(sample[1], Paper26PotionAliases.potionTypeName(sample[0]), sample[0]);
            same(sample[1], Paper26PotionAliases.potionTypeName(
                    sample[0].toLowerCase(Locale.ROOT)), sample[0] + " lower");
        }
        same(null, Paper26PotionAliases.potionTypeName(null), "null potion");

        System.out.println("PASS: " + assertions
                + " legacy potion alias/ID round-trip assertions");
    }
}
