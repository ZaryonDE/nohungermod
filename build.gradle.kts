import java.util.Properties

plugins {
    id("fabric-loom") version "1.11.1"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
}

group = "de.zaryon"

// -----------------------------
// Automatische Versionierung
// -----------------------------
val versionFile = file("version.properties")
val versionProps = Properties()
if (versionFile.exists()) {
    versionProps.load(versionFile.inputStream())
} else {
    versionProps.setProperty("modVersion", "1.0.0")
    versionFile.outputStream().use { versionProps.store(it, null) }
}

fun incrementVersion(ver: String): String {
    val parts = ver.split(".").map { it.toInt() }.toMutableList()
    parts[2] = parts[2] + 1
    return parts.joinToString(".")
}

var currentVersion = versionProps.getProperty("modVersion")
version = currentVersion

tasks.named("build") {
    doFirst {
        val newVersion = incrementVersion(currentVersion)
        versionProps.setProperty("modVersion", newVersion)
        versionFile.outputStream().use { versionProps.store(it, null) }
        println("Version updated: $currentVersion -> $newVersion")
        currentVersion = newVersion
        project.version = newVersion
    }
}

// -----------------------------
base {
    archivesName = "NoHunger-1.21.7-Fabric"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.7")
    mappings("net.fabricmc:yarn:1.21.7+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.17.2")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.129.0+1.21.7")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:19.0.147")
    modImplementation("com.terraformersmc:modmenu:15.0.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

// -----------------------------
// Modrinth Upload
// -----------------------------
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("NoHungerMod")
    versionNumber.set(version.toString())
    versionType.set("release")
    uploadFile.set(tasks.remapJar)
    gameVersions.addAll(listOf("1.21.x"))
    loaders.add("fabric")
    changelog.set(System.getenv("GITHUB_RELEASE_BODY") ?: "Automatischer Release-Build")
}
