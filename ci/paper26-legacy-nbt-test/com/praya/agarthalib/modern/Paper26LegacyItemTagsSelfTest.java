package com.praya.agarthalib.modern;

import net.kyori.adventure.nbt.*;
import java.util.List;

public final class Paper26LegacyItemTagsSelfTest {
    private static int count;
    private static void check(boolean ok, String what) {
        count++;
        if (!ok) throw new AssertionError(what);
    }
    public static void main(String[] args) {
        CompoundBinaryTag raw = CompoundBinaryTag.builder()
            .putString("Name", "Kiếm cũ")
            .putInt("Level", 13)
            .putDouble("Damage", 12.5D)
            .putLong("Time", 99999L)
            .putByte("Active", (byte) 1)
            .put("Stats", ListBinaryTag.builder(BinaryTagTypes.STRING)
                .add(StringBinaryTag.stringBinaryTag("Damage:10"))
                .add(StringBinaryTag.stringBinaryTag("Speed:2"))
                .build())
            .put("Nested", CompoundBinaryTag.builder()
                .putString("Name", "Tên phụ")
                .put("Stats", ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                    .add(CompoundBinaryTag.builder().putString("stat", "Armor:4").build())
                    .add(CompoundBinaryTag.builder().putString("stat", "Crit:2").build())
                    .build())
                .build())
            .build();
        CompoundBinaryTag modern = CompoundBinaryTag.builder()
            .put("components", CompoundBinaryTag.builder()
                .put("minecraft:custom_data", raw).build()).build();
        CompoundBinaryTag legacy = CompoundBinaryTag.builder().put("tag", raw).build();

        check(Paper26LegacyItemTags.locate(modern).equals(raw), "modern custom_data");
        check(Paper26LegacyItemTags.locate(legacy).equals(raw), "old tag root");
        check(Paper26LegacyItemTags.locate(CompoundBinaryTag.empty()) == null, "missing data");
        check("Kiếm cũ".equals(Paper26LegacyItemTags.string(raw, "Name", null)), "UTF8 name");
        check("Tên phụ".equals(Paper26LegacyItemTags.string(raw, "Name", "Nested")), "parent");
        check(Integer.valueOf(13).equals(Paper26LegacyItemTags.integer(raw, "Level", null)), "int");
        check(Double.valueOf(12.5D).equals(Paper26LegacyItemTags.decimal(raw, "Damage", null)), "double");
        check(Long.valueOf(99999).equals(Paper26LegacyItemTags.longNumber(raw, "Time", null)), "long");
        check(Boolean.TRUE.equals(Paper26LegacyItemTags.booleanValue(raw, "Active", null)), "boolean");
        check(List.of("Damage:10","Speed:2").equals(
            Paper26LegacyItemTags.strings(raw, "Stats", null)), "legacy string list");
        check(List.of("Armor:4","Crit:2").equals(
            Paper26LegacyItemTags.strings(raw, "Stats", "Nested")), "1.15 compound stat list");
        check(Paper26LegacyItemTags.integer(raw, "Name", null) == null, "wrong primitive type");
        check(Paper26LegacyItemTags.string(raw, "Level", null) == null, "no implicit string cast");
        check(Paper26LegacyItemTags.string(raw, "Name", "Missing") == null, "missing parent");
        check(Paper26LegacyItemTags.strings(raw, "Missing", null) == null, "missing list");
        CompoundBinaryTag bad = CompoundBinaryTag.builder().put("Bad",
            ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                .add(CompoundBinaryTag.builder().putInt("stat", 1).build()).build()).build();
        check(Paper26LegacyItemTags.strings(bad, "Bad", null) == null, "malformed list rejected");
        // Removal keys are separate from original values and encode the
        // legacy parent/key exactly. This protects old metadata from being
        // resurrected after a newer PDC key is deleted.
        var normalKey = Paper26ItemData.key("skill", "MyItems");
        var deletedKey = Paper26ItemData.removalKey("skill", "MyItems");
        check(!normalKey.equals(deletedKey), "tombstone cannot shadow PDC data key");
        check(!deletedKey.equals(Paper26ItemData.removalKey("skill", "Other")),
                "parent-scoped deletion masks");
        check(!deletedKey.equals(Paper26ItemData.removalKey("Skill", "MyItems")),
                "case-sensitive legacy keys");
        check(deletedKey.equals(Paper26ItemData.removalKey("skill", "MyItems")),
                "deterministic deletion marker");

        check(Paper26LegacyItemTags.locate(null) == null, "null root");
        check(Paper26LegacyItemTags.strings(raw, null, null) == null, "null key");
        check(Paper26LegacyItemTags.string(null, "Name", null) == null, "null data");
        System.out.println("PASS: " + count + " legacy NBT read-only compatibility checks");
    }
}
