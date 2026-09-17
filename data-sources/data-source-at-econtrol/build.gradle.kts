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
    implementation(project(":transport"))

    implementation(libs.spring.context)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.restclient)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.springdoc.openapi.starter.webmvc.api)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

val econtrolOpenApi = layout.projectDirectory.file("src/main/resources/openapi/e-control-models.json")
val econtrolGeneratedDir = layout.buildDirectory.dir("generated/econtrol")

tasks.register<GenerateTask>("openApiGenerateEcontrol") {
    group = "code generation"
    description = "Generates the E-Control API models."

    generatorName.set("spring")

    inputSpec.set(econtrolOpenApi.asFile.absolutePath)
    outputDir.set(econtrolGeneratedDir.get().asFile.absolutePath)

    cleanupOutput.set(true)

    modelPackage.set("energy.eddie.datasource.at.econtrol")

    generateModelTests.set(false)
    generateModelDocumentation.set(false)

    openapiNormalizer.set(
        mapOf(
            "REF_AS_PARENT_IN_ALLOF" to "true"
        )
    )

    configOptions.set(
        mapOf(
            "library" to "spring-boot",
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "hideGenerationTimestamp" to "true",
            "useSpringBoot4" to "true",
            "useJackson3" to "true",
            "useBeanValidation" to "true",
            "performBeanValidation" to "true",
            "useInstanceOfEqualsInEqualsMethod" to "true"
        )
    )

    globalProperties.set(
        mapOf(
            "models" to ""
        )
    )
}

sourceSets {
    main {
        java {
            srcDir(econtrolGeneratedDir.map { it.dir("src/main/java") })
        }
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerateEcontrol"))
}