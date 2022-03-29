plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
}

afterEvaluate {
    listOf("common", "jvm", "js", "native").forEach { platform ->
        val path = "${platform}Main"
        val dir = buildDir.resolve("generated").resolve("src").resolve(path)
        kotlin.sourceSets.findByName(path)?.apply {
            kotlin.srcDir(dir)
        }
    }
}


kotlin {
    fullJs()
    desktopOSX86()

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

    afterEvaluate {
        "metadataCommonMainClasses"{
            dependsOn(generateLoggerFunctions)
        }
        findByName("compileKotlinJvm")?.apply {
            dependsOn(generateLoggerFunctions)
        }
    }
}
