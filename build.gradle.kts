plugins {
    `java-library`
    `maven-publish`
}

group = "com.praya"
version = "2.17.2-paper26.3-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.helpch.at/releases/")
    // Official EssentialsX release API and VaultAPI as documented by MilkBowl.
    maven("https://repo.essentialsx.net/releases/")
    maven("https://jitpack.io") {
        content { includeGroup("com.github.MilkBowl") }
    }
    // TownyAdvanced documents this repository for its public Java API.
    maven("https://repo.glaremasters.me/repository/towny/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

dependencies {
    // Build-time API only: Paper provides these classes at runtime.
    compileOnly("io.papermc.paper:paper-api:26.3.build.230-stable")

    // Source-level dependencies found in the first GitHub Actions build log.
    // The historical JsonBuild and new hover payload parser both use this
    // library at runtime; Paper does not promise to provide it to plugins.
    // Read old raw item tags through a bounded NBT parser; no NMS imports.
    // Paper does not provide adventure-nbt on its plugin classpath.
    implementation("net.kyori:adventure-nbt:5.2.0") {
        isTransitive = false
    }
    implementation("com.googlecode.json-simple:json-simple:1.1.1") {
        isTransitive = false
    }
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.20")
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("org.jetbrains:annotations:26.0.2")
    // API-only: reject VaultAPI's stale Bukkit 1.13 transitive dependency.
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }
    // EssentialsX 2.22.0 artifact is not resolvable by GitHub CI. Keep
    // its optional runtime warp adapter via verified reflection, not a
    // mandatory compile classpath dependency that prevents all builds.
    // EssentialsX 2.22.0 is an optional runtime integration: its real warp
    // API is resolved by SupportEssentials without linking its unstable JAR.
    compileOnly("com.palmergames.bukkit.towny:towny:0.103.2.0")

    // Remaining integrations must be ported or supplied with compatible APIs:
    // Factions variants, Jobs, Heroes, Towny, HolographicDisplays and others.
    // Historical NMS/CraftBukkit packet handlers still block compilation.
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        // Build target is Paper 26.3 only. Keep all v1_7..v1_15
        // source files for audit/history, but never ship incompatible NMS.
        // All public Bridge* entry points now select modern Paper handlers.
        java.exclude("**/packet/**/Packet*_1_*.java")
        // Obsolete compile-only external plugin APIs are not part of the
        // Paper build; historic implementations stay in Git for reference.
        java.exclude("**/builder/support/SupportJobsReborn.java")
        java.exclude("**/builder/support/SupportHolographicDisplays.java")
        java.exclude("**/builder/support/SupportHeroes.java")
        java.exclude("**/builder/support/SupportSkillsPro.java")
        java.exclude("**/builder/support/SupportCombatStamina.java")
        java.exclude("**/builder/support/SupportLifeEssence.java")
        java.exclude("**/builder/support/SupportKingdoms.java")
        java.exclude("**/builder/support/SupportMcMMO.java")
        java.exclude("**/builder/support/SupportBattleLevels.java")
        java.exclude("**/builder/support/SupportCitizens.java")
        java.exclude("**/builder/support/SupportMyItems.java")
        java.exclude("**/builder/support/SupportSkillAPI.java")
        java.exclude("**/builder/support/SupportGangs.java")
        java.exclude("**/builder/support/SupportSimpleClans.java")
        java.exclude("**/listener/support/heroes/**")
        java.exclude("**/listener/support/skillapi/**")
        java.exclude("**/listener/support/battlelevels/**")
        resources.setSrcDirs(listOf("resources"))
    }
}

