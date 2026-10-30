#!/usr/bin/env python3
"""Deterministic scoped migration inventory; counts FILES, not javac diagnostics.

Run: python3 .github/scripts/audit_paper26_remaining.py
Writes JSON to build/reports/paper26-remaining.json for trend comparisons.
A source-level adapter != compiled plugin != runtime verified plugin.
"""
import json
import re
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "src"
files = sorted(SRC.rglob("*.java"))
packet = [p for p in files if "/packet/" in p.as_posix()]
versioned = [p for p in packet if re.search(r"_1_\d+(?:_R\d+)?\.java$", p.name)]
nms = [p for p in files if re.search(
    r"\b(?:net\.minecraft\.server\.v\d+|org\.bukkit\.craftbukkit\.v\d+)", p.read_text("utf-8"))]
families = Counter(p.parent.name for p in versioned)
external = {
    "JobsReborn": r"\bcom\.gamingmesh\.jobs\b",
    "Heroes": r"\bcom\.herocraftonline\.heroes\b",
    "HolographicDisplays": r"\bcom\.gmail\.filoghost\.holographicdisplays\b",
    "Factions": r"\bcom\.massivecraft\.factions\b",
    "McMMO": r"\bcom\.gmail\.nossr50\b",
    "Kingdoms": r"\borg\.kingdoms\b",
    "SkillAPI": r"\bcom\.sucy\.skill\b",
}
ext = {key: [str(p.relative_to(ROOT)) for p in files if re.search(pattern, p.read_text("utf-8"))]
       for key, pattern in external.items()}

adapters = [
    "Paper26AuraSkillsProgress", "Paper26MMOCoreProgress",
    "Paper26LandsClaims", "Paper26DecentHolograms",
]
existing = [name for name in adapters if (SRC / "com/praya/agarthalib/modern" / (name+".java")).is_file()]
# A *source* implemented flag only; never assert runtime completion from this.
remaining = {
    "legacy_versioned_packet_files_to_retire_or_isolate": len(versioned),
    "old_optional_provider_import_files_jobs_heroes_holographic": sum(
        len(ext[name]) for name in ("JobsReborn", "Heroes", "HolographicDisplays")),
    "legacy_raw_custom_nbt_migration_unverified": 1,
    "legacy_json_snbt_hover_item_unverified": 1,
    "four_modern_provider_adapters_missing_source": 4 - len(existing),
}
data = {
    "scope": "AgarthaLib source under src at the checked-out commit",
    "java_files": len(files),
    "packet_java_files": len(packet),
    "versioned_packet_files": len(versioned),
    "versioned_packet_by_family": dict(sorted(families.items())),
    "versioned_packet_paths": [str(p.relative_to(ROOT)) for p in versioned],
    "source_files_referencing_versioned_nms": len(nms),
    "nms_source_paths": [str(p.relative_to(ROOT)) for p in nms],
    "direct_external_api_file_counts": {k: len(v) for k, v in ext.items()},
    "direct_external_api_paths": ext,
    "modern_provider_adapters_present": existing,
    "scoped_remaining_work_items": remaining,
    "scope_item_total": len(versioned) + sum(
        len(ext[name]) for name in ("JobsReborn", "Heroes", "HolographicDisplays")) + 2 + 4,
    "scoped_implemented_provider_sources": len(existing),
    "meaning": "Counts represent selected source-level work items, NOT independent defects or validated runtime functionality"
}
output = ROOT / "build/reports/paper26-remaining.json"
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(json.dumps(data, ensure_ascii=False, indent=2)+"\n", "utf-8")
print("PAPER26_AUDIT " + json.dumps({
    "packet_files": len(packet),
    "versioned": len(versioned),
    "direct_nms": len(nms),
    "external": data["direct_external_api_file_counts"],
    "modern_provider_sources": len(existing),
    "work_item_scope": data["scope_item_total"],
    "remaining": remaining,
}, ensure_ascii=False))
# Scope regression guard, not a bogus proof that 82 blockers were resolved.
assert len(versioned) == 82, "Legacy versioned packet inventory changed: review delta before changing baseline"
assert len(ext["Factions"]) == 0, "Removed Factions imports reappeared"
