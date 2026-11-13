import com.github.gradle.node.pnpm.task.PnpmTask
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
    alias(libs.plugins.errorprone) apply false
    alias(libs.plugins.sonarqube)
    jacoco
}

subprojects {
    pluginManager.withPlugin("java") {
        pluginManager.apply("net.ltgt.errorprone")
        pluginManager.apply("jacoco")

        val catalog = rootProject.libs

        dependencies {
            add("errorprone", catalog.errorprone.core)
            add("errorprone", catalog.nullaway)
            add("compileOnly", catalog.jspecify)
        }

        tasks.withType<JavaCompile>().configureEach {
            options.errorprone {
                excludedPaths.set(".*/build/generated/.*")
                check("NullAway", CheckSeverity.ERROR)
                option(
                    "NullAway:AnnotatedPackages",
                    "energy.eddie.implicitflexibility"
                )
                option(
                    "NullAway:JSpecifyMode",
                    "true"
                )
            }
        }
    }

    val subproject = this
    subproject.plugins.withType<JacocoPlugin>().configureEach {
        subproject.tasks.withType<Test>().configureEach {
            rootProject.tasks.named("codeCoverageReport") {
                mustRunAfter(this@configureEach)
            }
        }
    }

    pluginManager.withPlugin("org.springframework.boot") {
        tasks.matching { it.name == "bootJar" }.configureEach {
            enabled = false
        }
        tasks.matching { it.name == "jar" }.configureEach {
            enabled = true
        }
    }
}

tasks.register<PnpmTask>("pnpmBuildImplicitFlexibilityDocs") {
    group = "documentation"
    description = "builds the implicit flexibility documentation"
    dependsOn("pnpmInstall")
    pnpmCommand.set(listOf("run", "docs:build"))
}

sonar {
    properties {
        property("sonar.projectName", "implicit-flexibility")
        property("sonar.projectKey", "eddie-energy_implicit-flexibility")
        property("sonar.organization", "eddie-energy")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.rootDir}/build/reports/jacoco/codeCoverageReport/codeCoverageReport.xml"
        )
    }
}

tasks.register<JacocoReport>("codeCoverageReport") {
    description = "Generates a jacoco report for all subprojects."
    group = "verification"

    dependsOn(subprojects.map { subproject ->
        subproject.tasks.withType<Test>()
    })

    subprojects {
        val subproject = this

        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks.matching {
                it.extensions.findByType<JacocoTaskExtension>() != null
            }.configureEach {
                sourceSets(
                    subproject.the<SourceSetContainer>()
                        .named("main")
                        .get()
                )
                executionData(this)
            }
        }
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

project(":tariff-information") {
    pluginManager.withPlugin("org.springframework.boot") {
        tasks.matching { it.name == "bootJar" }.configureEach {
            enabled = true
        }
        tasks.matching { it.name == "jar" }.configureEach {
            enabled = false
        }
    }
}
