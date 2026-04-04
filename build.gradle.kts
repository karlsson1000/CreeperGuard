plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "org.karlssonsmp"
version = "1.5"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.1.build.+")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.shadowJar {
    archiveClassifier.set("")
    minimize()
    relocate("org.bstats", "org.karlssonsmp.creeperguard.bstats")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}