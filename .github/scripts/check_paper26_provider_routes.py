#!/usr/bin/env python3
"""Paper 26.3 adapter contract guard. Runtime tests are still mandatory."""
from pathlib import Path
root = Path(__file__).resolve().parents[2]
read = lambda p: (root / p).read_text(encoding="utf-8")
support = root / "src/api/praya/agarthalib/builder/support"
manager = read("src/api/praya/agarthalib/manager/plugin/SupportManagerAPI.java")
for obsolete in ("SupportFactionsLegacy.java", "SupportFactionsUUID.java", "SupportFactionsMassive.java"):
    assert not (support / obsolete).exists(), f"Legacy Factions implementation remains: {obsolete}"
for obsolete in ("getSupportFactions(", "getSupportFactionsUUID(", "getSupportFactionsMassive(",
                 "getSupportFactionsLegacy(", "PluginUtil.getPlugin(\"Factions\""):
    assert obsolete not in manager, f"Factions runtime entry point remains: {obsolete}"
assert 'PluginUtil.getPlugin("MythicLib")' in manager
assert "Paper26MythicLibStats.read" in read("src/api/praya/agarthalib/builder/support/SupportMythicLib.java")
assert "- MythicLib" in read("resources/plugin.yml")
assert "PacketEquipment_Paper26_3(plugin)" in read("src/core/praya/agarthalib/bridge/unity/BridgeEquipment.java")
assert "PacketInventory_Paper26_3(plugin)" in read("src/core/praya/agarthalib/bridge/unity/BridgeInventory.java")
inventory = read("src/com/praya/agarthalib/packet/inventory/PacketInventory_Paper26_3.java")
assert ".setTitle(" not in inventory and "net.minecraft" not in inventory
message = read("src/com/praya/agarthalib/packet/message/PacketMessage_Paper26_3.java")
assert "implements MessageActionbar, MessageTitle, MessageJson" in message
assert "Paper26JsonChat.send" in message
assert "net.minecraft" not in message
assert "PacketMessage_Paper26_3(plugin)" in read("src/core/praya/agarthalib/bridge/unity/BridgeMessage.java")
print("PASS: Factions integration removed, optional MythicLib and Paper 26.3 bridges wired")
