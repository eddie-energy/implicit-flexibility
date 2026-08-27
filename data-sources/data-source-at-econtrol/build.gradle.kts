import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("java")

    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "energy.eddie"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":interactions"))

    implementation(libs.spring.context)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jackson.databind)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.hateoas)

    implementation(libs.google.gson)
    implementation(libs.squareup.okio)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging)
    implementation(libs.gson.fire)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

/*
 * -------------------------------------------------------------------------
 * OpenAPI model generation
 * -------------------------------------------------------------------------
 */

val econtrolOpenApi =
    layout.projectDirectory.file("src/main/resources/openapi/e-control-models.json")

val econtrolGeneratedDir =
    layout.buildDirectory.dir("generated/econtrol")

tasks.register<GenerateTask>("openApiGenerateEcontrol") {
    group = "code generation"
    description = "Generates the OpenAPI client stubs for the E-Control API."

    generatorName.set("java")

    inputSpec.set(econtrolOpenApi.asFile.absolutePath)
    outputDir.set(econtrolGeneratedDir.get().asFile.absolutePath)

    modelPackage.set("energy.eddie.datasource.at.econtrol")

    generateModelTests.set(false)
    generateModelDocumentation.set(false)

    generateApiTests.set(false)
    generateApiDocumentation.set(false)

    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "hideGenerationTimestamp" to "true"
        )
    )

    globalProperties.set(
        mapOf(
            "models" to "",
            "apis" to "",
            "supportingFiles" to ""
        )
    )
}

/*
 * -------------------------------------------------------------------------
 * Generated sources
 * -------------------------------------------------------------------------
 */

sourceSets {
    main {
        java {
            srcDir(econtrolGeneratedDir.map { it.dir("src/main/java") })
        }
    }
}

/*
 * -------------------------------------------------------------------------
 * Make Java compilation depend on OpenAPI generation
 * -------------------------------------------------------------------------
 */

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerateEcontrol"))
}