package com.praya.agarthalib.modern;

import java.util.List;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

/**
 * Paper 26.3 model-only operations. No item reconstruction, NBT copy, name,
 * lore, attribute, enchantment, or PDC mutation. External providers supply a
 * source ItemStack; their identifying PDC is intentionally NOT transferred.
 */
public final class Paper26ItemModels {
    private static final int MAX_EXACT_FLOAT_INT = 16_777_216;

    public enum Result { APPLIED, INVALID_ID, NO_MODEL, MATERIAL_MISMATCH, INVALID_ITEM }

    private Paper26ItemModels() {}

    public static Result applyKey(final ItemStack target, final String value) {
        if (target == null || target.getType().isAir()) return Result.INVALID_ITEM;
        if (value == null || value.isBlank()) return Result.INVALID_ID;
        final NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null || !value.contains(":")) return Result.INVALID_ID;
        final ItemMeta meta = target.getItemMeta();
        if (meta == null) return Result.INVALID_ITEM;
        meta.setItemModel(key);
        // The explicitly selected modern item_model should not be shadowed
        // by a stale float custom_model_data from a different pack.
        meta.setCustomModelDataComponent(null);
        return target.setItemMeta(meta) ? Result.APPLIED : Result.INVALID_ITEM;
    }

    public static Result applyNumber(final ItemStack target, final int value) {
        if (target == null || target.getType().isAir()) return Result.INVALID_ITEM;
        if (value < 0 || value > MAX_EXACT_FLOAT_INT) return Result.INVALID_ID;
        final ItemMeta meta = target.getItemMeta();
        if (meta == null) return Result.INVALID_ITEM;
        final CustomModelDataComponent data = meta.getCustomModelDataComponent();
        data.setFloats(List.of((float) value));
        meta.setItemModel(null);
        meta.setCustomModelDataComponent(data);
        return target.setItemMeta(meta) ? Result.APPLIED : Result.INVALID_ITEM;
    }

    public static Result clear(final ItemStack target) {
        if (target == null || target.getType().isAir()) return Result.INVALID_ITEM;
        final ItemMeta meta = target.getItemMeta();
        if (meta == null) return Result.INVALID_ITEM;
        meta.setItemModel(null);
        meta.setCustomModelDataComponent(null);
        return target.setItemMeta(meta) ? Result.APPLIED : Result.INVALID_ITEM;
    }

    /** Copy visual model selectors ONLY, never the provider's other metadata.
     * Legacy float-selector models require the same base Material to work
     * with the original resource pack's CMD definitions. Modern item_model
     * references do not impose that legacy material restriction.
     */
    public static Result copyFrom(final ItemStack target, final ItemStack template) {
        if (target == null || target.getType().isAir() || template == null
                || template.getType().isAir()) return Result.INVALID_ITEM;
        final ItemMeta source = template.getItemMeta();
        final ItemMeta destination = target.getItemMeta();
        if (source == null || destination == null) return Result.INVALID_ITEM;
        final NamespacedKey key = source.getItemModel();
        final boolean hasData = source.hasCustomModelDataComponent();
        if (key == null && !hasData) return Result.NO_MODEL;
        if (key == null && !Objects.equals(target.getType(), template.getType())) {
            return Result.MATERIAL_MISMATCH;
        }
        destination.setItemModel(key);
        destination.setCustomModelDataComponent(hasData
                ? source.getCustomModelDataComponent() : null);
        return target.setItemMeta(destination) ? Result.APPLIED : Result.INVALID_ITEM;
    }
}
