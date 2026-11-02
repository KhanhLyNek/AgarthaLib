package com.praya.agarthalib.modern;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/** Construct titled inventories before opening; never reassign menu holders. */
public final class Paper26InventoryFactory {
    private Paper26InventoryFactory() {}
    public static Inventory createChest(final InventoryHolder holder, final int slots, final String title) {
        if (title == null || slots < 9 || slots > 54 || slots % 9 != 0)
            throw new IllegalArgumentException("Expected a title and a 9-54-slot chest");
        return Bukkit.createInventory(holder, slots, title);
    }
}
