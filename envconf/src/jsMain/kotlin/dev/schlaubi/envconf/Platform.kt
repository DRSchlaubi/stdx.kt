package dev.schlaubi.envconf

import js.core.get
import node.process.process

internal actual fun getEnvValue(name: String): String? = process.env[name]
