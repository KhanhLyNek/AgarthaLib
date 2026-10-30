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

## Modern block-damage overlay adapter (2026-10-30)

- `PacketBlock_Paper26_3` implements the unchanged `PacketBlockAnimation` interface. `BridgeBlock` selects it for Paper 26.3. The old source ID and x/y/z coordinates are passed through.
- `Paper26BlockDamage` uses `Player.sendBlockDamage(Location, float, int)` to display cracking without changing the real block or firing block-break events. Stage 0–9 maps to 0.1–1.0; stages outside 0–9 map to zero progress. Source IDs isolate multiple overlays.
- The API accepts a world through `Location`; the legacy packet had no world field. The modern adapter uses each receiving viewer's world, matching the old per-viewer packet scope.
- The isolated CI task `compilePaper26BlockBridge` checks the actual adapter and the unchanged original interface. `testPaper26BlockDamage` tests all 14 boundary cases, including clearing.
- **Gameplay caution:** The 10-level screen crack overlay and stage clearing must be compared on a live Paper 26.3 client. Successful source/compile checks do not prove pixel-perfect animation or lifecycle parity. No MyItems skill code was changed.

## Potion effect IDs, aliases and optional legacy placeholders (2026-10-30)

- `PotionUtil.getPotionEffectType(String)` continues to query `Registry.MOB_EFFECT` and keeps the exact original public signature. The name and historical integer-ID normalization now live in `Paper26PotionAliases`, with legacy IDs 1–32 unchanged.
- `PotionUtil.getPotionTypeByOriginalName` preserves existing aliases such as `INSTANT_HEAL → HEALING`, `INSTANT_DAMAGE → HARMING`, `REGEN → REGENERATION`, `JUMP → LEAPING`, and `SPEED → SWIFTNESS`.
- `PotionUtil.createPotion` remains unchanged: duration is still interpreted as ticks, amplifier is still `grade - 1`, and ambient/particle flags are passed through unchanged. The existing poison/wither victim-selection switch is untouched.
- Isolated Java 25 compilation includes the **actual** `PotionUtil` and `PotionTypeEnum` sources against Paper 26.3, not replacement stubs. `Paper26PotionAliasesSelfTest` verifies all 32 historical IDs and legacy config aliases. Those files and alias mapping now have CI source hashes to block silent later changes.
- `SupportMVdWPlaceholderAPI` retains its original class, public constructor and `setPlaceholders(OfflinePlayer, String)` contract, but discovers the optional legacy MVdW API lazily through its plugin class loader. It no longer requires a discontinued `be.maximvdw.placeholderapi` dependency at compile time. Existing `{...}` formatting is NOT silently converted to `%...`.
- The independent `compilePaper26MVdWBridge` task compiles the real optional adapter and original support interfaces **without** the MVdW JAR. Live behavior remains contingent on the external plugin being installed, compatible and enabled.
- No claim is made for removed `PotionType.UNCRAFTABLE` or other historic potion variants without a supported Paper equivalent; it must be handled explicitly when relevant fixtures are available.

**Still blocked:** The full AgarthaLib build requires migration or replacement of additional optional integrations, remaining versioned NMS classes and runtime tests. MyItems cannot yet be installed safely.

## Optional FeatherBoard migration (2026-10-30)

- `SupportFeatherBoard` preserves its original public constructor and all five public scoreboard operations, including the `toggle` overload with a `silent` flag, reset, show and remove functions.
- The legacy `be.maximvdw.featherboard.api.FeatherBoardAPI` is resolved lazily through the enabled FeatherBoard plugin's class loader and its exact old public method signatures. Its binary JAR is no longer a mandatory compile-time dependency. The class retains the original toggle-if-needed behavior.
- If `isToggled` cannot be resolved or invoked, the adapter deliberately **does not toggle**: guessing disabled could invert a player's scoreboard. Missing methods and absent/disabled plugins are handled without changing the underlying Bukkit scoreboard. No automatic replacement with Paper scoreboards is attempted.
- CI compiles the REAL hook with Java 25 and the Paper 26.3 API, and runs a separate test fixture (located exclusively under `ci/`) with a fake FeatherBoard API. Tests cover enabled/disabled, redundant toggles, `silent`, reset, show, remove, null inputs and missing external API. The fixture is never packaged in the plugin.
- This proves isolated source compatibility and deterministic forwarding, **not** equivalence with every current FeatherBoard release. Validate sidebars, re-enable behavior and priority with a real supported FeatherBoard installation before release.

