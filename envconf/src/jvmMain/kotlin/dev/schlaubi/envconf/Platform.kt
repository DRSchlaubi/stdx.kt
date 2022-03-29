package dev.schlaubi.envconf

internal actual fun getEnvValue(name: String): String? = System.getenv(name)