// AgarthaLib is deployed as a plugin jar, not through a Gradle classpath.
// Embed JSON-simple, otherwise rendering existing messages may fail with
// NoClassDefFoundError in a standalone server installation.
tasks.named<Jar>("jar") {
    from({
        configurations.runtimeClasspath.get()
            .filter { (it.name.startsWith("json-simple-") || it.name.startsWith("adventure-nbt-")) }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
    // Show more than javac's default 100 errors so one CI run exposes the next API blockers.
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "1000", "-Xmaxwarns", "200"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

// Compile the modern item storage independently while the decompiled v1_* NMS
// classes are migrated. This task never treats a failed full build as success.
tasks.register<JavaCompile>("compilePaper26ItemData") {
    description = "Type-check the Paper 26.3 item storage and metadata services without legacy NMS"
    group = "verification"
    source = fileTree("src/com/praya/agarthalib/modern") { include("Paper26ItemData.java", "Paper26ItemMeta.java", "Paper26StringListCodec.java", "Paper26SoundRegistry.java", "Paper26AudienceService.java", "Paper26BlockDamage.java", "Paper26PotionAliases.java", "Paper26MVdWRegistration.java", "Paper26PlaceholderValues.java", "Paper26MythicLibStats.java", "Paper26InventoryFactory.java", "Paper26JsonChat.java", "Paper26AuraSkillsProgress.java", "Paper26MMOCoreProgress.java", "Paper26LandsClaims.java", "Paper26DecentHolograms.java", "Paper26OptionalProviders.java", "Paper26ItemSnapshots.java", "Paper26HoverItems.java", "Paper26HoverItemPayload.java", "Paper26ServerTPS.java") }
    classpath = configurations["compileClasspath"]
    destinationDirectory.set(layout.buildDirectory.dir("paper26-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Compile the real 26.3 packet bridge and its original public NBT interfaces
// with only CI-scope stand-ins for legacy AgarthaLib implementation classes.
// These stubs NEVER enter the plugin's runtime source set or published jar.
tasks.register<JavaCompile>("compilePaper26ItemBridge") {
    description = "Type-check the real Paper 26.3 item adapter against preserved NBT interfaces"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/item/PacketItem_Paper26_3.java")
        include("src/core/praya/agarthalib/builder/bridge/TagsNBTItem.java")
        include("src/core/praya/agarthalib/builder/bridge/TagsNBTBooks.java")
        include("src/core/praya/agarthalib/builder/bridge/TagsNBTCustom.java")
        include("ci/paper26-bridge-stubs/**/*.java")
    }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-bridge-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Exercise the actual Java codec (not a re-implementation in Python).
tasks.register<JavaCompile>("compilePaper26CodecSelfTest") {
    description = "Compile byte-exact item string-list serialization tests"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-codec-test") { include("**/*.java") }
    classpath = files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-codec-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26StringListCodec") {
    description = "Test UTF-8 lists, >64 KiB strings, preview format, and corrupt data"
    group = "verification"
    dependsOn("compilePaper26CodecSelfTest")
    classpath = files(
        layout.buildDirectory.dir("paper26-codec-tests"),
        layout.buildDirectory.dir("paper26-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26StringListCodecSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

tasks.named("compilePaper26ItemBridge") {
    dependsOn("testPaper26StringListCodec")
}

// Type-check the real Paper player adapter against the original public
// player packet contracts; the old NMS implementations remain untouched.
tasks.register<JavaCompile>("compilePaper26PlayerBridge") {
    description = "Compile the real Paper 26.3 player bridge using its legacy interfaces"
    group = "verification"
    dependsOn("compilePaper26ItemBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/player/PacketPlayer_Paper26_3.java")
        include("src/com/praya/agarthalib/packet/player/PacketPlayerProperties.java")
        include("src/com/praya/agarthalib/packet/player/PacketPlayerVisibility.java")
        include("src/com/praya/agarthalib/packet/player/PacketPlayerAbsorption.java")
        include("src/com/praya/agarthalib/packet/player/PacketPlayerCombat.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-player-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Compile the real Paper actionbar/title adapter against the old interfaces.
// JSON click/hover/item components intentionally remain a separate blocker.
tasks.register<JavaCompile>("compilePaper26MessageBridge") {
    description = "Type-check Adventure actionbar/title adapter without legacy NMS"
    group = "verification"
    dependsOn("compilePaper26PlayerBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/message/PacketMessage_Paper26_3.java")
        // Compile the runtime hover payload in this isolated adapter check too.
        // The message bridge must not depend on unverified implicit class outputs.
        include("src/com/praya/agarthalib/modern/Paper26HoverItemPayload.java")
        include("src/core/praya/agarthalib/builder/bridge/MessageActionbar.java")
        include("src/core/praya/agarthalib/builder/bridge/MessageTitle.java")
        include("src/core/praya/agarthalib/builder/bridge/MessageJson.java")
        include("ci/paper26-json-stubs/com/praya/agarthalib/utility/JsonUtil.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-bridge-api-check")) +
        files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-message-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26ServerTPSSelfTest") {
    description = "Compile legacy TPS rounding/clamping regression checks"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-tps-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-tps-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}
tasks.register<JavaExec>("testPaper26ServerTPS") {
    description = "Check original first-minute TPS formatting on Paper"
    group = "verification"
    dependsOn("compilePaper26ServerTPSSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-tps-tests"),
        layout.buildDirectory.dir("paper26-api-check"))
    mainClass.set("com.praya.agarthalib.modern.Paper26ServerTPSSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

// Source-isolated provider fixtures are not shipped with the plugin.
tasks.register<JavaCompile>("compilePaper26ModernProvidersSelfTest") {
    description = "Compile optional AuraSkills, MMOCore, Lands and DecentHolograms JVM contracts"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-modern-provider-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-modern-provider-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}
tasks.register<JavaExec>("testPaper26ModernProviders") {
    description = "Verify read-only provider compatibility and absent API fallback"
    group = "verification"
    dependsOn("compilePaper26ModernProvidersSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-modern-provider-tests"),
        layout.buildDirectory.dir("paper26-api-check"))
    mainClass.set("com.praya.agarthalib.modern.Paper26ModernProvidersSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}
tasks.register<JavaCompile>("compilePaper26ItemSnapshotSelfTest") {
    description = "Compile item snapshot rejection/hover null checks"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-item-snapshot-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-item-snapshot-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}
tasks.register<JavaExec>("testPaper26ItemSnapshots") {
    description = "Reject unsafe item backup data without editing any inventory"
    group = "verification"
    dependsOn("compilePaper26ItemSnapshotSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-item-snapshot-tests"),
        layout.buildDirectory.dir("paper26-api-check"))
    mainClass.set("com.praya.agarthalib.modern.Paper26ItemSnapshotsSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

// Verify modern inventory and equipment adapters against preserved public contracts.
tasks.register<JavaCompile>("compilePaper26InventoryEquipmentBridges") {
    description = "Compile Paper 26.3 inventory and equipment bridge sources"
    group = "verification"
    dependsOn("compilePaper26ItemBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/inventory/PacketInventory_Paper26_3.java")
        include("src/com/praya/agarthalib/packet/equipment/PacketEquipment_Paper26_3.java")
        include("src/core/praya/agarthalib/builder/bridge/WindowTools.java")
        include("src/core/praya/agarthalib/builder/bridge/EquipmentTools.java")
    }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-inventory-equipment-api-check"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Verify the actual 26.3 arrow and material replacements using original
// public handler interfaces and only test-scoped AgarthaLib base stubs.
tasks.register<JavaCompile>("compilePaper26ArrowMaterial") {
    description = "Compile current-only Bukkit arrow and material packet replacements"
    group = "verification"
    dependsOn("compilePaper26ItemBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/arrow/PacketArrow_Paper26_3.java")
        include("src/com/praya/agarthalib/packet/arrow/PacketArrowNormal.java")
        include("src/com/praya/agarthalib/packet/arrow/PacketArrowModification.java")
        include("src/com/praya/agarthalib/packet/material/PacketMaterial_Paper26_3.java")
        include("src/core/praya/agarthalib/builder/bridge/MaterialTools.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-arrow-material-api-check"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Production JsonUtil remains the original DSL implementation. Only the
// JSON serializing transport is independently validated here.
tasks.register<JavaCompile>("compilePaper26JsonSelfTest") {
    description = "Compile rich Adventure JSON message self-tests"
    group = "verification"
    dependsOn("compilePaper26MessageBridge")
    source = fileTree("ci/paper26-json-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-json-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26JsonChat") {
    description = "Check click, hover, Unicode, and malformed component handling"
    group = "verification"
    dependsOn("compilePaper26JsonSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-json-tests"),
        layout.buildDirectory.dir("paper26-api-check"))
    mainClass.set("com.praya.agarthalib.modern.Paper26JsonChatSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

tasks.register<JavaCompile>("compilePaper26MythicLibSelfTest") {
    description = "Compile MythicLib isolated stat API compatibility fixtures"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-mythiclib-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-mythiclib-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26MythicLibOptional") {
    description = "Read-only MythicLib values and safe disabled-provider fallback"
    group = "verification"
    dependsOn("compilePaper26MythicLibSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-mythiclib-tests"),
        layout.buildDirectory.dir("paper26-api-check"))
    mainClass.set("com.praya.agarthalib.modern.Paper26MythicLibStatsSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

tasks.named("compilePaper26MessageBridge") { dependsOn("compilePaper26InventoryEquipmentBridges", "compilePaper26ArrowMaterial") }

// Verify the real block animation adapter and its unchanged packet interface.
// CI-only AgarthaLib class stubs were already compiled by the item-bridge task.
tasks.register<JavaCompile>("compilePaper26BlockBridge") {
    description = "Type-check the Paper 26.3 block-damage bridge and original interface"
    group = "verification"
    dependsOn("compilePaper26MessageBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/packet/block/PacketBlock_Paper26_3.java")
        include("src/com/praya/agarthalib/packet/block/PacketBlockAnimation.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-api-check")) +
        files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-block-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26BlockSelfTest") {
    description = "Compile the block cracking-stage mapping checks"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-block-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-block-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26BlockDamage") {
    description = "Check legacy 0-9 block-crack stage progress boundaries"
    group = "verification"
    dependsOn("compilePaper26BlockSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-block-tests"),
        layout.buildDirectory.dir("paper26-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26BlockDamageSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

tasks.named("compilePaper26BlockBridge") {
    dependsOn("testPaper26BlockDamage")
}

// Compile the original potion API utility and PotionTypeEnum against Paper 26.3,
// preserving the public signatures and MyItems damage/duration/amplifier logic.
tasks.register<JavaCompile>("compilePaper26PotionUtility") {
    description = "Compile real PotionUtil and PotionTypeEnum with modern registry aliases"
    group = "verification"
    dependsOn("compilePaper26BlockBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/utility/PotionUtil.java")
        include("src/core/praya/agarthalib/enums/branch/PotionTypeEnum.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-potion-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26PotionAliasesSelfTest") {
    description = "Compile all legacy potion ID and YAML name regression cases"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-potion-test") { include("**/*.java") }
    classpath = files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-potion-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26PotionAliases") {
    description = "Verify all historical effect ID/name mappings before plugin build"
    group = "verification"
    dependsOn("compilePaper26PotionAliasesSelfTest")
    classpath = files(
        layout.buildDirectory.dir("paper26-potion-tests"),
        layout.buildDirectory.dir("paper26-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26PotionAliasesSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

tasks.named("compilePaper26PotionUtility") {
    dependsOn("testPaper26PotionAliases")
}

// MVdWPlaceholderAPI is an optional legacy plugin. The real adapter and its
// public SupportPlaceholder contract must compile without the external JAR.
tasks.register<JavaCompile>("compilePaper26PlaceholderBridges") {
    description = "Compile both original PlaceholderAPI and optional MVdW hooks with current APIs"
    group = "verification"
    dependsOn("compilePaper26PotionUtility")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportMVdWPlaceholderAPI.java")
        include("src/api/praya/agarthalib/builder/support/SupportPlaceholderAPI.java")
        include("src/api/praya/agarthalib/builder/support/main/Support.java")
        include("src/api/praya/agarthalib/builder/support/main/SupportPlaceholder.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-placeholder-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// FeatherBoard is optional. Type-check the REAL hook without shipping a
// FeatherBoard API JAR or fake classes in the plugin's production source set.
tasks.register<JavaCompile>("compilePaper26FeatherBoardBridge") {
    description = "Compile the preserved FeatherBoard integration against Paper without its old API JAR"
    group = "verification"
    dependsOn("compilePaper26PlaceholderBridges")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportFeatherBoard.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-featherboard-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// The fake external API lives in ci/ and never enters the production jar.
// This tests boolean state, toggle-if-needed behavior, silent flag and names.
tasks.register<JavaCompile>("compilePaper26FeatherBoardSelfTest") {
    description = "Compile real-adapter tests against a CI-only fake FeatherBoard API"
    group = "verification"
    dependsOn("compilePaper26FeatherBoardBridge")
    source = fileTree("ci/paper26-featherboard-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-featherboard-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-featherboard-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26FeatherBoard") {
    description = "Test optional FeatherBoard API forwarding and original scoreboard toggling"
    group = "verification"
    dependsOn("compilePaper26FeatherBoardSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-featherboard-tests"),
        layout.buildDirectory.dir("paper26-featherboard-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26FeatherBoardSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Retain EssentialsX warp discovery and world filtering, while removing the
// hard compile-time dependency on its version-specific Java classes.
tasks.register<JavaCompile>("compilePaper26EssentialsBridge") {
    description = "Compile real Essentials warp adapter without an EssentialsX JAR"
    group = "verification"
    dependsOn("testPaper26FeatherBoard")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportEssentials.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-essentials-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26EssentialsSelfTest") {
    description = "Compile real-adapter Essentials warp and world-filtering tests"
    group = "verification"
    dependsOn("compilePaper26EssentialsBridge")
    source = fileTree("ci/paper26-essentials-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-essentials-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-essentials-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26EssentialsWarps") {
    description = "Check optional Essentials warp identity, coordinates, world filtering and invalid entries"
    group = "verification"
    dependsOn("compilePaper26EssentialsSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-essentials-tests"),
        layout.buildDirectory.dir("paper26-essentials-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26EssentialsWarpSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// LangUtils is another optional legacy integration. Compile the ORIGINAL
// 24 public method overloads without requiring its obsolete external JAR.
tasks.register<JavaCompile>("compilePaper26LangUtilsBridge") {
    description = "Compile real legacy LangUtils adapter with Paper API and no external JAR"
    group = "verification"
    dependsOn("testPaper26EssentialsWarps")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportLangUtils.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-langutils-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26LangUtilsSelfTest") {
    description = "Compile tests for optional LangUtils overloads with isolated fake API"
    group = "verification"
    dependsOn("compilePaper26LangUtilsBridge")
    source = fileTree("ci/paper26-langutils-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-langutils-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-langutils-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26LangUtils") {
    description = "Check LangUtils translation calls, typed overloads and missing-plugin behavior"
    group = "verification"
    dependsOn("compilePaper26LangUtilsSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-langutils-tests"),
        layout.buildDirectory.dir("paper26-langutils-api-check"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26LangUtilsSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Test the actual Towny adapter against the plugin's current published Java
// API without modifying town/claim/group calculations.
tasks.register<JavaCompile>("compilePaper26TownyBridge") {
    description = "Compile original Towny group contract against current Towny API"
    group = "verification"
    dependsOn("testPaper26LangUtils")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportTowny.java")
        include("src/api/praya/agarthalib/builder/support/main/SupportGroup.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-towny-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Compile the REAL legacy custom-placeholder registration class without
// shipping discontinued MVdW API classes. CI manager stubs come exclusively
// from the earlier isolated bridge compilation.
tasks.register<JavaCompile>("compilePaper26MVdWReplacer") {
    description = "Compile original MVdW wildcard registration with modern optional API bridge"
    group = "verification"
    dependsOn("compilePaper26TownyBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/placeholder/replacer/ReplacerMVDWPlaceholderAPI.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-mvdw-replacer-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26MVdWRegistrationSelfTest") {
    description = "Compile real MVdW callback registration tests with CI-only fake external classes"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-mvdw-registration-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-mvdw-registration-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26MVdWRegistration") {
    description = "Test preserved MVdW wildcard ID, replacement callback and missing provider"
    group = "verification"
    dependsOn("compilePaper26MVdWRegistrationSelfTest", "compilePaper26MVdWReplacer")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-mvdw-registration-tests"),
        layout.buildDirectory.dir("paper26-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26MVdWRegistrationSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Compile the actual replacement for removed PlaceholderHook against the
// modern PlaceholderAPI 2.12.3 public expansion API. Internal stubs are
// compile-only, and never enter sourceSets.main or published JARs.
tasks.register<JavaCompile>("compilePaper26PapiExpansion") {
    description = "Type-check preserved placeholder prefix with PlaceholderExpansion 2.12.3"
    group = "verification"
    dependsOn("testPaper26MVdWRegistration")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/placeholder/replacer/ReplacerPlaceholderAPI.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-papi-expansion-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26PlaceholderValuesSelfTest") {
    description = "Compile legacy YAML placeholder lookup regression tests"
    group = "verification"
    dependsOn("compilePaper26ItemData")
    source = fileTree("ci/paper26-placeholder-values-test") { include("**/*.java") }
    classpath = files(layout.buildDirectory.dir("paper26-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-placeholder-values-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26PlaceholderValues") {
    description = "Protect configured placeholder names, colors and original values"
    group = "verification"
    dependsOn("compilePaper26PlaceholderValuesSelfTest", "compilePaper26PapiExpansion")
    classpath = files(
        layout.buildDirectory.dir("paper26-placeholder-values-tests"),
        layout.buildDirectory.dir("paper26-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26PlaceholderValuesSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Preserve Vault Economy's public type: the same service contract is provided
// by original Vault and VaultUnlocked. No economy data migrations or rounding.
tasks.register<JavaCompile>("compilePaper26VaultBridge") {
    description = "Compile original Vault economy adapter against official VaultAPI 1.7"
    group = "verification"
    dependsOn("testPaper26PlaceholderValues")
    source = fileTree(".") {
        include("src/api/praya/agarthalib/builder/support/SupportVault.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-vault-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Compile both original placeholder classes and handler hierarchy. External
// providers, config loader and helper signatures are CI-only stubs so no
// fake implementation ever enters the production source set.
tasks.register<JavaCompile>("compilePaper26OriginalPlaceholderManager") {
    description = "Type-check real PlaceholderManager and PlaceholderMemory with preserved Bukkit entrypoints"
    group = "verification"
    dependsOn("compilePaper26VaultBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/handler/Handler.java")
        include("src/com/praya/agarthalib/handler/HandlerManager.java")
        include("src/com/praya/agarthalib/manager/plugin/PlaceholderManager.java")
        include("src/com/praya/agarthalib/placeholder/PlaceholderMemory.java")
        include("ci/paper26-placeholder-manager-stubs/**/*.java")
    }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check"),
        layout.buildDirectory.dir("paper26-mvdw-replacer-api-check"),
        layout.buildDirectory.dir("paper26-papi-expansion-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-original-placeholder-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26OriginalPlaceholderManagerSelfTest") {
    description = "Compile real-placeholder-class tests using CI-only config fixture"
    group = "verification"
    dependsOn("compilePaper26OriginalPlaceholderManager")
    source = fileTree("ci/paper26-placeholder-manager-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-original-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-original-placeholder-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26OriginalPlaceholderManager") {
    description = "Verify actual historic PlaceholderManager and PlaceholderMemory resolve YAML through PAPI"
    group = "verification"
    dependsOn("compilePaper26OriginalPlaceholderManagerSelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-original-placeholder-tests"),
        layout.buildDirectory.dir("paper26-original-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check"),
        layout.buildDirectory.dir("paper26-papi-expansion-api-check"),
        layout.buildDirectory.dir("paper26-mvdw-replacer-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26OriginalPlaceholderManagerSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Verify the original optional PlaceholderAPI adapter does not call a disabled
// provider or mutate placeholder expressions, including Vault expansions.
tasks.register<JavaCompile>("compilePaper26PapiOptionalSelfTest") {
    description = "Compile original optional PAPI adapter fallback checks"
    group = "verification"
    dependsOn("compilePaper26PlaceholderBridges")
    source = fileTree("ci/paper26-papi-optional-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-papi-optional-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

// Compile the actual Paper 26.3 combat target classifier, old public bridge
// interface and new packet adapter without any ancient NMS implementations.
tasks.register<JavaCompile>("compilePaper26LivingEntityBridge") {
    description = "Type-check the Paper 26.3 LivingEntityTools adapter and classifier"
    group = "verification"
    dependsOn("compilePaper26ItemBridge")
    source = fileTree(".") {
        include("src/com/praya/agarthalib/modern/Paper26LivingEntityKind.java")
        include("src/com/praya/agarthalib/packet/livingentity/PacketLivingEntity_Paper26_3.java")
        include("src/core/praya/agarthalib/builder/bridge/LivingEntityTools.java")
    }
    classpath = configurations["compileClasspath"] +
        files(layout.buildDirectory.dir("paper26-bridge-api-check"))
    destinationDirectory.set(layout.buildDirectory.dir("paper26-livingentity-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaCompile>("compilePaper26LivingEntitySelfTest") {
    description = "Compile entity, armor-stand, projectile and display-entity regression tests"
    group = "verification"
    dependsOn("compilePaper26LivingEntityBridge")
    source = fileTree("ci/paper26-livingentity-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-livingentity-api-check")
    )
    destinationDirectory.set(layout.buildDirectory.dir("paper26-livingentity-tests"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26LivingEntityKind") {
    description = "Test real Paper entity metadata and legacy combat target exclusions"
    group = "verification"
    dependsOn("compilePaper26LivingEntitySelfTest")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-livingentity-tests"),
        layout.buildDirectory.dir("paper26-livingentity-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26LivingEntityKindSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

// Exercise the exact old DSL->JsonBuild pipeline, including real Paper 26
// versioned hover items, click commands and legacy value-based text tooltips.
tasks.register<JavaCompile>("compilePaper26HoverPipelineSelfTest") {
    description = "Compile actual JsonUtil/JsonBuild regression tests"
    group = "verification"
    dependsOn("classes")
    source = fileTree("ci/paper26-hover-pipeline-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + sourceSets.main.get().output
    destinationDirectory.set(layout.buildDirectory.dir("paper26-hover-pipeline-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}
tasks.register<JavaCompile>("compilePaper26LegacyNbtSelfTest") {
    description = "Compile real read-only raw NBT compatibility fixtures"
    group = "verification"
    dependsOn("classes")
    source = fileTree("ci/paper26-legacy-nbt-test") { include("**/*.java") }
    classpath = configurations["compileClasspath"] + sourceSets.main.get().output
    destinationDirectory.set(layout.buildDirectory.dir("paper26-legacy-nbt-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26LegacyNbt") {
    description = "Verify historical string, compound-stat and typed primitive tag readers"
    group = "verification"
    dependsOn("compilePaper26LegacyNbtSelfTest")
    classpath = configurations["compileClasspath"] + sourceSets.main.get().output +
            files(layout.buildDirectory.dir("paper26-legacy-nbt-tests"))
    mainClass.set("com.praya.agarthalib.modern.Paper26LegacyItemTagsSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

tasks.register<JavaExec>("testPaper26HoverPipeline") {
    description = "Test old DSL and modern show_item.contents with unchanged click/text behavior"
    group = "verification"
    dependsOn("compilePaper26HoverPipelineSelfTest")
    classpath = configurations["compileClasspath"] + sourceSets.main.get().output +
            files(layout.buildDirectory.dir("paper26-hover-pipeline-tests"))
    mainClass.set("com.praya.agarthalib.modern.Paper26HoverPipelineSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

// Verify the actual encoded hover payload and strict item-stack counts using
// the same JSON-simple parser shipped in the historical AgarthaLib sources.
tasks.register<JavaCompile>("compilePaper26HoverItemPayloadSelfTest") {
    description = "Compile real versioned hover item payload against JSON-simple"
    group = "verification"
    source = fileTree(".") {
        include("src/com/praya/agarthalib/modern/Paper26HoverItemPayload.java")
        include("ci/paper26-hover-item-test/**/*.java")
    }
    classpath = configurations["compileClasspath"]
    destinationDirectory.set(layout.buildDirectory.dir("paper26-hover-item-tests"))
    javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(25)) })
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.register<JavaExec>("testPaper26HoverItemPayload") {
    description = "Reject oversized or nonintegral item counts and preserve UTF-8 hover items"
    group = "verification"
    dependsOn("compilePaper26HoverItemPayloadSelfTest")
    classpath = configurations["compileClasspath"] +
            files(layout.buildDirectory.dir("paper26-hover-item-tests"))
    mainClass.set("com.praya.agarthalib.modern.Paper26HoverItemPayloadSelfTest")
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) })
}

tasks.register<JavaExec>("testPaper26PapiOptional") {
    description = "Verify disabled/missing PlaceholderAPI retains exact placeholder and formatting"
    group = "verification"
    dependsOn("compilePaper26PapiOptionalSelfTest", "testPaper26OriginalPlaceholderManager", "testPaper26LivingEntityKind", "testPaper26JsonChat", "testPaper26MythicLibOptional", "testPaper26ModernProviders", "testPaper26ItemSnapshots", "testPaper26ServerTPS", "testPaper26HoverItemPayload", "testPaper26HoverPipeline", "testPaper26LegacyNbt")
    classpath = configurations["compileClasspath"] + files(
        layout.buildDirectory.dir("paper26-papi-optional-tests"),
        layout.buildDirectory.dir("paper26-placeholder-api-check"),
        layout.buildDirectory.dir("paper26-bridge-api-check")
    )
    mainClass.set("com.praya.agarthalib.modern.Paper26PapiOptionalSelfTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}
