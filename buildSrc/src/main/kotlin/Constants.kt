
// projects which are supposed to be omitted in bom and full
val groupProjects = listOf("stdx-bom", "stdx-full")

val isCI = System.getenv("GITHUB_RUN_ID") != null
val hostOs = System.getProperty("os.name")
val isLinux = hostOs == "Linux"
val runMainCI = isLinux || !isCI
