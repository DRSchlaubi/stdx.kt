plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
    `stdx-generated-elements`
}

kotlin {
    fullJs()

    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
    }
}

tasks {
    val generateContextFunctions = task<GenerateContextFunctionsTask>("generateContextFunctions") {
        `package`.set("dev.schlaubi.stdx.core")
    }

    generateElements {
        dependsOn(generateContextFunctions)
    }
}
