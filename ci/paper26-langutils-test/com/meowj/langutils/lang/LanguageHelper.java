package com.meowj.langutils.lang;

import org.bukkit.entity.EntityType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** CI-only fake for exactly matching the historical LangUtils method signatures. */
public final class LanguageHelper {
    public static int calls;
    private LanguageHelper() {}

    public static String getItemDisplayName(final ItemStack item, final String locale) {
        calls++;
        return "display:" + locale;
    }

    public static String getItemName(final ItemStack item, final Player player) {
        calls++;
        return player == null ? "item:offline" : "item:online";
    }

    public static String getEntityName(final EntityType entityType, final String locale) {
        calls++;
        return entityType.name() + ":" + locale;
    }

    public static String getEnchantmentLevelName(final int level, final String locale) {
        calls++;
        return level + ":" + locale;
    }

    public static String getEnchantmentDisplayName(final Enchantment enchantment,
                                                   final int level, final String locale) {
        calls++;
        return level + ":ench:" + locale;
    }

    public static String translateToLocal(final String name, final String locale) {
        calls++;
        return name + ":" + locale;
    }
}
