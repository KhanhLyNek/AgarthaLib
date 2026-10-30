#!/usr/bin/env python3
"""Find exact source consumers before retiring old compile-only plugin APIs.

No automatic removal: typed public return values require an explicit migration.
Tracks direct mentions of obsolete Support classes & getters in Java sources.
"""
import json
from pathlib import Path
root = Path(__file__).resolve().parents[2]
sources = list((root / "src").rglob("*.java"))
legacy = [
    "JobsReborn", "Heroes", "HolographicDisplays", "SkillAPI",
    "SkillsPro", "McMMO", "BattleLevels", "Citizens", "Kingdoms",
    "Gangs", "SimpleClans", "CombatStamina", "LifeEssence", "MyItems"
]
hits = {}
for name in legacy:
    tokens = ("Support" + name, "getSupport" + name + "(")
    entries = []
    for src in sources:
        contents = src.read_text("utf-8")
        if any(token in contents for token in tokens):
            entries.append(str(src.relative_to(root)))
    hits[name] = entries
# These are source mentions, not necessarily compile-incompatible direct imports.
out = root / "build/reports/paper26-legacy-api-callers.json"
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(hits, indent=2, ensure_ascii=False)+"\n", "utf-8")
print("PAPER26_LEGACY_API_CALLERS " + json.dumps({
    key: {"file_count": len(val),
          "outside_provider": [v for v in val
                  if not v.startswith("src/api/praya/agarthalib/builder/support/")
                  and not v.endswith("SupportManagerAPI.java")]}
    for key,val in hits.items()}, ensure_ascii=False))
