package dev.schlaubi.stdx.serialization

import kotlinx.serialization.json.JsonObject

private val emptyJsonObject = JsonObject(emptyMap())

/**
 * Returns an empty [JsonObject].
 */
public fun emptyJsonObject(): JsonObject = emptyJsonObject
