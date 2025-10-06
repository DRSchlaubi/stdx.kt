import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `full-js`
    `desktop-X86`
    `stdx-module`
    `stdx-publishing`
    `stdx-generated-elements`
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api("io.github.oshai", "kotlin-logging", "7.0.13")
            }
        }
        findByName("jvmTest")?.apply {
            dependencies {
                implementation("org.slf4j", "slf4j-simple", "2.0.17")
            }
        }
    }
}

tasks {
    val generateLoggerFunctions = register<GenerateLoggerFunctionsTask>("generateLoggerFunctions") {
        `package` = "dev.schlaubi.stdx.logging"
        logLevels.addAll("debug", "trace", "error", "info", "warn")
    }

    generateElements {
        dependsOn(generateLoggerFunctions)
    }
}
