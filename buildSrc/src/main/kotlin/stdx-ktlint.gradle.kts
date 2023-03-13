plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    filter {
        exclude { element ->
            val path = element.file.absolutePath
            path.contains("build") || path.contains("generated") || element.isDirectory
        }
    }
}
