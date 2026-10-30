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

- [x] Add a Java 25 Gradle build scaffold and a Paper 26.3 dependency (not yet compile-verified).
- [ ] Finish migrating legacy NMS and optional dependencies, then obtain a successful full Paper 26.3 compilation.
- [ ] Remove/replace all old `net.minecraft.server.v1_*` and `org.bukkit.craftbukkit.v1_*` packet/NBT imports; numerous historical packet handlers still exist in `src/com/praya/agarthalib/packet`.
- [x] Add `Paper26ItemData` for new plugin-owned PDC tags and `Paper26ItemMeta` for Bukkit attribute, unbreakable and book operations. `PacketItem_Paper26_3` is selected by `BridgeTagsNBT` when running 26.3.
- [x] Add an independent `compilePaper26ItemData` Java 25 CI task. The PDC and item metadata services have successfully compiled against Paper 26.3 in Actions.
- [ ] Implement and test **read/migration of old raw item NBT** into PDC (requires historic serialized-item test fixtures). The new PDC service does **not** read old custom tags automatically. Do not deploy this bridge against production legacy items without a migration plan.
- [ ] Audit removed/renamed Material, Sound, EntityType, Attribute, Particle, enchantment APIs across all sources.
- [ ] Audit integrations (Essentials, WorldGuard, Factions, Heroes, Citizens, etc.) and decide supported versions explicitly.
- [ ] Confirm particle data payload coverage and missing/removed sounds via server tests; some old names have no direct modern replacement.
- [ ] Verify startup, shutdown, custom events, menu, metadata, item NBT, particles, sounds, and player state on a Paper 26.3 test server.
- [ ] Once compilation and behavior checks pass, update `plugin.yml` to `api-version: '26.3'`.

**Dependency order:** finish AgarthaLib and its public API first, then compile and test MyItems against the resulting AgarthaLib jar.

References: https://docs.papermc.io/paper/dev/project-setup/ and https://jd.papermc.io/paper/26.3/


## Modern item bridge design and compatibility limitations

- **Preserved public API:** Existing `Bridge.getBridgeTagsNBT()` method names and signatures are unchanged, including typed custom values, strings, lists, item attributes, unbreakable, and written books.
- **Custom data for newly written items:** Namespace `agarthalib`, with UTF-8 hexadecimal-encoded, case-sensitive original keys and optional parents. `String`, `int`, `double`, `long`, `boolean` and length-prefixed string lists use supported PDC primitive types.
- **Vanilla item data:** Attribute modifiers, unbreakable, and signed books now use Bukkit/Paper `ItemMeta` and `Player.openBook`, rather than direct NMS packet handlers.
- **Critical compatibility gap:** NMS-era raw `NBTTagCompound` entries (possibly converted into a `minecraft:custom_data` component by Minecraft) are **not** addressable through an AgarthaLib PDC key automatically. Old item data may not be recognized until migration is implemented and validated against representative backups. Do **not** discard or overwrite original items in an attempted migration.
- **Gameplay validation required:** Attribute slot restrictions, zero/default modifiers, legacy RGB/book text, complex list serialization, old enchantments, and existing recipe/item identifiers need smoke tests with real item fixtures. Independently compiling the new service is not equivalent to runtime verification.
- **Legacy source remains:** The old `PacketItem_1_*_R*` handlers are preserved as reference code during migration but still stop the full Gradle compile. They must eventually be retired from the **compiled** source set only after their callers have modern implementations.
- **No release until:** successful AgarthaLib and MyItems full builds, a verified one-time/dual-read migration for historic custom item tags, and Paper 26.3 server tests.

### Suggested original-item fixture procedure

1. On a **backup or disposable copy** of the previous working MyItems server, record known item IDs and items covering sockets, abilities, stats, nested NBT and custom lore.
2. Export copies of representative item stacks and their serialized metadata from that environment without modifying the originals.
3. Write migration tests for raw legacy tag extraction and convert only recognized, typed fields. Retain all unrecognized vanilla/custom components.
4. Compare item descriptions, attributes, socket values, abilities, enchantments, durability, permission checks and real combat outcomes before/after.
5. Add an explicit backup and migration command; never auto-rewrite live inventories on the first plugin startup.

Reference: https://docs.papermc.io/paper/dev/pdc/ (item stack PDC support), https://docs.papermc.io/paper/dev/data-component-api/.

## 2026-10-30 additional regression checks

- Original combat/metadata code and all source-controlled YAML are protected by a Git-blob baseline and a CI comparison. An intentional edit needs a review and baseline update.
- The real Paper26StringListCodec now supports UTF-8 strings longer than 64 KiB and reads the first experimental PDC codec format. A Java 25 executable test covers Unicode, long strings, preview input and corrupt payloads.
- The new Paper26SoundRegistry resolves sound IDs through the Minecraft namespaced registry. Missing historical sounds must still be validated; the bridge does not silently use different audio.
- GitHub Actions scans every Java file for historic NMS, CraftBukkit, reflection and external integrations. The scan is diagnostic only.
- Full build and runtime gameplay verification remain unresolved; old raw item NBT migration requires backups and representative item fixtures.

## Modern player and message bridges (incremental)

- `PacketPlayer_Paper26_3` now implements the original player locale, entity visibility, absorption and main-hand animation interfaces with public Paper APIs; `BridgePlayer` chooses it for `V26_3`. The off-hand animation goes through `Player.swingOffHand()`. Original skill and command sources are untouched.
- **Behavior review needed:** `Player.hideEntity(plugin, entity)` is plugin-scoped and can persist until `showEntity`; the old implementation sent a one-off entity-destroy packet. Check lifecycle and reveal conditions before production. `Player.swingMainHand()` broadcasts via Paper's tracking system, not the old `effectRange`-limited manual packet loop.
- **Intentionally not guessed:** Old NMS `getSwingDuration` and `getSwingProgress` calculations have no confirmed 26.3 equivalents; they are not implemented in the new `PacketPlayerCombatPresent` interface. Callers must be audited and any required semantics reconstructed with tests before release.
- `Paper26AudienceService` and `PacketMessage_Paper26_3` replace legacy action-bar, title and subtitle NMS packets using Adventure. Section-sign formatting and the numeric tick timing parameters retain their public contract; titles/subtitles update only the requested part instead of clearing the other.
- **JSON gap:** The legacy `MessageJson` contract uses a custom `||` DSL plus serialized item NBT in hover events. JSON chat, click/hover and `getJsonItem` are **not migrated** by this adapter. Do not claim they work on Paper 26.3 until a lossless hover-item conversion is implemented.
- **Inventory gap:** Paper 26.3 deprecates `InventoryView.setTitle` because it has broken or unsupported behavior. Do not substitute it blindly for NMS window-title packets, which could cause inventory event or item behavior changes. Investigate a safe menu recreation flow with explicit viewer/state tests.
- GitHub Actions now independently compiles these new adapters, their original interfaces and the prior codec tests. The full `compileJava` and all gameplay/runtime tests **still must pass** before publishing.

Paper 26.3 references: https://jd.papermc.io/paper/26.3/org/bukkit/entity/Player.html and https://jd.papermc.io/paper/26.3/org/bukkit/inventory/InventoryView.html .
