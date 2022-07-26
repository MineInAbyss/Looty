import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val idofrontVersion: String by project
val gearyVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Other plugins
    compileOnly(lootylibs.geary.papermc.core)

    // From Geary

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)

    // Shaded
    implementation(libs.idofront.core)
    implementation(libs.idofront.nms)
}

tasks {
    shadowJar {
        archiveBaseName.set("Looty")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlin.ExperimentalUnsignedTypes",
                "-opt-in=com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL",
                "-opt-in=kotlin.RequiresOptIn",
            )
        }
    }
}
