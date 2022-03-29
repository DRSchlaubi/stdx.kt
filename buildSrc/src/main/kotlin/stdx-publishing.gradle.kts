plugins {
    `maven-publish`
    signing
}

val dokkaJar by tasks.registering(Jar::class) {
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from(tasks.getByName("dokkaHtml"))
}

publishing {
    repositories {
        listOf(
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/",
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        ).forEach {
            maven {
                setUrl(it)
                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_KEY")
                }
            }
        }
    }

    publications {
        withType<MavenPublication> {
            artifact(dokkaJar)
            pom {
                name.set(project.name)
                description.set("Kotlin Standard Library Extensions")
                url.set("https://github.com/DRSchlaubi/stdx.kt")

                licenses {
                    license {
                        name.set("Apache-2.0 License")
                        url.set("https://github.com/DRSchlaubi/stdx.kt/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        name.set("Michael Rittmeister")
                        email.set("mail@schlaubi.me")
                        organizationUrl.set("https://schlau.bi")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/DRSchlaubi/stdx.kt.git")
                    developerConnection.set("scm:git:https://github.com/DRSchlaubi/stdx.kt.git")
                    url.set("https://github.com/DRSchlaubi/stdx.kt")
                }
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(
            String(java.util.Base64.getDecoder().decode(signingKey.toByteArray())),
            signingPassword
        )
    }

    publishing.publications.withType<MavenPublication> {
        sign(this)
    }
}
