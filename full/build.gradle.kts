plugins {
    `java-library`
    `stdx-publishing`
}

val me = project
rootProject.subprojects {
    if (name !in groupProjects) {
        me.evaluationDependsOn(path)
    }
}

dependencies {
    rootProject.subprojects.forEach { subproject ->
        if (subproject.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            api(subproject)
        }
        }
}
