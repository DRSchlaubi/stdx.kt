import org.gradle.jvm.toolchain.internal.DefaultToolchainSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.net.URL

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

kotlin {
    explicitApi()
    configureTargets()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                if (project.name != "stdx-test-tools") {
                    implementation(project(":stdx-test-tools"))
                }
            }
        }

        findByName("jvmTest")?.apply {
            dependencies {
                implementation(kotlin("test-junit5"))
                runtimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.2")
            }
        }

        findByName("jsTest")?.apply {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks {
    withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
        dokkaSourceSets {
            configureEach {
                val file = projectDir.resolve("src/$name/kotlin")
                if (file.exists()) {
                    sourceLink {
                        // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                        localDirectory.set(file)

                        // URL showing where the source code can be accessed through the web browser
                        remoteUrl.set(
                            URL(
                                "https://github.com/DRSchlaubi/stdx.kt/blob/main/${project.name}src/$name/kotlin"
                            )
                        )
                        // Suffix which is used to append the line number to the URL. Use #L for GitHub
                        remoteLineSuffix.set("#L")
                    }
                }
            }

            val map = asMap

            if (map.containsKey("jsMain")) {
                named("jsMain") {
                    displayName.set("JS")
                }
            }

            if (map.containsKey("jvmMain")) {
                named("jvmMain") {
                    jdkVersion.set(8)
                    displayName.set("JVM")
                }
            }

            if (map.containsKey("nativeMain")) {
                named("nativeMain") {
                    displayName.set("Native")
                }
            }
        }
    }
}

kotlin {
    jvmToolchain {
        (this as DefaultToolchainSpec).languageVersion.set(JavaLanguageVersion.of(19))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

fun KotlinMultiplatformExtension.configureTargets() {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
}
