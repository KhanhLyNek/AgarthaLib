package com.praya.agarthalib.modern;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only legacy raw-NBT bridge for *already loadable* Paper ItemStacks.
 *
 * The 1.12 implementation wrote List<String>; 1.15 wrote List<Compound{stat}>.
 * The vanilla 1.20.5 DataFixer moves non-vanilla data into custom_data.
 * This reader never writes back to the item, changes an inventory, or guesses
 * that an arbitrary historical pre-DataVersion blob is safe to deserialize.
 */
public final class Paper26LegacyItemTags {
    private static final int MAX_ITEM_BYTES = 2 * 1024 * 1024;
    private static final int MAX_LIST_SIZE = 4096;
    private Paper26LegacyItemTags() {}

    /** Bounded NBT read; a corrupt or unsupported item returns null. */
    public static CompoundBinaryTag customData(final ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        try {
            final byte[] bytes = item.serializeAsBytes();
            if (bytes.length == 0 || bytes.length > MAX_ITEM_BYTES) return null;
            // Paper's ItemStack.serializeAsBytes() uses GZIP-compressed NBT.
            // Keep an explicit fallback for standalone uncompressed fixtures,
            // but never guess a codec from untrusted, arbitrary input.
            final BinaryTagIO.Compression compression =
                    bytes.length >= 2 && (bytes[0] & 0xff) == 0x1f
                            && (bytes[1] & 0xff) == 0x8b
                            ? BinaryTagIO.Compression.GZIP
                            : BinaryTagIO.Compression.NONE;
            final CompoundBinaryTag root = BinaryTagIO.reader(MAX_ITEM_BYTES * 2L)
                    .read(new ByteArrayInputStream(bytes), compression);
            return locate(root);
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            return null;
        }
    }

    /** Read-only extraction from a validated NBT root; usable by offline tests. */
    public static CompoundBinaryTag locate(final CompoundBinaryTag root) {
        if (root == null) return null;
        final BinaryTag components = root.get("components");
        if (components instanceof CompoundBinaryTag cmp) {
            final BinaryTag modern = cmp.get("minecraft:custom_data");
            if (modern instanceof CompoundBinaryTag custom) return custom;
        }
        final BinaryTag old = root.get("tag");
        return old instanceof CompoundBinaryTag legacy ? legacy : null;
    }

    private static BinaryTag get(final CompoundBinaryTag data,
                                 final String key, final String parent) {
        if (data == null || key == null || key.isEmpty()) return null;
        final CompoundBinaryTag node;
        if (parent != null) {
            final BinaryTag nested = data.get(parent);
            if (!(nested instanceof CompoundBinaryTag compound)) return null;
            node = compound;
        } else {
            node = data;
        }
        return node.get(key);
    }

    public static String string(final CompoundBinaryTag data,
                                final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        return value instanceof StringBinaryTag tag ? tag.value() : null;
    }

    public static Integer integer(final CompoundBinaryTag data,
                                  final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        return value instanceof IntBinaryTag tag ? tag.value() : null;
    }

    public static Double decimal(final CompoundBinaryTag data,
                                 final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        return value instanceof DoubleBinaryTag tag ? tag.value() : null;
    }

    public static Long longNumber(final CompoundBinaryTag data,
                                  final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        return value instanceof LongBinaryTag tag ? tag.value() : null;
    }

    public static Boolean booleanValue(final CompoundBinaryTag data,
                                       final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        return value instanceof ByteBinaryTag tag ? tag.value() != 0 : null;
    }

    /**
     * Both historical encodings are supported. A malformed / mixed list is
     * rejected whole, never partially mapped to a different set of skills.
     */
    public static List<String> strings(final CompoundBinaryTag data,
                                       final String key, final String parent) {
        final BinaryTag value = get(data, key, parent);
        if (!(value instanceof ListBinaryTag list) || list.size() > MAX_LIST_SIZE)
            return null;
        final List<String> result = new ArrayList<>(list.size());
        for (final BinaryTag entry : list) {
            if (entry instanceof StringBinaryTag text) {
                result.add(text.value());
            } else if (entry instanceof CompoundBinaryTag element
                    && element.get("stat") instanceof StringBinaryTag stat) {
                result.add(stat.value());
            } else {
                return null;
            }
        }
        return result;
    }
}
