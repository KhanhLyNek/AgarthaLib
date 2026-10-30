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
    compileOnly("me.clip:placeholderapi:2.11.6")

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
    source = fileTree("src/com/praya/agarthalib/modern") { include("Paper26ItemData.java", "Paper26ItemMeta.java", "Paper26StringListCodec.java", "Paper26SoundRegistry.java") }
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
