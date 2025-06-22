plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.21"
    id("io.spring.dependency-management") version "1.1.3"
}

description = "Example project showing how to integrate embers with http4k"

dependencies {
    implementation(project(":embers-services"))
    implementation(project(":embers-acceptance-tests"))
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // http4k
    val http4kVersion = "5.10.2.0" // Updated to include security fixes for Netty vulnerabilities
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    
    // Database
    implementation("com.h2database:h2:2.2.220")
    implementation("org.jdbi:jdbi3-core:3.41.3")
    implementation("org.apache.commons:commons-compress:1.25.0")
    
    // Jakarta REST API for response conversion
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("org.glassfish.jersey.core:jersey-common:3.1.2")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
}

tasks.test {
    useJUnitPlatform()
    // Required for reflection access in tests
    jvmArgs = listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}

kotlin {
    jvmToolchain(21)
}
