package com.praya.agarthalib.packet.inventory;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import core.praya.agarthalib.builder.bridge.WindowTools;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/** No NMS screen packets or unsupported InventoryView.setTitle calls. */
public final class PacketInventory_Paper26_3 extends HandlerPacket implements WindowTools {
    private final AgarthaLib owner;
    private boolean warned;
    public PacketInventory_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
        this.owner = plugin;
    }
    @Override
    public void setTitleWindow(final Player player, final String title) {
        if (player == null || title == null) return;
        // Reopening a copy would break holders, callbacks, and item state.
        // The legacy dynamic rename has no safe equivalent on Paper 26.3.
        if (!warned && owner != null) {
            warned = true;
            owner.getLogger().warning("Cannot safely rename an already-open inventory on Paper 26.3; create it with the title before opening.");
        }
    }
    @Override
    public void setTitleWindow(final Inventory inventory, final String title) {
        if (inventory == null || title == null) return;
        for (final HumanEntity human : inventory.getViewers()) {
            if (human instanceof Player player) setTitleWindow(player, title);
        }
    }
}
