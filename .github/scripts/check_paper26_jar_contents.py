#!/usr/bin/env python3
"""Inspect the actual production JAR, not just Gradle sourceSets or CI stubs."""
from pathlib import Path
from zipfile import ZipFile
import sys

root = Path(__file__).resolve().parents[2]
candidates = [p for p in (root / "build/libs").glob("*.jar")
              if not p.name.endswith("-sources.jar") and not p.name.endswith("-javadoc.jar")]
if len(candidates) != 1:
    sys.exit(f"Expected exactly one AgarthaLib binary JAR: {[p.name for p in candidates]}")
with ZipFile(candidates[0]) as jar:
    names = set(jar.namelist())
    manifest = jar.read("plugin.yml").decode("utf-8")
    assert "main: com.lymeowz.lib.LyMeowzLib\n" in manifest
    assert "name: LyMeowzLib\n" in manifest
    assert "provides: [AgarthaLib]\n" in manifest
    assert "  lymeowzlib.admin:\n" in manifest
    assert "  lymeowzlib.command.reload:\n" in manifest
    assert "  lymeowzlib:\n" in manifest
    assert "  agarthalib:\n" not in manifest
    for required in (
        "plugin.yml",
        "com/praya/agarthalib/AgarthaLib.class",
        "com/lymeowz/lib/LyMeowzLib.class",
        "com/lymeowz/lib/command/CommandText.class",
        "com/lymeowz/lib/command/CommandDefinitions.class",
        "com/lymeowz/lib/command/CommandRegistry.class",
        "com/lymeowz/lib/command/CommandLookup.class",
        "com/lymeowz/lib/text/HexColors.class",
        "com/lymeowz/lib/item/StringListCodec.class",
        "com/praya/agarthalib/modern/Paper26StringListCodec.class",
        "com/praya/agarthalib/modern/Paper26HexColors.class",
        "com/lymeowz/lib/permission/PermissionBridge.class",
        "com/lymeowz/lib/migration/PluginFolderMigration.class",
        "com/praya/agarthalib/packet/arrow/PacketArrow_Paper26_3.class",
        "com/praya/agarthalib/packet/material/PacketMaterial_Paper26_3.class",
        "com/praya/agarthalib/packet/equipment/PacketEquipment_Paper26_3.class",
        "com/praya/agarthalib/packet/item/PacketItem_Paper26_3.class",
        "com/praya/agarthalib/modern/Paper26OptionalProviders.class",
        "org/json/simple/JSONObject.class",
        "org/json/simple/parser/JSONParser.class",
        "net/kyori/adventure/nbt/BinaryTagIO.class",
        "com/praya/agarthalib/modern/Paper26LegacyItemTags.class",
        "api/praya/agarthalib/manager/plugin/SupportManagerAPI.class",
    ):
        assert required in names, f"Missing Paper 26.3 JAR class: {required}"
    retired = (
        "JobsReborn", "HolographicDisplays", "Heroes", "SkillsPro", "CombatStamina",
        "LifeEssence", "Kingdoms", "McMMO", "BattleLevels", "Citizens",
        "MyItems", "SkillAPI", "Gangs", "SimpleClans")
    forbidden = [n for n in names if
        (n.startswith("com/praya/agarthalib/packet/") and
         n.rsplit("/", 1)[-1].startswith("Packet") and
         "_1_" in n.rsplit("/", 1)[-1]) or
        n in {f"api/praya/agarthalib/builder/support/Support{item}.class" for item in retired} or
        n.startswith(("com/praya/agarthalib/listener/support/heroes/",
                      "com/praya/agarthalib/listener/support/skillapi/",
                      "com/praya/agarthalib/listener/support/battlelevels/"))]
    assert not forbidden, f"Legacy optional/NMS classes leaked into runtime JAR: {forbidden[:30]}"
    print(f"PASS: inspected {candidates[0].name}, {len(names)} entries, "
          "9 modern bridges and no archived versioned packet/retired provider classes")
