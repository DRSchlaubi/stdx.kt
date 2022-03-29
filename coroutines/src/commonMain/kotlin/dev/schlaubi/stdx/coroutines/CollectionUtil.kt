package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Performs [action] on each element of this [Iterable] in parallel.
 * Suspends until all spawned child coroutines are done.
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T> Iterable<T>.forEachParallel(action: suspend (T) -> Unit): Unit = coroutineScope {
    forEach {
        launch {
            action(it)
        }
    }
}

/**
 * Version of [forEachParallel] using a lambda-with-receiver instead.
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T> Iterable<T>.onEachParallel(action: suspend T.() -> Unit): Unit = forEachParallel(action)