## Optional integration migration: Essentials, LangUtils, Towny and MVdW registration (2026-10-30)

- **EssentialsX warps:** `SupportEssentials` keeps all five original public warp lookups (including world-filtered overloads). The implementation resolves `getWarps`, `getList`, and `getWarp(String)` on the real enabled Essentials plugin. The original collection is returned unchanged, warp names/locations are not rewritten, and missing warp / invalid world exceptions are skipped as before. Java CI uses a fake provider strictly under `ci/` (19 assertions). When the optional provider is unavailable, queries return empty/null rather than crashing.
- **LangUtils:** `SupportLangUtils` preserves all **24 overloaded public translation methods** for item, entity, enchantment and locale text. Its underlying `LanguageHelper` is now loaded only from the LangUtils plugin's class loader; no unavailable plugin classes are imported in the production source. Java CI covers representative overload forwarding, the exact locale string, disabled plugin and missing external API (12 assertions). A missing translation method returns null and emits a warning rather than inventing a localized value. The CI-only fake API is never packaged.
- **Towny:** the original `SupportTowny` / `SupportGroup` contracts compile against the real `com.palmergames.bukkit.towny:towny:0.103.2.0` public API via Towny's documented Maven repository. Calls to removed `getTownyUniverse()`, `getTownsMap()`, `getUuid()`, and `object.TownyUniverse.getOnlinePlayers()` were replaced by `TownyAPI.getInstance().getTowns()`, `Town.getUUID()`, and `TownyAPI.getInstance().getOnlinePlayersInTown(town)`. `getGroupIDs()` continues returning town names from the historic map keys, while separate `getGroupIDByName` / `getPlayerGroupID` continue returning UUID text. The original online-player-only behavior of `getPlayers(Town)` is intentionally unchanged, even though Towny offers broader offline-resident APIs.
- **Towny runtime caveat:** Towny 0.103.2.0 is used to compile against a stable published API, but Towny documents its **pre-release** line for Minecraft/Paper 26.3 runtime. This is a compile check only, not proof that Towny 0.103.2.0 itself runs on Paper 26.3. Use a 26.3-compatible Towny build when conducting runtime tests, with backups due to modern Towny's UUID database migration.
- **MVdW custom placeholder registration:** `ReplacerMVDWPlaceholderAPI` still registers the exact original wildcard `placeholder + "_*"`. Its callback still extracts the original suffix and calls the original AgarthaLib `PlaceholderManager.getReplacement(player, suffix)`. New `Paper26MVdWRegistration` dynamically implements the actual external `PlaceholderReplacer` interface only when MVdWPlaceholderAPI is installed and enabled, instead of requiring outdated API classes at compile time. CI compiles the real original replacer against CI-only internal signature stand-ins and verifies 16 cases covering identifier/owner/callback text/player and missing external API.
- **No silent gameplay change:** Existing MyItems abilities, YAML files, 20 commands, permission names, skill formulas and item serialization have not been edited in this wave. CI source-hash and command/ability guards remain active. Real-plugin integration behavior (especially town identity and locale rendering) requires Paper 26.3 server tests.
- **Status remains NOT releasable.** The full Java 25 AgarthaLib build still produces numerous errors from additional external integrations and historic versioned NMS/CraftBukkit packet handlers. Isolated compile/test success does not produce a valid AgarthaLib/MyItems JAR.

## Shared modern plugin APIs: PlaceholderAPI / EssentialsX / Vault (2026-10-30)

