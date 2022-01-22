import org.jetbrains.intellij.tasks.RunPluginVerifierTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("org.jetbrains.intellij") version "1.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
}

repositories {
    mavenCentral()
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }

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