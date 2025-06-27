plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.0"
}

description = "Example project showing how to integrate embers with http4k"

dependencies {
    implementation(project(":embers-services"))
    implementation(project(":embers-acceptance-tests"))
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // http4k
    val http4kVersion = "6.15.0.1" // Latest http4k version
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    
    // Jakarta REST API for response conversion
    implementation(libs.jakarta.ws.rs)
    implementation(libs.bundles.jersey)

    // Testing
    testImplementation(libs.bundles.unit.tests)
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
