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
    description = "Type-check the Paper 26.3 PDC service without legacy NMS sources"
    group = "verification"
    source = files("src/com/praya/agarthalib/modern/Paper26ItemData.java")
    classpath = configurations["compileClasspath"]
    destinationDirectory.set(layout.buildDirectory.dir("paper26-api-check"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.encoding = "UTF-8"
    options.release.set(25)
}
