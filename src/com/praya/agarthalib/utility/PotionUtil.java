// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.utility;

import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import core.praya.agarthalib.enums.branch.PotionTypeEnum;
import org.bukkit.Color;
import org.bukkit.potion.PotionType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import java.util.Locale;

public class PotionUtil
{
    // Keep existing configuration names while mapping to current potion registry names.
    public static final PotionType getPotionTypeByOriginalName(final String potion) {
        if (potion == null) {
            return null;
        }
        String name = potion.toUpperCase(Locale.ROOT);
        switch (name) {
            case "INSTANT_DAMAGE": name = "HARMING"; break;
            case "INSTANT_HEAL": name = "HEALING"; break;
            case "JUMP": name = "LEAPING"; break;
            case "REGEN": name = "REGENERATION"; break;
            case "SPEED": name = "SWIFTNESS"; break;
            default: break;
        }
        try {
            return PotionType.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static final PotionType getPotionTypeByColor(final Color color) {
        if (color != null) {
            final int red = color.getRed();
            final int green = color.getGreen();
            final int blue = color.getBlue();
            PotionType potionType = PotionType.WATER;
            double lastDif = -1.0;
            PotionTypeEnum[] values;
            for (int length = (values = PotionTypeEnum.values()).length, i = 0; i < length; ++i) {
                final PotionTypeEnum key = values[i];
                final PotionType potion = key.getPotionType();
                if (potion != null) {
                    final Color keyColor = key.getColor();
                    final double difRed = Math.abs(red - keyColor.getRed());
                    final double difGreen = Math.abs(green - keyColor.getGreen());
                    final double difBlue = Math.abs(blue - keyColor.getBlue());
                    final double dif = difRed + difGreen + difBlue;
                    if (lastDif == -1.0 || dif < lastDif) {
                        potionType = potion;
                        lastDif = dif;
                    }
                }
            }
            return potionType;
        }
        return null;
    }
    
    // Numeric IDs were supported in older MyItems configurations.
    // Resolve their historical meanings using namespaced registry keys, not removed IDs.
    private static final String[] LEGACY_EFFECT_KEYS = {
        null, "speed", "slowness", "haste", "mining_fatigue", "strength",
        "instant_health", "instant_damage", "jump_boost", "nausea",
        "regeneration", "resistance", "fire_resistance", "water_breathing",
        "invisibility", "blindness", "night_vision", "hunger", "weakness",
        "poison", "wither", "health_boost", "absorption", "saturation",
        "glowing", "levitation", "luck", "unluck", "slow_falling",
        "conduit_power", "dolphins_grace", "bad_omen", "hero_of_the_village"
    };

    public static final PotionEffectType getPotionEffectType(final String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        String name = key.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (MathUtil.isNumber(name)) {
            try {
                int id = Integer.parseInt(name);
                if (id < 1 || id >= LEGACY_EFFECT_KEYS.length) {
                    return null;
                }
                name = LEGACY_EFFECT_KEYS[id];
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (name.startsWith("minecraft:")) {
            name = name.substring("minecraft:".length());
        }
        switch (name) {
            case "slow": name = "slowness"; break;
            case "fast_digging": name = "haste"; break;
            case "slow_digging": name = "mining_fatigue"; break;
            case "increase_damage": name = "strength"; break;
            case "damage_resistance": name = "resistance"; break;
            case "heal": name = "instant_health"; break;
            case "harm": name = "instant_damage"; break;
            case "jump": name = "jump_boost"; break;
            case "confusion": name = "nausea"; break;
            default: break;
        }
        return Registry.MOB_EFFECT.get(NamespacedKey.minecraft(name));
    }

    public static final PotionEffectType getPoisonType(final LivingEntity victims) {
        if (victims == null) {
            return null;
        }
        switch (victims.getType()) {
            case ZOMBIE: {
                return PotionEffectType.WITHER;
            }
            case ZOMBIFIED_PIGLIN: {
                return PotionEffectType.WITHER;
            }
            case WITHER_SKULL: {
                return PotionEffectType.WITHER;
            }
            case SKELETON: {
                return PotionEffectType.WITHER;
            }
            case SPIDER: {
                return PotionEffectType.WITHER;
            }
            case CAVE_SPIDER: {
                return PotionEffectType.WITHER;
            }
            default: {
                return PotionEffectType.POISON;
            }
        }
    }
    
    public static final PotionEffect createPotion(final PotionEffectType potion, final int duration) {
        return createPotion(potion, duration, 1, false, false);
    }
    
    public static final PotionEffect createPotion(final PotionEffectType potion, final int duration, final int grade) {
        return createPotion(potion, duration, grade, false, false);
    }
    
    public static final PotionEffect createPotion(final PotionEffectType potion, final int duration, final int grade, final boolean ambient, final boolean particles) {
        return (potion != null) ? new PotionEffect(potion, duration, grade - 1, ambient, particles) : null;
    }
}
