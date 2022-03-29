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
            subproject.publishing.publications.withType<MavenPublication> {
                if (!artifactId.endsWith("-metadata") &&
                    !artifactId.endsWith("-kotlinMultiplatform")
                ) {
                    api(groupId, artifactId, version)
                }
            }
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
