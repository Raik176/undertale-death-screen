import org.gradle.kotlin.dsl.version

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin") version "0.7.4"
}

val minecraft = stonecutter.current.version

extra["githubRepo"] = "CHANGE/THIS"
extra["modrinthId"] = "N/A"
extra["curseforgeId"] = "N/A"

version = "${mod.version}+$minecraft"
group = "${mod.group}.common"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.prop("loom.platform")
})

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

publishMods {
    changelog = providers.fileContents(layout.projectDirectory.file("../../CHANGELOG.md")).asText.get()
    type = STABLE
    displayName = "${mod.version} for $minecraft"

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = extra["githubRepo"].toString()
        commitish = "master"
        tagName = "v${mod.version}"

        allowEmptyFiles = true
    }

    dryRun = providers.environmentVariable("PUBLISH_DRY_RUN").isPresent
}