plugins {
    id("org.jetbrains.dokka")
}


allprojects {
    version = "1.3.0"
    group = "dev.schlaubi"
    repositories {
        mavenCentral()
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(rootProject.file("docs"))
    }
}
