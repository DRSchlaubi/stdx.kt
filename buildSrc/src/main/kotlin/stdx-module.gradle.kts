@file:OptIn(ExperimentalAbiValidation::class)

import gradle.kotlin.dsl.accessors._0f82fbdf64a8d81165830c231454130a.abiValidation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
}

kotlin {
    explicitApi()
    configureTargets()

    compilerOptions {
        optIn.addAll(
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.RequiresOptIn"
        )
    }

    abiValidation {
        enabled = true
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))

                if (project.name != "stdx-test-tools") {
                    implementation(project(":test-tools"))
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

dokka {
    dokkaSourceSets {
        configureEach {
            val file = projectDir.resolve("src/$name/kotlin")
            if (file.exists()) {
                sourceLink {
                    // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    localDirectory = file

                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl = uri("https://github.com/DRSchlaubi/stdx.kt/blob/main/${project.name}src/$name/kotlin")
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix = "#L"
                }
            }
        }

        val map = asMap

        if (map.containsKey("jvmMain")) {
            named("jvmMain") {
                jdkVersion = 8
                displayName = "JVM"
            }
        }
    }
}

tasks {
    withType<KotlinTest> {
        failOnNoDiscoveredTests = false
    }
}

kotlin {
    jvmToolchain(25)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

fun KotlinMultiplatformExtension.configureTargets() {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        testRuns.all {
            executionTask.configure { useJUnitPlatform() }
        }
    }
}
