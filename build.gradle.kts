plugins {
    id("org.jetbrains.dokka")
    id("io.codearte.nexus-staging") version "0.30.0"
}


allprojects {
    version = "1.0.0"
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

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    packageGroup = "dev.schlaubi"
    username = System.getenv("SONATYPE_USER")
    password = System.getenv("SONATYPE_KEY")
}
