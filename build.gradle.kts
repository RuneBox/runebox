plugins {
    kotlin("jvm") apply false
    `java-library`
}

allprojects {
    group = "org.runebox"
    version = "0.1.0"

    repositories {
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
            vendor.set(JvmVendorSpec.ADOPTOPENJDK)
        }
    }
}