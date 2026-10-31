package com.lymeowz.lib.item;

import core.praya.agarthalib.builder.item.ItemList;
import core.praya.agarthalib.builder.item.ItemSingle;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Dependency-free invalid/empty-state tests. Non-null ItemStack creation
 * requires Paper's runtime registry (not available in this JVM fixture).
 */
public final class ItemViewSelfTest {
    private ItemViewSelfTest() { }

    private static void check(final boolean condition, final String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void immutable(final List<ItemStack> snapshot) {
        try {
            snapshot.clear();
            throw new AssertionError("Canonical item list must be unmodifiable");
        } catch (UnsupportedOperationException expected) { }
    }

    public static void main(final String[] args) {
        final ItemList legacyList = new ItemList((ItemStack) null, (ItemStack) null);
        final ItemView view = legacyList;
        check(view.currentSnapshot() == null,
                "current snapshot of a null legacy item is null");
        final List<ItemStack> all = view.itemSnapshots();
        check(all.size() == 2 && all.get(0) == null && all.get(1) == null,
                "list view preserves legacy order and null entries");
        immutable(all);

        legacyList.addItemStack(null);
        check(all.size() == 2 && view.itemSnapshots().size() == 3,
                "canonical snapshot list is detached from mutable legacy storage");
        legacyList.clearItemStack();
        check(view.currentSnapshot() == null && view.itemSnapshots().isEmpty(),
                "cleared legacy list exposes safe empty snapshots");
        check(all.size() == 2, "previous snapshot retains its original size");

        final ItemSingle single = new ItemSingle(null);
        final ItemView singleView = single;
        check(singleView.currentSnapshot() == null && singleView.itemSnapshots().isEmpty(),
                "empty single-item builder returns safe views");
        immutable(singleView.itemSnapshots());
        single.setItemStack(null);
        check(single.getItemStack() == null, "original mutable API still works");

        final ItemList emptyList = new ItemList();
        check(emptyList.currentSnapshot() == null && emptyList.itemSnapshots().isEmpty(),
                "empty builder snapshots remain empty");
        System.out.println("PASS: item builder null/empty snapshots and immutable copy boundaries");
    }
}
