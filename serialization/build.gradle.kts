plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.3.2")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.2")
                implementation("org.jetbrains.kotlinx", "kotlinx-serialization-protobuf", "1.3.2")
                implementation("com.github.jershell", "kbson", "0.4.4")
            }
        }
    }
}
