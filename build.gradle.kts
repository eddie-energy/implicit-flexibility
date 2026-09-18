import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    alias(libs.plugins.errorprone) apply false
}

subprojects {
    pluginManager.withPlugin("java") {
        pluginManager.apply("net.ltgt.errorprone")

        val catalog = rootProject.libs

        dependencies {
            add("errorprone", catalog.errorprone.core)
            add("errorprone", catalog.nullaway)
            add("compileOnly", catalog.jspecify)
        }

        tasks.withType<JavaCompile>().configureEach {
            options.errorprone {
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
}
