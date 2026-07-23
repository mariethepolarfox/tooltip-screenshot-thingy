import net.fabricmc.loom.task.ValidateAccessWidenerTask

plugins {
    java
    idea
    kotlin("jvm") version "2.4.0"
    alias(libs.plugins.loom)
    `versioned-catalogues`
}

repositories {
    maven("https://maven.teamresourceful.com/repository/maven-public/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.terraformersmc.com/releases/")
}

java {
    withSourcesJar()
}

kotlin { jvmToolchain(25) }


dependencies {
    minecraft(versionedCatalog["minecraft"])

    implementation(libs.fabricLoader)
    implementation(versionedCatalog["fabricApi"])
    implementation(libs.fabricKt)

    implementation(libs.devauth)

    implementation(versionedCatalog["resourcefulconfig"])
    implementation(libs.resourcefulconfigkt)
    include(libs.resourcefulconfigkt)

    implementation(versionedCatalog["modmenu"])

    compileOnly(libs.objc)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/toolshot.accesswidener")

    runConfigs["client"].apply {
        generateRunConfig = true
        runDirectory = project.file("../../run")
        jvmArguments.add("-Dfabric.modsFolder=" + '"' + rootProject.projectDir.resolve("run/${stonecutter.current.version.replace(".", "")}Mods").absolutePath + '"')
        systemProperties.put("devauth.configDir", rootProject.file(".devauth").absolutePath)
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        inputs.property("minecraft_version", versionedCatalog.versions["minecraft"])
        inputs.property("loader_version", libs.versions.fabricLoader.get())

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "loader_version" to libs.versions.fabricLoader.get(),
                "minecraft_version" to versionedCatalog.versions["minecraft"],
            )
        }
    }

    jar {
        from("LICENSE")
    }

    build {
        doLast {
            val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${stonecutter.current.version}-$version.jar")
            val targetFile = rootProject.projectDir.resolve("build/libs/Toolshot-$version-${stonecutter.current.version}.jar")
            targetFile.parentFile.mkdirs()
            targetFile.writeBytes(sourceFile.readBytes())
        }
    }
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

idea {
    module {
        excludeDirs.add(file("run"))
    }
}