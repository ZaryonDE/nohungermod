pluginManagement {
    repositories {
        gradlePluginPortal()                   // Standard für Gradle-Plugins
        maven("https://maven.fabricmc.net/")  // Fabric/Loom Plugins
        mavenCentral()                        // Allgemeine Plugins
    }
}

dependencyResolutionManagement {
    // Experimental/Incubating, aber safe für dein Projekt
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://api.modrinth.com/maven")
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/")
    }
}

rootProject.name = "nohunger-mod"

// Für zukünftige Subprojekte vorbereitet
// include("common", "client")
