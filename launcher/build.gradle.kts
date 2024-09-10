import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "8.3.0"
    kotlin("plugin.serialization") version "2.0.20"
}

repositories.apply {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(projects.logger)
    implementation(projects.common)
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:_")
    implementation("no.tornado:tornadofx-controls:_")
    implementation("de.jensd:fontawesomefx:_")
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("me.friwi:jcefmaven:_")
    implementation("com.squareup.okhttp3:okhttp:_")
    implementation("com.nimbusds:oauth2-oidc-sdk:_")
    implementation("org.apache.commons:commons-compress:_")
    //implementation("net.raumzeitfalle.fx:scenic-view:11.0.2")
}

javafx {
    version = "11"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.swing", "javafx.web", "javafx.base", "javafx.media", "javafx.fxml")
}


application {
    mainClass.set("org.runebox.launcher.Launcher")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

tasks.shadowJar {
    minimize()
}