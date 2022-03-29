plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.6.10"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.6.10")
    implementation("org.jlleitschuh.gradle", "ktlint-gradle", "10.2.1")
    implementation("com.squareup", "kotlinpoet", "1.11.0")
    implementation("org.jetbrains.kotlinx", "binary-compatibility-validator", "0.8.0")
    implementation(gradleApi())
    implementation(localGroovy())
}
