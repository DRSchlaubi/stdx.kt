rootProject.name = "stdx-kt"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("core", "coroutines", "logging", "envconf", "test-tools", "bom", "full")

rootProject.children.forEach {
    it.name = "stdx-${it.name}"
}
