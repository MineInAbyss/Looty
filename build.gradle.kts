import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Other plugins
    compileOnly("com.mineinabyss:geary-platform-papermc:0.6.49")
    compileOnly("com.mineinabyss:geary-commons-papermc:0.1.2")

    // From Geary
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    compileOnly("com.github.okkero:skedule")

    // Shaded
    implementation("com.mineinabyss:idofront:1.17.1-0.6.23")
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
            freeCompilerArgs = listOf("-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
