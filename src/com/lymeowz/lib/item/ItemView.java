package com.lymeowz.lib.item;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Read-only copies of the legacy library's live item builders.
 * Returned ItemStacks are cloned so callers cannot edit the stored originals.
 * Neither existing builder mutators nor their Java descriptors are changed.
 */
public interface ItemView {
    /** A clone of the currently selected stack, or null if none exists. */
    ItemStack currentSnapshot();

    /** Immutable collection of independent ItemStack copies, preserving order. */
    List<ItemStack> itemSnapshots();
}
