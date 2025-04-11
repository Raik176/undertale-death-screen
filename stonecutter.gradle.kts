plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.7.4" apply false
}
stonecutter active "1.20.6" /* [SC] DO NOT EDIT */

for (it in listOf("Mods", "Github", "Modrinth", "Curseforge")) {
    stonecutter registerChiseled tasks.register("chiseledPublish$it", stonecutter.chiseled) {
        group = "publishing"
        ofTask("publish$it")
    }
}

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "build"
    ofTask("buildAndCollect")
}
for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "build"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }
}

stonecutter registerChiseled tasks.register("chiseledClean", stonecutter.chiseled) {
    group = "build"
    ofTask("clean")
}
for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledClean$loader", stonecutter.chiseled) {
        group = "build"
        versions { branch, _ -> branch == it.id }
        ofTask("clean")
    }
}

for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) it.project.tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("run$type")
    }
}
