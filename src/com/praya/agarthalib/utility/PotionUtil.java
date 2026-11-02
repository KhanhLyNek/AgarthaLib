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
import com.praya.agarthalib.modern.Paper26PotionAliases;

public class PotionUtil
{
    // Keep existing configuration names while mapping to current potion registry names.
    public static final PotionType getPotionTypeByOriginalName(final String potion) {
        if (potion == null) {
            return null;
        }
        final String name = Paper26PotionAliases.potionTypeName(potion);
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
    
    // Keep legacy 1..32 IDs and YAML aliases stable while resolving against
    // the modern registry instead of the removed Bukkit ID API.
    public static final PotionEffectType getPotionEffectType(final String key) {
        final String minecraftKey = Paper26PotionAliases.effectKey(key);
        return minecraftKey == null ? null
                : Registry.MOB_EFFECT.get(NamespacedKey.minecraft(minecraftKey));
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
