plugins {
    id("org.jetbrains.dokka")
}


allprojects {
    version = "2.0.0"
    group = "dev.schlaubi"
}

dependencies {
    dokka(projects.core)
    dokka(projects.coroutines)
    dokka(projects.envconf)
    dokka(projects.serialization)
}
