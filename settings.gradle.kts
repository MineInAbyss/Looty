pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
    }

    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("kapt") version kotlinVersion
    }
}

rootProject.name = "looty"

include(
    "looty-mixins",
)

project(":looty-mixins").projectDir = file("./mixins")
