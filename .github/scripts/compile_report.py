#!/usr/bin/env python3
"""Turn Gradle javac failures into a short grouped Markdown migration report.

This script does not change build exit codes or suppress errors. The complete
log remains available as an Actions artifact.
"""
from __future__ import annotations

from collections import Counter, defaultdict
from pathlib import Path
import re
import sys

ERROR = re.compile(r"^(.*?\.java):(\d+): error: (.+)$")
PACKAGE = re.compile(r"package (.*?) does not exist")


def category(path: str, message: str) -> str:
    value = (path + " " + message).lower()
    if "net.minecraft.server.v1_" in value or "org.bukkit.craftbukkit.v1_" in value:
        return "Legacy NMS/CraftBukkit 1.7–1.15"
    if "/packet/" in value or "/builder/reflection/minecraft/" in value:
        return "Legacy packet/reflection classes"
    if "package " in message and " does not exist" in message:
        name = PACKAGE.search(message)
        package = name.group(1) if name else ""
        if package.startswith(("com.massivecraft", "com.sucy", "com.herocraftonline",
                               "com.palmergames", "com.gamingmesh", "be.maximvdw",
                               "net.ess3", "com.earth2me", "com.gmail",
                               "me.leothepro555", "org.kingdoms", "net.sacredlabyrinth",
                               "api.praya", "com.gmail.nossr50", "Skills")):
            return "Third-party integration API"
        return "Missing library/API"
    return "Other Java/API migration errors"


def report(file: Path) -> None:
    print(f"## Build diagnostics: `{file}`")
    if not file.exists():
        print("Build step did not run; inspect previous CI steps.\n")
        return

    unique: set[tuple[str, int, str]] = set()
    counts: Counter[str] = Counter()
    samples: dict[str, list[str]] = defaultdict(list)
    for line in file.read_text(encoding="utf-8", errors="replace").splitlines():
        match = ERROR.search(line)
        if not match:
            continue
        name, number, message = match.groups()
        relative_name = name.split("/src/", 1)[-1]
        source = "src/" + relative_name if "/src/" in name else name
        item = (source, int(number), message)
        if item in unique:
            continue
        unique.add(item)
        group = category(name, message)
        counts[group] += 1
        if len(samples[group]) < 4:
            samples[group].append(f"`{source}:{number}` — {message}")

    if not counts:
        print("No javac file/line diagnostics found. See complete log.\n")
        return
    print(f"**{len(unique)} unique javac error(s)** found (this may be capped by `-Xmaxerrs`).\n")
    print("| Category | Errors |")
    print("|---|---:|")
    for group, count in counts.most_common():
        print(f"| {group} | {count} |")
    print()
    for group, _ in counts.most_common():
        print(f"### {group}")
        for example in samples[group]:
            print(f"- {example}")
        print()
    print("The report is diagnostic only: it does not indicate a successful build.\n")


if __name__ == "__main__":
    for arg in sys.argv[1:]:
        report(Path(arg))
