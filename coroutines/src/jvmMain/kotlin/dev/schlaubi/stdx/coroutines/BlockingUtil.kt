package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Makes an "inappropriate blocking method call" appropriate.
 *
 * @see Dispatchers.IO
 * @see withContext
 */
public suspend fun <T> blocking(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.IO, block)
