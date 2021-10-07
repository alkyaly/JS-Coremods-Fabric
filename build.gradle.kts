plugins {
    id("fabric-loom") version "0.10-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.3.0"
    `maven-publish`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))

val mcVersion: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val yarnVersion: String by project

val archivesBaseName: String by project
val modVersion: String by project
val group: String by project

repositories {
    mavenLocal()
    maven {
        name = "Parchment"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
    maven {
        url = uri("https://maven.saps.dev/minecraft")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    
    if (yarnVersion.isNotEmpty()) {
        mappings("net.fabricmc:yarn:$mcVersion+build.$yarnVersion:v2")
    } else {
        mappings(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-1.17.1:2021.08.23-nightly-SNAPSHOT")
        })
    }

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("org.openjdk.nashorn:nashorn-core:15.3")
}

tasks.processResources {
    inputs.property("version", modVersion)

    filesMatching("fabric.mod.json") {
        expand("version" to modVersion)
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.encoding = "UTF-8"
    options.release.set(16)
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16

    withSourcesJar()
}

tasks.jar {
    from("LICENSE")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifact(tasks.remapJar) {
                builtBy(tasks.remapJar)
            }
            artifact(tasks.getByName("sourcesJar")) {
                builtBy(tasks.remapSourcesJar)
            }
        }
    }
}
