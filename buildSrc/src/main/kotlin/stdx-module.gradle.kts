import org.gradle.jvm.toolchain.internal.DefaultToolchainSpec
import java.net.URL

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(BOTH) {
        browser()
        nodejs()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test-junit5"))
                runtimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.2")
            }
        }

        getByName("jsTest") {
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
                sourceLink {
                    // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    localDirectory.set(projectDir.resolve("src/main/kotlin"))

                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(
                        URL(
                            "https://github.com/DRSchlaubi/stdx.kt/blob/main/${project.name}src/main/kotlin"
                        )
                    )
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix.set("#L")
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
        (this as DefaultToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
