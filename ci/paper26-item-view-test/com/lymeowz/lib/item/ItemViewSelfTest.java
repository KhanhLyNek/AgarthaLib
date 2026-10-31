package com.lymeowz.lib.item;

import core.praya.agarthalib.builder.item.ItemList;
import core.praya.agarthalib.builder.item.ItemSingle;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** No server inventory or disk access; checks detached copies on the real Bukkit API. */
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
        final ItemStack original = new ItemStack(Material.STONE, 2);
        final ItemList legacyList = new ItemList(original);
        final ItemView view = legacyList;
        final ItemStack current = view.currentSnapshot();
        check(current != original && current.getAmount() == 2,
                "current stack snapshot is an independent clone");
        final List<ItemStack> all = view.itemSnapshots();
        check(all.size() == 1 && all.get(0) != original,
                "list snapshot preserves order and detaches stored ItemStacks");
        immutable(all);
        original.setAmount(7);
        check(all.get(0).getAmount() == 2 && current.getAmount() == 2,
                "existing snapshot is unaffected by mutation of the stored item");
        all.get(0).setAmount(1);
        check(legacyList.getItemList().get(0).getAmount() == 7,
                "mutating snapshot ItemStack does not change legacy storage");

        legacyList.addItemStack(null);
        final List<ItemStack> nulls = view.itemSnapshots();
        check(nulls.size() == 2 && nulls.get(1) == null,
                "list snapshots preserve legacy null entries and ordering");

        final ItemSingle single = new ItemSingle(original);
        final ItemView singleView = single;
        final ItemStack singleCopy = singleView.currentSnapshot();
        check(singleCopy != original && singleCopy.getAmount() == 7,
                "single-item builder exposes a clone, not a live reference");
        immutable(singleView.itemSnapshots());
        single.clearItemStack();
        check(singleView.currentSnapshot() == null && singleView.itemSnapshots().isEmpty(),
                "empty single-item builder returns safe empty snapshots");
        final ItemList emptyList = new ItemList();
        check(emptyList.currentSnapshot() == null && emptyList.itemSnapshots().isEmpty(),
                "empty list builder snapshots remain empty");
        check(legacyList.getItemStack() == original,
                "original mutable Java API remains unchanged");
        System.out.println("PASS: legacy item list/single read-only cloned API views");
    }
}
