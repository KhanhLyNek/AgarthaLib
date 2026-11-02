package com.praya.agarthalib.modern;

import com.google.common.collect.ImmutableMultimap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Modern Bukkit/Paper API operations which replace NMS item attributes and
 * written-book packets. Kept independent of all legacy AgarthaLib classes so
 * GitHub Actions can type-check it on Paper 26.3 before NMS removal is complete.
 */
public final class Paper26ItemMeta {
    private Paper26ItemMeta() {
    }

    private static boolean valid(final ItemStack item) {
        return item != null && !item.getType().isAir();
    }

    public static void addAttribute(final ItemStack item, final Attribute attribute,
                                    final NamespacedKey modifierId, final double amount,
                                    final EquipmentSlotGroup slot) {
        if (!valid(item) || attribute == null || modifierId == null || slot == null) {
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        final Collection<AttributeModifier> existing = meta.getAttributeModifiers(attribute);
        if (existing != null) {
            for (final AttributeModifier old : new ArrayList<>(existing)) {
                if (modifierId.equals(old.getKey())) {
                    meta.removeAttributeModifier(attribute, old);
                }
            }
        }
        meta.addAttributeModifier(attribute, new AttributeModifier(
                modifierId, amount, AttributeModifier.Operation.ADD_NUMBER, slot));
        item.setItemMeta(meta);
    }

    public static void clearAttributes(final ItemStack item) {
        if (!valid(item)) {
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // An explicit empty collection suppresses custom attribute modifiers.
            meta.setAttributeModifiers(ImmutableMultimap.of());
            item.setItemMeta(meta);
        }
    }

    public static void setUnbreakable(final ItemStack item, final boolean unbreakable) {
        if (!valid(item)) {
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
    }

    public static boolean isUnbreakable(final ItemStack item) {
        return valid(item) && item.hasItemMeta() && item.getItemMeta().isUnbreakable();
    }

    public static ItemStack createBook(final String name, final int amount, final String title,
                                       final String author, final String... pages) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK, Math.max(1, amount));
        final BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta != null) {
            if (name != null) {
                // Preserve legacy display-name contents exactly during migration.
                meta.setDisplayName(name);
            }
            meta.setTitle(title == null ? "" : title);
            meta.setAuthor(author == null ? "" : author);
            if (pages != null) {
                meta.setPages(pages);
            }
            book.setItemMeta(meta);
        }
        return book;
    }

    public static void openBook(final ItemStack book, final Player player) {
        if (valid(book) && player != null) {
            player.openBook(book);
        }
    }
}
