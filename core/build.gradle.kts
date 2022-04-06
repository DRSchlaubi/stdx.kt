plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `stdx-generated-elements`
}

kotlin {
    fullJs()
}

tasks {
    val generateContextFunctions = task<GenerateContextFunctionsTask>("generateContextFunctions") {
        `package`.set("dev.schlaubi.stdx.core")
        // wait for Kotlin 1.7.0 or stabilization of the feature
        enabled = false
    }

    generateElements {
        dependsOn(generateContextFunctions)
    }
}
