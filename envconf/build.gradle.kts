import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    nodejs
    `desktop-all`
    `stdx-module`
    `stdx-publishing`
}

val testEnv = mapOf("HELLO" to "HELLO", "PREFIX_HELLO" to "HELLO")

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-node:18.14.6-pre.510")
            }
        }
    }
}

tasks {
    withType<Test> {
        environment(testEnv)
    }

    withType<KotlinJsTest> {
        environment = testEnv.toMutableMap()
    }

    withType<KotlinNativeTest> {
        environment = testEnv
    }

    withType<KotlinNativeSimulatorTest> {
        enabled = false
    }
}
