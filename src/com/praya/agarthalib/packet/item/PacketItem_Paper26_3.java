package com.praya.agarthalib.packet.item;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.modern.Paper26ItemData;
import com.praya.agarthalib.modern.Paper26ItemMeta;
import com.praya.agarthalib.modern.Paper26StringListCodec;
import core.praya.agarthalib.builder.bridge.TagsNBTBooks;
import core.praya.agarthalib.builder.bridge.TagsNBTCustom;
import core.praya.agarthalib.builder.bridge.TagsNBTItem;
import core.praya.agarthalib.enums.main.Slot;
import core.praya.agarthalib.enums.main.TagsAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;

/**
 * Paper 26.3 replacement for the old CraftBukkit/NMS item packets.
 *
 * API compatibility: the existing BridgeTagsNBT and MyItems method signatures
 * are deliberately unchanged. New plugin-owned tags are written to the PDC,
 * not to vanilla's private NBT implementation.
 *
 * IMPORTANT: historic raw NBT tags on previously-created items are NOT PDC.
 * Reading those items needs a separate, tested migration from real samples.
 */
public final class PacketItem_Paper26_3 extends HandlerPacket
        implements TagsNBTItem, TagsNBTBooks, TagsNBTCustom {

    public PacketItem_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }

    private static <T, Z> void set(final String key, final String parent, final ItemStack item,
                                   final PersistentDataType<T, Z> type, final Z value) {
        Paper26ItemData.set(key, parent, item, type, value);
    }

    private static <T, Z> Z get(final String key, final String parent, final ItemStack item,
                                final PersistentDataType<T, Z> type) {
        return Paper26ItemData.get(key, parent, item, type);
    }

    private static boolean valid(final ItemStack item) {
        return item != null && !item.getType().isAir();
    }

    @Override
    public void setString(final String key, final String parent, final ItemStack item, final String value) {
        set(key, parent, item, PersistentDataType.STRING, value);
    }

    @Override
    public void setInt(final String key, final String parent, final ItemStack item, final int value) {
        set(key, parent, item, PersistentDataType.INTEGER, value);
    }

    @Override
    public void setDouble(final String key, final String parent, final ItemStack item, final double value) {
        set(key, parent, item, PersistentDataType.DOUBLE, value);
    }

    @Override
    public void setLong(final String key, final String parent, final ItemStack item, final long value) {
        set(key, parent, item, PersistentDataType.LONG, value);
    }

    @Override
    public void setBoolean(final String key, final String parent, final ItemStack item, final boolean value) {
        set(key, parent, item, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    /**
     * A bounded, length-prefixed format avoids comma/newline ambiguity in lore
     * and does not require a runtime JSON library.
     */
    @Override
    public void setListString(final String key, final String parent, final ItemStack item,
                              final List<String> list) {
        if (list == null) {
            return;
        }
        set(key, parent, item, PersistentDataType.BYTE_ARRAY, Paper26StringListCodec.encode(list));
    }

    @Override
    public String getString(final String key, final String parent, final ItemStack item) {
        return get(key, parent, item, PersistentDataType.STRING);
    }

    @Override
    public int getInt(final String key, final String parent, final ItemStack item) {
        final Integer value = get(key, parent, item, PersistentDataType.INTEGER);
        return value == null ? 0 : value;
    }

    @Override
    public double getDouble(final String key, final String parent, final ItemStack item) {
        final Double value = get(key, parent, item, PersistentDataType.DOUBLE);
        return value == null ? 0 : value;
    }

    @Override
    public long getLong(final String key, final String parent, final ItemStack item) {
        final Long value = get(key, parent, item, PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    @Override
    public boolean getBoolean(final String key, final String parent, final ItemStack item) {
        final Byte value = get(key, parent, item, PersistentDataType.BYTE);
        return value != null && value != 0;
    }

    @Override
    public List<String> getListString(final String key, final String parent, final ItemStack item) {
        final byte[] raw = get(key, parent, item, PersistentDataType.BYTE_ARRAY);
        return Paper26StringListCodec.decode(raw);
    }

    @Override
    public void remove(final String key, final String parent, final ItemStack item) {
        Paper26ItemData.remove(key, parent, item);
    }

    private static Attribute resolveAttribute(final TagsAttribute tag) {
        final String vanillaKey;
        switch (tag) {
            case HEALTH: vanillaKey = "max_health"; break;
            case FOLLOW_RANGE: vanillaKey = "follow_range"; break;
            case KNOCKBACK_RESISTANCE: vanillaKey = "knockback_resistance"; break;
            case SPEED: vanillaKey = "movement_speed"; break;
            case ATTACK_DAMAGE: vanillaKey = "attack_damage"; break;
            case DEFENSE: vanillaKey = "armor"; break;
            case SHIELD: vanillaKey = "armor_toughness"; break;
            case ATTACK_RATE: vanillaKey = "attack_speed"; break;
            case LUCK: vanillaKey = "luck"; break;
            case HORSE_JUMP: vanillaKey = "jump_strength"; break;
            case FLYING_SPEED: vanillaKey = "flying_speed"; break;
            case ZOMBIE_REINFORCEMENT: vanillaKey = "spawn_reinforcements"; break;
            default: return null;
        }
        return Registry.ATTRIBUTE.get(NamespacedKey.minecraft(vanillaKey));
    }

    private static EquipmentSlotGroup slotGroup(final Slot slot) {
        return switch (slot) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            case OFFHAND -> EquipmentSlotGroup.OFFHAND;
            default -> EquipmentSlotGroup.HAND;
        };
    }

    @Override
    public void addNBT(final ItemStack item, final TagsAttribute tags, double value, final Slot slot) {
        if (!valid(item) || tags == null) {
            return;
        }
        final Attribute attribute = resolveAttribute(tags);
        if (attribute == null) {
            throw new IllegalArgumentException("Unsupported 26.3 item attribute: " + tags.name());
        }
        final Slot resolvedSlot = slot == null ? Slot.MAINHAND : slot;
        final NamespacedKey modifierKey = new NamespacedKey(
                "agarthalib",
                "attribute_" + tags.name().toLowerCase(Locale.ROOT)
                        + "_" + resolvedSlot.name().toLowerCase(Locale.ROOT));
        value = Math.max(tags.getMinValue(), Math.min(tags.getMaxValue(), value));
        if (!"Double".equalsIgnoreCase(tags.getValueType())) {
            value = (int) value;
        }
        Paper26ItemMeta.addAttribute(item, attribute, modifierKey, value, slotGroup(resolvedSlot));
    }

    @Override
    public void clearNBT(final ItemStack item) {
        Paper26ItemMeta.clearAttributes(item);
    }

    @Override
    public void setUnbreakable(final ItemStack item, final boolean unbreakable) {
        Paper26ItemMeta.setUnbreakable(item, unbreakable);
    }

    @Override
    public boolean isUnbreakable(final ItemStack item) {
        return Paper26ItemMeta.isUnbreakable(item);
    }

    @Override
    public ItemStack createBook(final String name, final int amount, final String title,
                                final String author, final String... pages) {
        return Paper26ItemMeta.createBook(name, amount, title, author, pages);
    }

    @Override
    public void openBook(final ItemStack book, final Player player) {
        Paper26ItemMeta.openBook(book, player);
    }
}
