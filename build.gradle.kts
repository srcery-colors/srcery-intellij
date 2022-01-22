import org.jetbrains.changelog.date
import org.jetbrains.intellij.tasks.RunPluginVerifierTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("org.jetbrains.intellij") version "1.0"
    id("org.jetbrains.changelog") version "1.3.1"
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

changelog {
    version.set(properties("pluginVersion"))
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    groups.set(listOf("Added", "Changed"))
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
        pluginDescription.set("""
            |<p><img src="https://raw.githubusercontent.com/nils-degroot/srcery-intellij/master/docs/logo.png" width="600" /></p>
            |<p>Colorscheme with clearly defined contrasting colors and a slightly earthy tone.</p>
            |<p>Based on <a href="https://srcery-colors.github.io/">https://srcery-colors.github.io/</a></p>
            |<h2>Installation</h2>
            |<p>Installation can be done via
            |the <a href="https://plugins.jetbrains.com/plugin/18428-srcery-colorscheme/">JetBrains plugin manager</a></p>
            |<h2>Screenshots</h2>
            |<p><img src="https://raw.githubusercontent.com/nils-degroot/srcery-intellij/master/docs/screenshot.png" width="600" /></p>
        """.trimMargin())
        changeNotes.set(provider { changelog.getLatest().toHTML() })
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

    publishPlugin {
        token.set(System.getenv("SRCERY_INTELLIJ_TOKEN"))
    }
}