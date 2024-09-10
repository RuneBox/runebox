plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "runebox"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":logger")
include(":common")
include(":launcher")