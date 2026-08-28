plugins {
    id("java-library")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "energy.eddie"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.spring.context)
    api(libs.jackson.databind)

    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.spring.boot.starter.web)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}