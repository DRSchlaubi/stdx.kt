plugins {
    `full-js`
    `desktop-X86`
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `stdx-generated-elements`
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("io.github.microutils", "kotlin-logging", "2.1.21")
            }
        }
    }
}

tasks {
    val generateLoggerFunctions = task<GenerateLoggerFunctionsTask>("generateLoggerFunctions") {
        `package`.set("dev.schlaubi.stdx.logging")
        logLevels.set(listOf("debug", "trace", "error", "info", "warn"))
    }

    generateElements {
        dependsOn(generateLoggerFunctions)
    }
}
