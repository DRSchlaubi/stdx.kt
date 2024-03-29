plugins {
    `full-js`
    `desktop-X86`
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `stdx-generated-elements`
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api("io.github.microutils", "kotlin-logging", "3.0.4")
            }
        }
        findByName("jvmTest")?.apply {
            dependencies {
                implementation("org.slf4j", "slf4j-simple", "2.0.6")
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
