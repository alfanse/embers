plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.0"
    id("io.spring.dependency-management") version "1.1.3"
}

description = "Example project showing how to integrate embers with http4k"

dependencies {
    implementation(project(":embers-services"))
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // http4k
    val http4kVersion = "5.4.0.0"
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    
    // Database
    implementation("com.h2database:h2:2.2.220")
    implementation("org.jdbi:jdbi3-core:3.41.3")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
    testImplementation("org.testcontainers:postgresql:1.18.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
