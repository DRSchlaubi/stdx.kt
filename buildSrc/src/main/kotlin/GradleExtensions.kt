import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

private fun notation(group: String, name: String, version: String? = null) =
    "$group:$name${version?.let { ":$version" } ?: ""}"

fun KotlinDependencyHandler.api(group: String, name: String, version: String? = null) =
    api(notation(group, name, version))

fun KotlinDependencyHandler.compileOnly(group: String, name: String, version: String? = null) =
    compileOnly(notation(group, name, version))

fun KotlinDependencyHandler.implementation(group: String, name: String, version: String? = null) =
    implementation(notation(group, name, version))

fun KotlinDependencyHandler.runtimeOnly(group: String, name: String, version: String? = null) =
    runtimeOnly(notation(group, name, version))
