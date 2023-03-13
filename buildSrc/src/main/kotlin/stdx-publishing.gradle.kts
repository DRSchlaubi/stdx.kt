import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.util.*

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

val dokkaJar by tasks.registering(Jar::class) {
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from(tasks.getByName("dokkaHtml"))
}

publishing {
    repositories {
        val repo = if ("SNAPSHOT" in version.toString()) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
        maven {
            setUrl(repo)
            credentials {
                username = System.getenv("SONATYPE_USER")
                password = System.getenv("SONATYPE_KEY")
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
            String(Base64.getDecoder().decode(signingKey.toByteArray())),
            signingPassword
        )

        publishing.publications.withType<MavenPublication> {
            sign(this)
        }
    }

}

tasks {
    task("publishPlatformPublications") {
        if (runMainCI) {
            dependsOn(publish)
        } else {
            afterEvaluate {
                val extension = extensions.findByName("kotlin")
                        as? KotlinMultiplatformExtension ?: return@afterEvaluate

                val enabledTargets = extension.run {
                    val hostManager = HostManager()
                    val macOsTargets = hostManager.enabledByHost[KonanTarget.MACOS_X64] ?: emptySet()
                    val linuxTargets = hostManager.enabledByHost[KonanTarget.LINUX_X64] ?: emptySet()

                    targets.asSequence()
                        .filterIsInstance<KotlinNativeTarget>()
                        .filter {
                            it.konanTarget !in linuxTargets && it.konanTarget in macOsTargets
                        }
                }

                val tasks = enabledTargets.map { "publish${it.publicationName()}PublicationToMavenRepository" }
                dependsOn(*tasks.filter { project.tasks.findByName(it) != null }.toList().toTypedArray())
            }
        }
    }
}

fun KotlinTarget.publicationName() = targetName.take(1).uppercase(Locale.getDefault()) + targetName.drop(1)
