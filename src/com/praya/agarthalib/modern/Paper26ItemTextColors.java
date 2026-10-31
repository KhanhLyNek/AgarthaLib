package com.praya.agarthalib.modern;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Precision-preserving Adventure RGB bridge for the original EquipmentUtil
 * String API. The standard Paper String getters may downsample RGB, while
 * the Component metadata stores the actual 24-bit color.
 *
 * Only RGB item names/lores take the new path. Existing legacy names/lore
 * still use the old ItemMeta String APIs and original cancellable event.
 */
public final class Paper26ItemTextColors {
    private static final LegacyComponentSerializer RGB =
            LegacyComponentSerializer.builder()
                    .character('§')
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    private Paper26ItemTextColors() {}

    public static String displayName(final ItemMeta meta) {
        final Component name = meta.displayName();
        return hasRgb(name) ? RGB.serialize(name) : meta.getDisplayName();
    }

    public static List<String> lore(final ItemMeta meta) {
        final List<Component> components = meta.lore();
        if (components != null && components.stream().anyMatch(Paper26ItemTextColors::hasRgb)) {
            final List<String> result = new ArrayList<>(components.size());
            for (final Component component : components) result.add(RGB.serialize(component));
            return result;
        }
        return meta.getLore();
    }

    public static void displayName(final ItemMeta meta, final String value) {
        if (hasRgbEncoding(value)) {
            meta.displayName(RGB.deserialize(value));
        } else {
            meta.setDisplayName(value);
        }
    }

    public static void lore(final ItemMeta meta, final List<String> value) {
        if (value != null && value.stream().anyMatch(Paper26ItemTextColors::hasRgbEncoding)) {
            final List<Component> components = new ArrayList<>(value.size());
            for (final String line : value) components.add(RGB.deserialize(line));
            meta.lore(components);
        } else {
            meta.setLore(value);
        }
    }

    private static boolean hasRgbEncoding(final String value) {
        return value != null && (value.contains("§x§") || value.contains("§X§"));
    }

    private static boolean hasRgb(final Component component) {
        if (component == null) return false;
        final TextColor color = component.color();
        if (color != null && !(color instanceof NamedTextColor)) return true;
        for (final Component child : component.children()) {
            if (hasRgb(child)) return true;
        }
        return false;
    }
}
