#!/usr/bin/env python3
"""Source-level route guard; the Java self-tests provide separate API checks.

This protects against accidentally wiring Paper 26.3 back to a versioned NMS
packet class. It does not replace in-game targeting or visual tests.
"""
from pathlib import Path

root = Path(__file__).resolve().parents[2]
bridge_entity = (root / "src/core/praya/agarthalib/bridge/unity/BridgeLivingEntity.java").read_text(encoding="utf-8")
bridge_particle = (root / "src/core/praya/agarthalib/bridge/unity/BridgeParticle.java").read_text(encoding="utf-8")
entity_handler = (root / "src/com/praya/agarthalib/packet/livingentity/PacketLivingEntity_Paper26_3.java").read_text(encoding="utf-8")
entity_classifier = (root / "src/com/praya/agarthalib/modern/Paper26LivingEntityKind.java").read_text(encoding="utf-8")

assert "case V26_3:" in bridge_entity
assert "handler = new PacketLivingEntity_Paper26_3(plugin);" in bridge_entity
assert "implements LivingEntityTools" in entity_handler
assert "Paper26LivingEntityKind.isLivingEntity(entity)" in entity_handler
assert "type == EntityType.ARMOR_STAND" in entity_classifier
assert "LivingEntity.class.isAssignableFrom(entityClass)" in entity_classifier

legacy_particle_signature = (
    "public final void playParticle(final Collection<Player> players, "
    "final ParticleEnum particleEnum, final Location location, final int count, "
    "final double offsetX, final double offsetY, final double offsetZ, "
    "final int id, final float data)"
)
assert bridge_particle.count(legacy_particle_signature) == 1
legacy_method = bridge_particle.split(legacy_particle_signature, 1)[1].split(
    "private static class BridgeParticleHelper", 1
)[0]
assert "ServerUtil.getVersionNMS() == VersionNMS.V26_3" in legacy_method
assert "offsetX, offsetY, offsetZ, (double) data, null);" in legacy_method
assert "return;" in legacy_method

for content in (entity_handler, entity_classifier):
    assert "net.minecraft" not in content and "org.bukkit.craftbukkit" not in content

print("PASS: Paper 26.3 entity and legacy particle bridges stay on public Bukkit/Paper APIs")
