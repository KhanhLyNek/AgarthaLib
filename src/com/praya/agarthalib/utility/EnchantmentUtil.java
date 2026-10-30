// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.utility;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
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
        // Paper 26.3 no longer uses the old Enchantment enum/name model.
        // Only the unqualified vanilla key matched on the earlier modern
        // path. Preserve that grammar, while resolving through the registry.
        final String name = enchantment.toLowerCase(java.util.Locale.ROOT);
        try {
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                    .get(RegistryKey.ENCHANTMENT.typedKey(Key.key("minecraft:" + name)));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static final boolean isEnchantmentExists(final String enchantment) {
        return getEnchantment(enchantment) != null;
    }
}
