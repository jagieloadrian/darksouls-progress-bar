package project.WebSocketEcho

val ktor_version = "2.3.12"

plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "2.3.12"
}

group = "com.gft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("${rootProject.name}.jar")
    }
}