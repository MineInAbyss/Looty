import com.mineinabyss.kotlinSpice
import com.mineinabyss.looty.Deps
import com.mineinabyss.miaSharedSetup
import com.mineinabyss.mineInAbyss
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version com.mineinabyss.looty.Deps.kotlinVersion
    kotlin("plugin.serialization") version com.mineinabyss.looty.Deps.kotlinVersion
    kotlin("kapt") version com.mineinabyss.looty.Deps.kotlinVersion
    id("com.mineinabyss.shared-gradle") version "0.0.3"
}

miaSharedSetup()

repositories {
    mavenCentral()
//    maven("https://hub.spigotmc.org/nexus/content/groups/public/") //Spigot
    maven("https://papermc.io/repo/repository/maven-public/") //Paper
    mineInAbyss()
    maven("https://jitpack.io")

    mavenLocal()
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:${Deps.serverVersion}")
//    compileOnly("org.spigotmc:spigot:${Deps.serverVersion}") // NMS
    compileOnly(kotlin("stdlib-jdk8"))

    kotlinSpice("${Deps.kotlinVersion}+")
    compileOnly("com.github.okkero:skedule")

    compileOnly("com.mineinabyss:geary-spigot:0.3.30")
    implementation("com.mineinabyss:idofront:0.5.9")
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
