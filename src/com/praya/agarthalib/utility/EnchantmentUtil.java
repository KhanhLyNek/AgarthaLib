// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.utility;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import core.praya.agarthalib.enums.branch.EnchantmentEnum;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentUtil
{
    public static final Enchantment getEnchantment(final String enchantment) {
        final Enchantment enchantmentPrimary = getEnchantmentByOriginalName(enchantment);
        if (enchantmentPrimary != null) {
            return enchantmentPrimary;
        }
        final EnchantmentEnum enchantmentEnum = EnchantmentEnum.getEnchantmentEnum(enchantment);
        return (enchantmentEnum != null) ? enchantmentEnum.getEnchantment() : null;
    }
    
    public static final Enchantment getEnchantmentByOriginalName(final String enchantment) {
        if (enchantment == null || enchantment.isBlank()) {
            return null;
        }
        // The modern Paper registry contains vanilla and plugin-defined
        // enchantments. Preserve the original case-insensitive *unqualified*
        // lookup across all namespaces; fully-qualified keys also work.
        for (final Enchantment value : RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)) {
            final org.bukkit.NamespacedKey key = value.getKey();
            if (key.getKey().equalsIgnoreCase(enchantment)
                    || key.toString().equalsIgnoreCase(enchantment)) {
                return value;
            }
        }
        return null;
    }
    
    public static final boolean isEnchantmentExists(final String enchantment) {
        return getEnchantment(enchantment) != null;
    }
}
