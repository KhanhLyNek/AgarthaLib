package com.praya.agarthalib.modern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

    private static final NamespacedKey RAW_LEGACY_LORE =
            Objects.requireNonNull(NamespacedKey.fromString("agarthalib:legacy_lore_raw"));
    private static final NamespacedKey VISIBLE_LEGACY_LORE =
            Objects.requireNonNull(NamespacedKey.fromString("agarthalib:legacy_lore_visible"));
    private static final int MAX_BACKUP_BYTES = 131072;
    private static final String MYITEMS_STATS_MARKER = "§0§1§r";
    private static final String MYITEMS_VALUE_MARKER = "§0§2§r";

    private Paper26ItemTextColors() {}

    public static String displayName(final ItemMeta meta) {
        final Component name = meta.displayName();
        return hasRgb(name) ? RGB.serialize(name) : meta.getDisplayName();
    }

    public static List<String> lore(final ItemMeta meta) {
        final List<String> visible = visibleLore(meta);
        // Legacy hidden formatting markers (§0§1§r, etc.) are display-state
        // no-ops. Paper's Component roundtrip may normalize them away. Keep
        // the original marked lore only while visible content still matches.
        final PersistentDataContainer data = meta.getPersistentDataContainer();
        final byte[] rawBytes = data.get(RAW_LEGACY_LORE, PersistentDataType.BYTE_ARRAY);
        final byte[] expectedBytes = data.get(VISIBLE_LEGACY_LORE, PersistentDataType.BYTE_ARRAY);
        if (rawBytes != null && expectedBytes != null &&
                rawBytes.length <= MAX_BACKUP_BYTES && expectedBytes.length <= MAX_BACKUP_BYTES) {
            try {
                final List<String> expected = com.lymeowz.lib.item.StringListCodec.decode(expectedBytes);
                final List<String> original = com.lymeowz.lib.item.StringListCodec.decode(rawBytes);
                if (expected.equals(visible) && original.size() == visible.size()) {
                    return original;
                }
            } catch (IllegalStateException ignored) {
                // Malformed/stale PDC metadata cannot make regular lore unreadable.
            }
        }
        return visible;
    }

    private static List<String> visibleLore(final ItemMeta meta) {
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
        final PersistentDataContainer data = meta.getPersistentDataContainer();
        data.remove(RAW_LEGACY_LORE);
        data.remove(VISIBLE_LEGACY_LORE);
        // Store sidecars only for the old internal lore marker format. Do not
        // rewrite/override unrelated plugin items or all other lore lines.
        if (value != null && value.stream().anyMatch(line -> line != null &&
                (line.contains(MYITEMS_STATS_MARKER) || line.contains(MYITEMS_VALUE_MARKER)))) {
            final byte[] raw = com.lymeowz.lib.item.StringListCodec.encode(value);
            final List<String> visible = visibleLore(meta);
            if (visible != null) {
                final byte[] canonical = com.lymeowz.lib.item.StringListCodec.encode(visible);
                if (raw.length <= MAX_BACKUP_BYTES && canonical.length <= MAX_BACKUP_BYTES) {
                    data.set(RAW_LEGACY_LORE, PersistentDataType.BYTE_ARRAY, raw);
                    data.set(VISIBLE_LEGACY_LORE, PersistentDataType.BYTE_ARRAY, canonical);
                }
            }
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
