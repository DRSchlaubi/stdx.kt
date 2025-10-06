package dev.schlaubi.envconf

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun getEnvValue(name: String): String? = getenv(name)?.toKString()
