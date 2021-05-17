import com.mineinabyss.mineInAbyss
import com.mineinabyss.sharedSetup

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("com.mineinabyss.shared-gradle")
}

sharedSetup {
    applyJavaDefaults()
}

val serverVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://papermc.io/repo/repository/maven-public/") //Paper
    mineInAbyss()
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:$serverVersion")
    compileOnly("com.destroystokyo.paper:paper:$serverVersion")
    implementation("space.vectrix.ignite:ignite-api:0.4.0")
    compileOnly("com.mineinabyss:geary-spigot:0.4.42")

}

tasks {
    val ignite_mods_path: String by project

    shadowJar {
        relocate("kotlin", "${project.group}.${project.name}.kotlin".toLowerCase())

        minimize()
    }

    register("copyModsJar", Copy::class.java) {
        from(shadowJar)
        into(ignite_mods_path)
        doLast {
            println("Copied to mods directory $ignite_mods_path")
        }
    }

    build {
        dependsOn(shadowJar, "copyModsJar")
    }
}
