package dev.schlaubi.envconf

private external val process: Process

private external class Process {
    val env: ProcessENV
}

private external class ProcessENV : Dict<String>

private external interface Dict<T>

@Suppress("UnsafeCastFromDynamic")
private inline operator fun <T> Dict<T>.get(key: String): T? = try {
    this.asDynamic()[key]
} catch (e: IllegalStateException) {
    null
}

internal actual fun getEnvValue(name: String): String? = process.env[name]