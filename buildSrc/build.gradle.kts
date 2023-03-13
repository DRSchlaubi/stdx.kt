plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.8.10"))
    implementation(kotlin("serialization", version = "1.8.10"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.8.10")
    implementation("org.jlleitschuh.gradle", "ktlint-gradle", "11.3.1")
    implementation("com.squareup", "kotlinpoet", "1.12.0")
    implementation("org.jetbrains.kotlinx", "binary-compatibility-validator", "0.13.0")
    implementation(gradleApi())
    implementation(localGroovy())
}
