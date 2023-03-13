import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

fun KotlinMultiplatformExtension.fullJs() = js(IR) {
    browser()
    nodejs()
}

fun KotlinMultiplatformExtension.nodeJs() = js(IR) {
    nodejs()
}

fun KotlinMultiplatformExtension.desktopOSX86() {
    native { linuxX64() }
    native { mingwX64() }
    native { macosX64() }
}

fun KotlinMultiplatformExtension.desktopAll() {
    desktopOSX86()
    native { macosArm64() }
}

fun KotlinMultiplatformExtension.appleMobilePlatforms() {
    nativeGroup { ios(configure = it) }
    nativeGroup { tvos(configure = it) }
    nativeGroup { watchos(configure = it) }
}

inline fun KotlinMultiplatformExtension.nativeGroup(block: KotlinMultiplatformExtension.(apply: KotlinNativeTarget.() -> Unit) -> Unit) {
    block { native(this) }
}

inline fun KotlinMultiplatformExtension.native(block: KotlinMultiplatformExtension.() -> KotlinNativeTarget) =
    native(block())

fun KotlinMultiplatformExtension.native(target: KotlinNativeTarget) {
    val nativeMain = sourceSets.maybeCreate("nativeMain").apply {
        dependsOn(sourceSets.getByName("commonMain"))
    }
    val nativeTest = sourceSets.maybeCreate("nativeTest").apply {
        dependsOn(sourceSets.getByName("commonTest"))
    }

    sourceSets.apply {
        getByName(target.name + "Main") {
            dependsOn(nativeMain)
        }
        getByName(target.name + "Test") {
            dependsOn(nativeTest)
        }
    }
}
