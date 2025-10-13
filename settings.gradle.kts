pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.9"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        versions("1.20", "1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6")
        vcsVersion = "1.20.6"
        branch("fabric")
        branch("forge")
        branch("neoforge") { versions("1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6") }
    }
}

rootProject.name = "Undertale Death Screen"