Verified against:
- PlaceholderAPI 2.12.3 (Modrinth says Paper/Minecraft 26.3): https://modrinth.com/plugin/placeholderapi and https://wiki.placeholderapi.com/developers/creating-a-placeholderexpansion/
- PlaceholderAPI expansion list: https://wiki.placeholderapi.com/users/placeholder-list/minecraft/ (e.g. the Player expansion is an independently installed eCloud expansion)
- EssentialsX 2.22.0 official release: https://github.com/EssentialsX/Essentials/releases/tag/2.22.0 and https://essentialsx.net/
- Original Vault plugin 1.7.3: https://www.spigotmc.org/resources/vault.34315/ ; VaultAPI 1.7 build contract: https://github.com/MilkBowl/VaultAPI
- VaultUnlocked 2.20.3 officially says support for 26.3: https://www.spigotmc.org/resources/vaultunlocked.117277/ . Its actual `plugin.yml` declares `name: Vault`, so existing plugin discovery is preserved.

### Implemented
- Replaced removed `PlaceholderHook` and `registerPlaceholderHook` with supported internal `PlaceholderExpansion`, using the unchanged `agarthalib` identifier, original `hook()` public method, original `onPlaceholderRequest(Player, String)` callback, and `persist() = true` so internal expansion survives PlaceholderAPI reloads.
- The original `PlaceholderManager.getReplacement(...)` used to return `null` unconditionally. It now delegates to the actual configured YAML via `getPlaceholder(identifier)`. `PlaceholderMemory.getPlaceholder(...)` now correctly retrieves the matched original YAML map key when the requested key uses a different case. The original YAML text, legacy color codes and nested placeholder tokens are returned without normalization or recursive parsing.
- The original 13-file protected YAML/combat/potion baseline remains unchanged. Java tests verify both the pure case-insensitive resolver (12 assertions) and the real `PlaceholderManager` + `PlaceholderMemory` classes with CI-only supporting fixtures (10 assertions). These tests do NOT replace a real PAPI server test.
- `SupportEssentials` remains a lazy optional provider, avoiding a hard runtime dependency on an unsupported API artifact. Source review of EssentialsX 2.22.0 confirms the real `Essentials.getWarps()`, `Warps.getList()`, and `Warps.getWarp(String)` signatures still used by this adapter. However the release does not explicitly claim Paper 26.3 compatibility. Test server behavior separately.
- The original `SupportVault` public `Economy` return type, `OfflinePlayer` account methods and positive/negative deposit/withdraw semantics remain. It now refreshes its `ServicesManager.getRegistration(Economy.class)` reference and clears a stale provider if the economy plugin is disabled or unregistered. This is important for economy plugin reloads.
- Both repositories compile against public VaultAPI 1.7 with `isTransitive = false`; that avoids accidentally pulling legacy `org.bukkit:bukkit:1.13.1` from VaultAPI's old POM and breaking the Paper 26.3 Gradle resolution. JitPack is scoped only to the MilkBowl GitHub group; EssentialsX 2.22.0 is documented as an optional runtime target rather than an unconditional artifact to bundle.
- The real modern PAPI and Vault adapters independently compile against Java 25/Paper 26.3. No external plugin implementation or test fixture is shaded into the production artifact.

### Paper 26.3 server smoke tests required
1. Install **one** Vault-compatible bridge: original Vault if verified working on your server, or VaultUnlocked 2.20.3 for a supported 26.3 candidate. Do **not** install both under plugin name `Vault`. Install an actual economy provider too; Vault is a bridge, not an economy database.
2. Test with PlaceholderAPI 2.12.3 using `/papi parse me %agarthalib_prefix%` and a mixed-case identifier, then confirm exact YAML text/color handling. The standalone Player expansion is needed for `%player_name%`: `/papi ecloud download Player`, `/papi reload`.
3. Test original account creation and positive/negative money changes with a real Vault Economy service, including disabling/re-enabling the provider. Never conduct this test on production balances.
4. Validate the world-specific EssentialsX warps on a safe Paper 26.3 test server with backed-up warp data and a compatible EssentialsX build.

**Release status still blocked:** full compilation still fails for legacy versioned NMS and other optional external APIs. These isolated regression tests do not verify skill runtime, old raw item NBT migration, or deployability.

## 2026-10-30 official optional service API alignment

