@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `stdx-module`
    `stdx-publishing`
    `all-platforms`
}

kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("concurrentNonJvm") {
                withNative()
                withWasmJs()
                withWasmWasi()
            }
        }
    }

    compilerOptions {
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))
                api("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
            }
        }

        commonTest {
            dependencies {
                api("org.jetbrains.kotlinx", "kotlinx-coroutines-test")
            }
        }
    }
}
