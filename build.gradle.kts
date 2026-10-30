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
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

dependencies {
    // Build-time API only: Paper provides these classes at runtime.
    compileOnly("io.papermc.paper:paper-api:26.3.build.230-stable")

    // Source-level dependencies found in the first GitHub Actions build log.
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.20")
    compileOnly("me.clip:placeholderapi:2.12.3")

    // Remaining integrations must be ported or supplied with compatible APIs:
    // Factions variants, Jobs, Heroes, Towny, HolographicDisplays and others.
    // Historical NMS/CraftBukkit packet handlers still block compilation.
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
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
    source = fileTree("src/com/praya/agarthalib/modern") { include("Paper26ItemData.java", "Paper26ItemMeta.java", "Paper26StringListCodec.java", "Paper26SoundRegistry.java", "Paper26AudienceService.java", "Paper26BlockDamage.java", "Paper26PotionAliases.java") }
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
        include("src/core/praya/agarthalib/builder/bridge/MessageActionbar.java")
        include("src/core/praya/agarthalib/builder/bridge/MessageTitle.java")
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