- Project build uses the official PlaceholderAPI 2.12.3 `compileOnly` API (Modrinth: https://modrinth.com/plugin/placeholderapi ; development guide: https://wiki.placeholderapi.com/developers/using-placeholderapi/). Minecraft placeholders listed at https://wiki.placeholderapi.com/users/placeholder-list/minecraft/ depend on the corresponding installed expansion where indicated; the wiki is not a source for automatically bundling expansions.
- EssentialsX 2.22.0 publishes the `net.essentialsx:EssentialsX:2.22.0` Maven coordinate, but GitHub CI could **not resolve that artifact** on 2026-10-30, so it is **not** a build dependency. The existing warp adapter retains optional reflective access so EssentialsX does not become mandatory at plugin load. Its published 2.22.0 release describes support through Minecraft 26.1.2, not verified 26.3 runtime. Confirm a compatible EssentialsX build before a Paper 26.3 server launch.
- Vault uses official MilkBowl `com.github.MilkBowl:VaultAPI:1.7` `compileOnly`, with disabled transitive dependencies. This is only the shared API: a separately installed Vault-compatible service bridge and economy provider must supply the implementation. Never create balances or accounts merely to make a test pass.
- The original `SupportPlaceholderAPI` now preserves the exact input text when the optional PlaceholderAPI provider is disabled or missing. Dedicated tests check unchanged Minecraft/Vault placeholders and color codes. The enabled path still uses its historic online `Player` argument and PlaceholderAPI parser without altering legacy output formatting.
- These dependencies are aligned with MyItems but `compileOnly` is **not** installation, automatic expansion registration or proof of runtime support. Full NMS migration and complete builds remain outstanding.

Source references: https://modrinth.com/plugin/placeholderapi ; https://wiki.placeholderapi.com/users/placeholder-list/minecraft/ ; https://essentialsx.net/ ; https://www.spigotmc.org/resources/vault.34315/ .

**CI correction:** Do not add `net.essentialsx:EssentialsX:2.22.0` to the mandatory compile classpath until the artifact actually resolves in GitHub Actions. EssentialsX continues as an optional installed server plugin, and existing warp-forwarding tests remain in CI.

## 2026-10-30: modern LivingEntity combat-target bridge (proposed PR)

- Added `PacketLivingEntity_Paper26_3`, implementing the original `LivingEntityTools`
  overloads while using `EntityType.getEntityClass()` and
  `LivingEntity.class.isAssignableFrom(...)`; no CraftBukkit/NMS imports.
- `BridgeLivingEntity` routes `V26_3` to this adapter without deleting
  historical version implementations or changing gameplay ability sources.
- **Important semantic preservation:** Bukkit armor stands implement
  `LivingEntity`, but original AgarthaLib deliberately excluded them;
  the new adapter still excludes `ARMOR_STAND`. Arrow, dropped items and
  modern text/item holograms are not valid living combat targets.
- New Java 25 isolated compiler gate compiles the *actual* adapter with the
  original `LivingEntityTools` interface; an executable regression test checks
  Paper's registry class metadata, null inputs and combat target exceptions.
- This is an isolated component migration, **not** a full Paper 26.3 build or
  verified in-game combat target behavior. Entity selection must be tested on
  real Paper 26.3 fixtures before production deployment.

### Optional plugin replacements: compatibility policy

The existing direct Java imports for Jobs Reborn, Heroes, FactionsUUID/Massive
and HolographicDisplays are still **full-build blockers**. Do not silently
delete these public adapter classes or pretend that an incompatible replacement
provides the same types or persistent data.

- Jobs Reborn: EcoJobs (economy jobs) or AuraSkills (skill progression).
  A job salary is not equivalent to skill XP; disable old adapters only after
  explicit feature mapping.
- Heroes: evaluate MMOCore with its required runtime components; class names,
  health scaling and player level are not guaranteed to match old Heroes APIs.
- FactionsUUID/Massive: evaluate Lands or KingdomsX; claim/role semantics
  and offline ownership require provider-specific adapters.
- HolographicDisplays: prefer DecentHolograms, optionally FancyHolograms, via
  separate provider-specific hologram adapters. Legacy `Hologram` return types
  cannot be substituted with an unrelated API without breaking consumers.

Every new provider needs an explicitly optional adapter, provider-installed
runtime checks, read-only migration verification and relevant regression tests;
no automatic player data conversion, config rewrite or provider JAR bundling.
