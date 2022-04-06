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
        if (subproject.plugins.hasPlugin("maven-publish") && subproject.name !in groupProjects) {
            api("dev.schlaubi", subproject.name, subproject.version.toString())
        }
    }
}

if(runMainCI) {
    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
