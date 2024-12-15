@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.version

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin") version "0.7.4"
}

val loader = prop("loom.platform")!!
var minecraft: String = stonecutter.current.version
if (minecraft == "1.21.2") // forge only supports 1.21.3 and neoforge only supports 1.21.2
    minecraft = "1.21.3"
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set("${mod.id}-$loader")
}
architectury {
    platformSetupLoomIde()
    forge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.minecraftforge.net")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:$minecraft-${common.mod.dep("forge_loader")}")
    "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
        implementation(it)
        include(it)
    }

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    forge.convertAccessWideners = true
    forge.mixinConfigs(
        "${mod.id}-common.mixins.json"
    )

    runConfigs.all {
        isIdeConfigGenerated = false
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude("fabric.mod.json", "architectury.common.json")
}

fun convertMinecraftTargets(): String {
    return "[" + common.mod.prop("mc_targets").split(" ").joinToString(", ") + "]"
}

tasks.processResources {
    properties(listOf("META-INF/mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "description" to mod.prop("description"),
        "author" to mod.prop("author"),
        "license" to mod.prop("license"),
        "minecraft" to convertMinecraftTargets()
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

publishMods {
    file.set(tasks.named<RemapJarTask>("remapJar").flatMap { it.archiveFile })
    changelog = providers.fileContents(common.layout.projectDirectory.file("../../CHANGELOG.md")).asText.get()
    modLoaders.add("forge")
    type = STABLE
    displayName = "${common.mod.version} for Forge $minecraft"

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = common.extra["modrinthId"].toString()
        minecraftVersions.addAll(common.mod.prop("mc_targets").split(" "))
        projectDescription = providers.fileContents(common.layout.projectDirectory.file("../../README.md")).asText.get()
    }
    curseforge {
        accessToken = providers.environmentVariable("CF_API_KEY")
        projectId = common.extra["curseforgeId"].toString()
        minecraftVersions.addAll(common.mod.prop("mc_targets").split(" "))
    }
    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")

        parent(common.tasks.named("publishGithub"))
    }

    dryRun = providers.environmentVariable("PUBLISH_DRY_RUN").isPresent
}