package dev.schlaubi.envconf

import kotlinx.cinterop.toKString
import platform.posix.getenv

internal actual fun getEnvValue(name: String): String? = getenv(name)?.toKString()
