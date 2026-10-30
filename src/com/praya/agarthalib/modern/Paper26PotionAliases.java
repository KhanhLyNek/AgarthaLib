package com.praya.agarthalib.modern;

import java.util.Locale;

/**
 * Pure, Bukkit-independent compatibility layer for historically configured
 * potion names and numeric effect IDs.
 *
 * NEVER renumber old IDs: those are saved in MyItems user YAML.
 * Registry lookups happen in PotionUtil only after normalization.
 */
public final class Paper26PotionAliases {
    private static final String[] LEGACY_EFFECT_KEYS = {
            null, "speed", "slowness", "haste", "mining_fatigue", "strength",
            "instant_health", "instant_damage", "jump_boost", "nausea",
            "regeneration", "resistance", "fire_resistance", "water_breathing",
            "invisibility", "blindness", "night_vision", "hunger", "weakness",
            "poison", "wither", "health_boost", "absorption", "saturation",
            "glowing", "levitation", "luck", "unluck", "slow_falling",
            "conduit_power", "dolphins_grace", "bad_omen", "hero_of_the_village"
    };

    private Paper26PotionAliases() {
    }

    public static String effectKey(final String original) {
        if (original == null || original.isBlank()) {
            return null;
        }
        String key = original.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (key.startsWith("minecraft:")) {
            key = key.substring("minecraft:".length());
        }

        if (key.matches("[+-]?[0-9]+")) {
            try {
                final int id = Integer.parseInt(key);
                return id > 0 && id < LEGACY_EFFECT_KEYS.length
                        ? LEGACY_EFFECT_KEYS[id] : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return switch (key) {
            case "slow" -> "slowness";
            case "fast_digging" -> "haste";
            case "slow_digging" -> "mining_fatigue";
            case "increase_damage" -> "strength";
            case "damage_resistance" -> "resistance";
            case "heal" -> "instant_health";
            case "harm" -> "instant_damage";
            case "jump" -> "jump_boost";
            case "confusion" -> "nausea";
            default -> key;
        };
    }

    /**
     * Preserve the original PotionTypeEnum mappings. New prefixed variants
     * are passed through untouched to PotionType.valueOf for validation.
     */
    public static String potionTypeName(final String original) {
        if (original == null) {
            return null;
        }
        final String name = original.toUpperCase(Locale.ROOT);
        return switch (name) {
            case "INSTANT_DAMAGE" -> "HARMING";
            case "INSTANT_HEAL" -> "HEALING";
            case "JUMP" -> "LEAPING";
            case "REGEN" -> "REGENERATION";
            case "SPEED" -> "SWIFTNESS";
            default -> name;
        };
    }
}
