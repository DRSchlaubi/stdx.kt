import com.vanniktech.maven.publish.JavaPlatform
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    id("com.vanniktech.maven.publish.base")
    id("org.jetbrains.dokka")
}

mavenPublishing {
    if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = true))
    } else if (plugins.hasPlugin("java-platform")) {
        configure(JavaPlatform())
    }

    coordinates("dev.schlaubi", "stdx-${project.name}", project.version.toString())
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    pom {
        name = project.name
        description = "Kotlin Standard Library Extensions"
        url = "https://github.com/DRSchlaubi/stdx.kt"

        licenses {
            license {
                name = "Apache-2.0 License"
                url = "https://github.com/DRSchlaubi/stdx.kt/blob/main/LICENSE"
            }
        }

        developers {
            developer {
                name = "Michael Rittmeister"
                email = "mail@schlaubi.me"
                organizationUrl = "https://schlau.bi"
            }
        }

        scm {
            connection = "scm:git:https://github.com/DRSchlaubi/stdx.kt.git"
            developerConnection = "scm:git:https://github.com/DRSchlaubi/stdx.kt.git"
            url = "https://github.com/DRSchlaubi/stdx.kt"
        }
    }
}
