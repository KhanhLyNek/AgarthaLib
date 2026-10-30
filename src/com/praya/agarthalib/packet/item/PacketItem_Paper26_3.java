package com.praya.agarthalib.packet.item;

import com.google.common.collect.ImmutableMultimap;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import core.praya.agarthalib.builder.bridge.TagsNBTBooks;
import core.praya.agarthalib.builder.bridge.TagsNBTCustom;
import core.praya.agarthalib.builder.bridge.TagsNBTItem;
import core.praya.agarthalib.enums.main.Slot;
import core.praya.agarthalib.enums.main.TagsAttribute;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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

    private static final String NAMESPACE = "agarthalib";

    public PacketItem_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }

    /**
     * NamespacedKeys allow a restricted alphabet. UTF-8 hexadecimal encoding
     * preserves the original, case-sensitive key and parent without collisions.
     * The prefixes distinguish a null parent from an explicitly empty parent.
     */
    private static NamespacedKey dataKey(final String key, final String parent) {
        if (key == null) {
            return null;
        }
        final String path = (parent == null ? "n_" : "p_" + hex(parent) + "_") + hex(key);
        return new NamespacedKey(NAMESPACE, "legacy_" + path);
    }

    private static String hex(final String value) {
        final byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        final char[] chars = new char[utf8.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < utf8.length; i++) {
            chars[i * 2] = digits[(utf8[i] & 0xff) >>> 4];
            chars[i * 2 + 1] = digits[utf8[i] & 0x0f];
        }
        return new String(chars);
    }

    private static boolean valid(final ItemStack item) {
        return item != null && !item.getType().isAir();
    }

    private static <T, Z> void set(final String key, final String parent, final ItemStack item,
                                   final PersistentDataType<T, Z> type, final Z value) {
        final NamespacedKey tag = dataKey(key, parent);
        if (tag == null || !valid(item) || value == null) {
            return;
        }
        item.editPersistentDataContainer(pdc -> pdc.set(tag, type, value));
    }

    private static <T, Z> Z get(final String key, final String parent, final ItemStack item,
                                final PersistentDataType<T, Z> type) {
        final NamespacedKey tag = dataKey(key, parent);
        if (tag == null || !valid(item)) {
            return null;
        }
        return item.getPersistentDataContainer().get(tag, type);
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
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(bytes)) {
                out.writeInt(list.size());
                for (final String line : list) {
                    out.writeUTF(line == null ? "" : line);
                }
            }
            set(key, parent, item, PersistentDataType.BYTE_ARRAY, bytes.toByteArray());
        } catch (final IOException ex) {
            throw new IllegalArgumentException("Cannot encode custom item list '" + key + "'", ex);
        }
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
        final List<String> result = new ArrayList<>();
        if (raw == null) {
            return result;
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(raw))) {
            final int count = in.readInt();
            if (count < 0 || count > 10_000) {
                throw new IOException("Invalid string-list count");
            }
            for (int i = 0; i < count; i++) {
                result.add(in.readUTF());
            }
            return result;
        } catch (final IOException ex) {
            throw new IllegalStateException("Corrupted custom item list '" + key + "'", ex);
        }
    }

    @Override
    public void remove(final String key, final String parent, final ItemStack item) {
        final NamespacedKey tag = dataKey(key, parent);
        if (tag != null && valid(item)) {
            item.editPersistentDataContainer(pdc -> pdc.remove(tag));
        }
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
                NAMESPACE,
                "attribute_" + tags.name().toLowerCase(Locale.ROOT)
                        + "_" + resolvedSlot.name().toLowerCase(Locale.ROOT));
        value = Math.max(tags.getMinValue(), Math.min(tags.getMaxValue(), value));
        if (!"Double".equalsIgnoreCase(tags.getValueType())) {
            value = (int) value;
        }
        final AttributeModifier replacement = new AttributeModifier(
                modifierKey, value, AttributeModifier.Operation.ADD_NUMBER, slotGroup(resolvedSlot));
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        final Collection<AttributeModifier> existing = meta.getAttributeModifiers(attribute);
        if (existing != null) {
            for (final AttributeModifier old : new ArrayList<>(existing)) {
                if (modifierKey.equals(old.getKey())) {
                    meta.removeAttributeModifier(attribute, old);
                }
            }
        }
        meta.addAttributeModifier(attribute, replacement);
        item.setItemMeta(meta);
    }

    @Override
    public void clearNBT(final ItemStack item) {
        if (valid(item)) {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Empty overrides match the old explicit AttributeModifiers: [].
                meta.setAttributeModifiers(ImmutableMultimap.of());
                item.setItemMeta(meta);
            }
        }
    }

    @Override
    public void setUnbreakable(final ItemStack item, final boolean unbreakable) {
        if (valid(item)) {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(unbreakable);
                item.setItemMeta(meta);
            }
        }
    }

    @Override
    public boolean isUnbreakable(final ItemStack item) {
        return valid(item) && item.hasItemMeta() && item.getItemMeta().isUnbreakable();
    }

    @Override
    public ItemStack createBook(final String name, final int amount, final String title,
                                final String author, final String... pages) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK, Math.max(1, amount));
        final BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            meta.setTitle(title == null ? "" : title);
            meta.setAuthor(author == null ? "" : author);
            if (pages != null) {
                meta.setPages(pages);
            }
            book.setItemMeta(meta);
        }
        return book;
    }

    @Override
    public void openBook(final ItemStack book, final Player player) {
        if (valid(book) && player != null) {
            player.openBook(book);
        }
    }
}
