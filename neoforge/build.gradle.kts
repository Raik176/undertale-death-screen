plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin")

    id("dev.kikugie.fletching-table.neoforge")
}

val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

architectury {
    platformSetupLoomIde()
    neoForge()
}

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

    modImplementation("me.shedaniel.cloth:cloth-config-neoforge:${common.mod.dep("cloth_config")}")
}

fun convertMinecraftTargets(): String {
    return "[" + common.mod.prop("mc_targets").split(" ").joinToString(", ") + "]"
}

tasks.processResources {
    properties(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "description" to mod.prop("description"),
        "author" to mod.prop("author"),
        "license" to mod.prop("license"),
        "minecraft" to convertMinecraftTargets()
    )
}

publishMods {
    modLoaders.add("neoforge")
    displayName = "${common.mod.version} for Neoforge $minecraft"
}