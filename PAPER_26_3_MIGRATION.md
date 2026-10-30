# Paper 26.3 migration tracker — AgarthaLib

**Status: in progress; NOT a verified Paper 26.3 build.** The default branch of this repository is `master`, not `main`. Changes are made directly to the existing default branch as requested.

## Compatibility constraints

- Runtime and compiler target: Java 25.
- Paper API dependency: `io.papermc.paper:paper-api:26.3.build.+` (prefer pinning a stable build for reproducible releases).
- No `pom.xml` or Gradle build definition existed when this migration began; current sources originate from Procyon decompilation.
- Do not raise `api-version` in `resources/plugin.yml` or publish a release until a complete 26.3 build and smoke tests pass.
- Preserve MyItems' damage formulas, grade scaling, durations (ticks), amplifier semantics (`grade - 1`), game configs, and named abilities.

## Implemented (source changes, NOT runtime verified)

- [x] Modern potion effect lookup using `Registry.MOB_EFFECT` and Minecraft namespaced keys in `PotionUtil`.
- [x] Legacy effect names and historic numeric IDs mapped to current effect keys.
- [x] Legacy potion type names mapped to current `PotionType` names.
- [x] `PIG_ZOMBIE` renamed to `ZOMBIFIED_PIGLIN` in poison selection.
- [x] New `V26_3` server version marker; stop relying solely on versioned CraftBukkit package names.
- [x] Recognize the Paper server brand on modern builds.
- [x] Resolve legacy `ParticleEnum` names through current particle keys; provide a modern `Player.spawnParticle` bridge path.
- [x] Provide a modern `Registry.SOUND_EVENT` playback path for existing `SoundEnum` names.

## Remaining hard blockers

- [ ] Introduce a reproducible Java 25 build (Gradle/Maven) and compile against the Paper 26.3 API.
- [ ] Remove/replace all old `net.minecraft.server.v1_*` and `org.bukkit.craftbukkit.v1_*` packet/NBT imports; numerous historical packet handlers still exist in `src/com/praya/agarthalib/packet`.
- [ ] Port item NBT handling to supported modern item metadata, PersistentDataContainer, or data components, preserving existing serialized data where feasible.
- [ ] Audit removed/renamed Material, Sound, EntityType, Attribute, Particle, enchantment APIs across all sources.
- [ ] Audit integrations (Essentials, WorldGuard, Factions, Heroes, Citizens, etc.) and decide supported versions explicitly.
- [ ] Confirm particle data payload coverage and missing/removed sounds via server tests; some old names have no direct modern replacement.
- [ ] Verify startup, shutdown, custom events, menu, metadata, item NBT, particles, sounds, and player state on a Paper 26.3 test server.
- [ ] Once compilation and behavior checks pass, update `plugin.yml` to `api-version: '26.3'`.

**Dependency order:** finish AgarthaLib and its public API first, then compile and test MyItems against the resulting AgarthaLib jar.

References: https://docs.papermc.io/paper/dev/project-setup/ and https://jd.papermc.io/paper/26.3/
