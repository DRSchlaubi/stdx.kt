plugins {
    `java-library`
    `stdx-publishing`
}

val me = project
rootProject.subprojects {
    if (name != me.name && name != "bom") {
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
