val semVer: String? by rootProject
val kotlinLoggingVersion: String? by rootProject

// === IDE settings ===
val projectType: String by rootProject
val communityEdition: String by rootProject
val ultimateEdition: String by rootProject

val ideType: String by rootProject
val androidStudioPath: String? by rootProject

val ideaVersion: String? by rootProject
val pycharmVersion: String? by rootProject
val golandVersion: String? by rootProject

val javaIde: String? by rootProject
val pythonIde: String? by rootProject
val jsIde: String? by rootProject
val goIde: String? by rootProject

val ideVersion = when(ideType) {
    "PC", "PY" -> pycharmVersion
    "GO" -> golandVersion
    else -> ideaVersion
}

val pythonCommunityPluginVersion: String? by rootProject
val pythonUltimatePluginVersion: String? by rootProject
val goPluginVersion: String? by rootProject

// https://plugins.jetbrains.com/docs/intellij/android-studio.html#configuring-the-plugin-pluginxml-file
val ideTypeOrAndroidStudio = if (androidStudioPath == null) ideType else "IC"

project.tasks.asMap["runIde"]?.enabled = false
// === IDE settings ===

plugins {
    id("org.jetbrains.intellij") version "1.13.1"
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + listOf("-Xallow-result-return-type", "-Xsam-conversions=class")
            allWarningsAsErrors = false
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    compileTestJava {
        targetCompatibility = "17"
        sourceCompatibility = "17"
    }

    compileJava {
        targetCompatibility = "17"
        sourceCompatibility = "17"
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("232.*")
        version.set(semVer)
    }
}

val pythonTypesAPIHash: String by rootProject

dependencies {
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(project(":utbot-ui-commons"))

    //Family
    implementation(project(":utbot-python"))
    implementation("com.github.UnitTestBot:PythonTypesAPI:$pythonTypesAPIHash")
}

intellij {

    val androidPlugins = listOf("org.jetbrains.android")

    val jvmPlugins = mutableListOf(
        "java"
    )

    val kotlinPlugins = listOf(
        "org.jetbrains.kotlin"
    )

    androidStudioPath?.let { jvmPlugins += androidPlugins }

    val pythonCommunityPlugins = listOf(
        "PythonCore:${pythonCommunityPluginVersion}"
    )

    val pythonUltimatePlugins = listOf(
        "Pythonid:${pythonUltimatePluginVersion}"
    )

    val jsPlugins = listOf(
        "JavaScript"
    )

    val goPlugins = listOf(
        "org.jetbrains.plugins.go:${goPluginVersion}"
    )

    val mavenUtilsPlugins = listOf(
        "org.jetbrains.idea.maven"
    )

    val basePluginSet = jvmPlugins + kotlinPlugins + mavenUtilsPlugins + androidPlugins

    plugins.set(
        when (projectType) {
            communityEdition -> basePluginSet + pythonCommunityPlugins
            ultimateEdition -> when (ideType) {
                "IC" -> basePluginSet + pythonCommunityPlugins
                "IU" -> basePluginSet + pythonUltimatePlugins + jsPlugins + goPlugins
                "PC" -> pythonCommunityPlugins
                "PY" -> pythonUltimatePlugins + jsPlugins
                "GO" -> goPlugins
                else -> basePluginSet
            }
            else -> basePluginSet
        }
    )

    version.set(ideVersion)
    type.set(ideType)
}