package com.praya.agarthalib.modern;

import org.bukkit.inventory.ItemStack;
import java.util.Base64;

/**
 * A lossless, DataVersion-aware snapshot channel for already-loadable Bukkit
 * ItemStacks. Does NOT decode arbitrary pre-1.20.5 unversioned raw NBT tags.
 * Restore only from trusted backups; never run automatic inventory migration.
 */
public final class Paper26ItemSnapshots {
    private static final int MAX_SNAPSHOT_BYTES = 2 * 1024 * 1024;
    private Paper26ItemSnapshots() {}

    public static String snapshot(final ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        final byte[] bytes = item.serializeAsBytes();
        if (bytes.length == 0 || bytes.length > MAX_SNAPSHOT_BYTES)
            throw new IllegalArgumentException("Item snapshot size invalid");
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** Preserves the Paper DataVersion converter. Does not write to inventory. */
    public static ItemStack restore(final String base64) {
        if (base64 == null || base64.isBlank() || base64.length() > ((MAX_SNAPSHOT_BYTES + 2) / 3) * 4)
            throw new IllegalArgumentException("Missing or oversized item snapshot");
        final byte[] bytes = Base64.getDecoder().decode(base64);
        if (bytes.length == 0 || bytes.length > MAX_SNAPSHOT_BYTES)
            throw new IllegalArgumentException("Invalid item snapshot length");
        return ItemStack.deserializeBytes(bytes);
    }
}
