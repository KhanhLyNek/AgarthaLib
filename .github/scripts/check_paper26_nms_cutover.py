#!/usr/bin/env python3
"""Executable source guard for the Paper 26.3-only packet cutover.

Historical v1_7..v1_15 packet files are retained as SOURCE ONLY for
migration references; not compiled or added to the runtime Java output.
This guard checks source routing and Gradle exclusion, not live gameplay.
"""
import re
from pathlib import Path

root = Path(__file__).resolve().parents[2]
packets = sorted((root / "src/com/praya/agarthalib/packet").rglob("Packet*_1_*.java"))
gradle = (root / "build.gradle.kts").read_text("utf-8")
assert 'java.exclude("**/packet/**/Packet*_1_*.java")' in gradle
assert len(packets) == 82, f"Unexpected historic packet count: {len(packets)}"

bridges = root / "src/core/praya/agarthalib/bridge/unity"
implemented = {
    "BridgeArrow": "PacketArrow_Paper26_3",
    "BridgeBlock": "PacketBlock_Paper26_3",
    "BridgeEquipment": "PacketEquipment_Paper26_3",
    "BridgeInventory": "PacketInventory_Paper26_3",
    "BridgeLivingEntity": "PacketLivingEntity_Paper26_3",
    "BridgeMaterial": "PacketMaterial_Paper26_3",
    "BridgeMessage": "PacketMessage_Paper26_3",
    "BridgePlayer": "PacketPlayer_Paper26_3",
    "BridgeTagsNBT": "PacketItem_Paper26_3",
}
for name in ("BridgeArrow", "BridgeBlock", "BridgeEquipment", "BridgeInventory",
             "BridgeLivingEntity", "BridgeMaterial", "BridgeMessage", "BridgeParticle",
             "BridgePlayer", "BridgeServer", "BridgeSound", "BridgeTagsNBT"):
    s = (bridges / (name + ".java")).read_text("utf-8")
    assert "case V26_3:" in s, f"{name} not explicitly routed for Paper 26.3"
    assert not re.search(r"\b(?:new|import)\s+Packet\w*_1_\d+", s), f"Old packet reference in {name}"
    if name in implemented:
        assert "new " + implemented[name] + "(plugin)" in s, f"Missing {name} adapter"
        adapter = list((root / "src/com/praya/agarthalib/packet").rglob(
            implemented[name] + ".java"))
        assert len(adapter) == 1, f"Missing or ambiguous adapter for {name}"
    else:
        assert "handler = null;" in s

equipment = (root / "src/com/praya/agarthalib/packet/equipment/PacketEquipment_Paper26_3.java").read_text("utf-8")
assert "implements EquipmentTools" in equipment
assert "PacketEquipment_1_9" not in equipment
arrow = (root / "src/com/praya/agarthalib/packet/arrow/PacketArrow_Paper26_3.java").read_text("utf-8")
assert "HIDE_ADDITIONAL_TOOLTIP" in arrow and "PotionMeta" in arrow
# No provider implementation whose public Java signature links an absent
# dependency may accidentally re-enter the Paper runtime artifact.
retired = ("JobsReborn", "HolographicDisplays", "Heroes", "SkillsPro",
           "CombatStamina", "LifeEssence", "Kingdoms", "McMMO",
           "BattleLevels", "Citizens", "MyItems", "SkillAPI",
           "Gangs", "SimpleClans")
manager = (root / "src/api/praya/agarthalib/manager/plugin/SupportManagerAPI.java").read_text("utf-8")
for name in retired:
    assert f'java.exclude("**/builder/support/Support{name}.java")' in gradle, name
    assert "Support" + name + " getSupport" + name + "(" not in manager, name
    assert "isSupport" + name + "()" in manager, name
for folder in ("heroes", "skillapi", "battlelevels"):
    assert f'java.exclude("**/listener/support/{folder}/**")' in gradle
boot = (root / "src/com/praya/agarthalib/AgarthaLib.java").read_text("utf-8")
for old_listener in ("new ListenerHeroes", "new ListenerSkillAPI",
                     "new ListenerBattleLevels"):
    assert old_listener not in boot, "Old listener boot route reintroduced"
assert "createTransientDecentHologram" in manager and "getMMOCoreLevel" in manager

print(f"PASS: {len(packets)} old packet sources excluded from Paper-only build; "
      "12 bridges reviewed and 9 current packet adapters wired")
