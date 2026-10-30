package com.praya.agarthalib.modern;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.StandardCharsets;

/**
 * Paper-backed storage for plugin-owned custom item data.
 *
 * Keys intentionally encode the original key and optional parent exactly.
 * This avoids collisions from case folding and invalid NamespacedKey chars.
 * It does not claim to decode historic raw NBT written by older servers.
 */
public final class Paper26ItemData {
    private static final String NAMESPACE = "agarthalib";

    private Paper26ItemData() {
    }

    public static NamespacedKey key(final String legacyKey, final String parent) {
        if (legacyKey == null) {
            throw new IllegalArgumentException("Custom item key cannot be null");
        }
        final String encoded = parent == null
                ? "n_" + hex(legacyKey)
                : "p_" + hex(parent) + "_" + hex(legacyKey);
        return new NamespacedKey(NAMESPACE, "legacy_" + encoded);
    }

    /**
     * Removing a PDC key must also hide the historical raw NBT value:
     * otherwise /nbtclear resurrects old weapon skill or stat metadata.
     * This writes only a plugin-owned tombstone; raw legacy NBT is preserved
     * so the user can still recover the original item from a backup.
     */
    public static NamespacedKey removalKey(final String legacyKey, final String parent) {
        final NamespacedKey original = key(legacyKey, parent);
        return new NamespacedKey(NAMESPACE, "deleted_" + original.getKey());
    }

    public static boolean isRemoved(final String legacyKey, final String parent,
                                    final ItemStack item) {
        if (legacyKey == null || !valid(item)) return false;
        final Byte marker = item.getPersistentDataContainer().get(
                removalKey(legacyKey, parent), PersistentDataType.BYTE);
        return marker != null && marker == 1;
    }

    private static String hex(final String text) {
        final byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        final char[] output = new char[utf8.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < utf8.length; i++) {
            output[2 * i] = digits[(utf8[i] & 0xff) >>> 4];
            output[2 * i + 1] = digits[utf8[i] & 0xf];
        }
        return new String(output);
    }

    private static boolean valid(final ItemStack item) {
        return item != null && !item.getType().isAir();
    }

    public static <T, Z> void set(final String legacyKey, final String parent,
                                  final ItemStack item, final PersistentDataType<T, Z> type, final Z value) {
        if (legacyKey == null || !valid(item) || value == null) {
            return;
        }
        final NamespacedKey namespacedKey = key(legacyKey, parent);
        final NamespacedKey tombstone = removalKey(legacyKey, parent);
        item.editPersistentDataContainer(pdc -> {
            pdc.set(namespacedKey, type, value);
            pdc.remove(tombstone);
        });
    }

    public static <T, Z> Z get(final String legacyKey, final String parent,
                               final ItemStack item, final PersistentDataType<T, Z> type) {
        if (legacyKey == null || !valid(item)) {
            return null;
        }
        return item.getPersistentDataContainer().get(key(legacyKey, parent), type);
    }

    public static void remove(final String legacyKey, final String parent, final ItemStack item) {
        if (legacyKey == null || !valid(item)) {
            return;
        }
        final NamespacedKey namespacedKey = key(legacyKey, parent);
        final NamespacedKey tombstone = removalKey(legacyKey, parent);
        item.editPersistentDataContainer(pdc -> {
            pdc.remove(namespacedKey);
            pdc.set(tombstone, PersistentDataType.BYTE, (byte) 1);
        });
    }
}
