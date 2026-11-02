package com.praya.agarthalib.packet.arrow;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/** Paper 26.3 arrows. Preserves the original colour-only tipped arrow contract. */
public final class PacketArrow_Paper26_3 extends HandlerPacket
        implements PacketArrowNormal, PacketArrowModification {
    public PacketArrow_Paper26_3(final AgarthaLib plugin) { super(plugin); }

    @Override public ItemStack createNormalArrow() { return new ItemStack(Material.ARROW); }
    @Override public ItemStack createSpectralArrow() { return new ItemStack(Material.SPECTRAL_ARROW); }

    @Override public ItemStack createTippedArrow(final Color color) {
        final ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        final ItemMeta itemMeta = arrow.getItemMeta();
        if (itemMeta instanceof PotionMeta meta) {
            meta.clearCustomEffects();
            meta.setColor(color);
            // HIDE_POTION_EFFECTS was replaced by the modern tooltip flag.
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            arrow.setItemMeta(meta);
        }
        return arrow;
    }

    @Override public ItemStack createTippedArrow(final int red, final int green, final int blue) {
        return createTippedArrow(Color.fromRGB(clamp(red), clamp(green), clamp(blue)));
    }

    private static int clamp(final int channel) {
        return Math.max(0, Math.min(255, channel));
    }
}
