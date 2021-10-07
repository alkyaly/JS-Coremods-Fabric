plugins {
    id("fabric-loom") version "0.10-SNAPSHOT"
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

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")

    mappings(loom.officialMojangMappings())

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
