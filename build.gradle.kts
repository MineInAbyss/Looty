import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.nms)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.publication)
    alias(idofrontLibs.plugins.mia.autoversion)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

repositories {
    maven("https://repo.mineinabyss.com/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    mavenLocal()
}

dependencies {
    // Other plugins
    compileOnly(libs.geary.papermc)

    // From Geary

    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    compileOnly(idofrontLibs.minecraft.mccoroutine)
    compileOnly(idofrontLibs.bundles.idofront.core)
    compileOnly(idofrontLibs.idofront.nms)
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
