plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `all-platforms`
}

kotlin {

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }

        commonMain {
            dependencies {
                api(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
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
