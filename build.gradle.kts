import org.jetbrains.intellij.tasks.RunPluginVerifierTask

fun properties(key: String) = project.findProject(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("org.jetbrains.intellij") version "1.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

intellij {
    pluginName.set(properties("pluginName"))
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
}

tasks {
    runPluginVerifier {
        ideVersions.set(
            properties("pluginVerifierIdeVersions")
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        )

        failureLevel.set(listOf(
            RunPluginVerifierTask.FailureLevel.COMPATIBILITY_PROBLEMS,
            RunPluginVerifierTask.FailureLevel.INVALID_PLUGIN
        ))
    }
}