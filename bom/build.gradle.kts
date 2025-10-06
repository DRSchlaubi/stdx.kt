plugins {
    `java-platform`
    `stdx-publishing`
}

val me = project
rootProject.subprojects {
    if (name !in groupProjects) {
        me.evaluationDependsOn(path)
    }
}

dependencies {
    constraints {
        afterEvaluate {
            rootProject.subprojects.forEach { subproject ->
                if (subproject.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                    api(subproject)
                }
            }
        }
    }
}
