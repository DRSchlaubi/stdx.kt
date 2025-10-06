plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "2.2.20"))
    implementation(kotlin("serialization", version = "2.2.20"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "2.1.0-Beta")
    implementation("com.squareup", "kotlinpoet", "2.2.0")
    implementation("com.vanniktech", "gradle-maven-publish-plugin", "0.34.0")
    implementation(gradleApi())
    implementation(localGroovy())
}
