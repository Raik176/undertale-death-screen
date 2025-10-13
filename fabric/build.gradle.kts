plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin")

    id("dev.kikugie.fletching-table.fabric")
}

val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${common.mod.dep("fabric_api")}")

    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${common.mod.dep("cloth_config")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("com.terraformersmc:modmenu:${common.mod.dep("modmenu")}")
}

fun convertMinecraftTargets(): String {
    val split = common.mod.prop("mc_targets").split(" ")
    return ">=${split[0]} <=${split[split.size-1]}"
}

tasks.processResources {
    properties(listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "description" to mod.prop("description"),
        "author" to mod.prop("author"),
        "license" to mod.prop("license"),
        "minecraft" to convertMinecraftTargets(),
        "group" to mod.group
    )
}

publishMods {
    modLoaders.addAll("fabric", "quilt")
    displayName = "${common.mod.version} for Fabric $minecraft"

    modrinth {
        requires {
            slug = "fabric-api"
        }
    }

    curseforge {
        requires("fabric-api")
    }
}