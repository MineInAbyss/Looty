import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val idofrontVersion: String by project
val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Other plugins
    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")

    // From Geary

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.skedule)

    // Shaded
   implementation("com.mineinabyss:idofront:$idofrontVersion")
}

tasks {
    shadowJar {
        archiveBaseName.set("Looty")
//        relocate("com.derongan.minecraft.guiy", "${project.group}.${project.name}.guiy".toLowerCase())
//        relocate("com.mineinabyss.idofront", "${project.group}.${project.name}.idofront".toLowerCase())
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL",
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers"
            )
        }
    }
}
