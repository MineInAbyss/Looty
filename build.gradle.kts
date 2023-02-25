import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val idofrontVersion: String by project
val gearyVersion: String by project

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlinx.serialization)
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.autoversion")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Other plugins
    compileOnly(lootyLibs.geary.papermc)
    compileOnly(gearyLibs.autoscan)

    // From Geary

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)

    // Shaded
    implementation(libs.bundles.idofront.core)
    implementation(libs.idofront.nms)
}

tasks {
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
