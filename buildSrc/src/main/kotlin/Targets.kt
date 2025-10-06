import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalWasmDsl::class)
fun KotlinMultiplatformExtension.fullJs() {
    js(IR) {
        browser()
        nodejs()

        useCommonJs()
    }

    wasmJs {
        browser()
        nodejs()
    }
    wasmWasi { nodejs() }
}

@OptIn(ExperimentalWasmDsl::class)
fun KotlinMultiplatformExtension.nodeJs() {
    js(IR) {
        nodejs()
    }
    wasmWasi { nodejs() }
}

fun KotlinMultiplatformExtension.desktopOSX86() {
    linuxX64()
    mingwX64()
    macosX64()
}

fun KotlinMultiplatformExtension.desktopAll() {
    desktopOSX86()
    macosArm64()
    linuxArm64()
    mingwX64()
}

fun KotlinMultiplatformExtension.appleMobilePlatforms() {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
}
