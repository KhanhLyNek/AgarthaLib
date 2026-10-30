package com.praya.agarthalib.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import java.util.function.UnaryOperator;

/**
 * Native Paper item hover-data components. Use instead of legacy raw SNBT.
 * Does not modify original items, commands, or the historic JSON DSL.
 */
public final class Paper26HoverItems {
    private Paper26HoverItems() {}

    public static Component component(final String text, final ItemStack item) {
        if (text == null || item == null || item.getType().isAir()
                || item.getAmount() < 1 || item.getAmount() > 99) return null;
        return Component.text(text).hoverEvent(item.asHoverEvent(UnaryOperator.identity()));
    }

    public static String json(final String text, final ItemStack item) {
        final Component rich = component(text, item);
        return rich == null ? null : GsonComponentSerializer.gson().serialize(rich);
    }
}
