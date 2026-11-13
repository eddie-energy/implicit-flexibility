rootProject.name = "implicit-flexibility"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("interactions")
include("data-sources:data-source-at-econtrol")
include("transport")
include("tariff-information")
include("data-sources:data-source-es-esios")