plugins {
    kotlin("multiplatform")
}


afterEvaluate {
    kotlin {
        sourceSets {
            listOf("common", "jvm", "js", "native").forEach { platform ->
                val path = "${platform}Main"
                val dir = projectDir.resolve("src").resolve("${platform}Generated")
                kotlin.sourceSets.findByName(path)?.apply {
                    kotlin.srcDir(dir)
                }
            }
        }
    }
}

tasks {
    task("generateElements")
}
