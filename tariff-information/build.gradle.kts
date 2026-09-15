plugins {
    id("java")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "energy.eddie"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":transport"))
    implementation(project(":data-sources:data-source-at-econtrol"))
    implementation(project(":interactions"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.hateoas)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}