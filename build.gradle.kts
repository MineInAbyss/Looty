import com.mineinabyss.kotlinSpice
import com.mineinabyss.mineInAbyss
import com.mineinabyss.sharedSetup
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("com.mineinabyss.shared-gradle") version "0.0.6"
}

sharedSetup()

repositories {
    mavenCentral()
//    maven("https://hub.spigotmc.org/nexus/content/groups/public/") //Spigot
    maven("https://papermc.io/repo/repository/maven-public/") //Paper
    mineInAbyss()
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://jitpack.io")

    mavenLocal()
}

val kotlinVersion: String by project
val serverVersion: String by project

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:$serverVersion")
    compileOnly("com.destroystokyo.paper:paper:$serverVersion")
//    compileOnly("org.spigotmc:spigot:${Deps.serverVersion}") // NMS
    compileOnly(kotlin("stdlib-jdk8"))

    kotlinSpice("$kotlinVersion+")
    compileOnly("com.github.okkero:skedule")

    compileOnly("com.mineinabyss:geary-spigot:0.5.45")
    implementation("com.mineinabyss:idofront:0.6.14")
}

tasks {
    shadowJar {
        archiveBaseName.set("Looty")

        relocate("com.derongan.minecraft.guiy", "${project.group}.${project.name}.guiy".toLowerCase())
        relocate("com.mineinabyss.idofront", "${project.group}.${project.name}.idofront".toLowerCase())

        minimize {
            exclude(dependency("de.erethon:headlib:3.0.2"))
            exclude(dependency("com.github.WesJD.AnvilGUI:anvilgui:5e3ab1f721"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}

publishing {
    mineInAbyss(project) {
        from(components["java"])
    }
}
