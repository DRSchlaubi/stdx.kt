plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `all-platforms`
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.5.0"))
                api("org.jetbrains.kotlinx", "kotlinx-serialization-core")
                api("org.jetbrains.kotlinx", "kotlinx-serialization-json")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json")
                implementation("org.jetbrains.kotlinx", "kotlinx-serialization-protobuf")
            }
        }

        findByName("jvmTest")?.apply {
            dependencies {
                implementation("com.github.jershell", "kbson", "0.5.0")
            }
        }
    }
}
