package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Maps every element of this collection using [mapper] in parallel.
 *
 * @param maxConcurrentRequests the maximum amount of concurrent coroutines (`null` means unlimited)
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T, S> Collection<T>.parallelMap(
    maxConcurrentRequests: Int? = null,
    mapper: suspend (T) -> S
): List<S> = parallelMapIndexed(maxConcurrentRequests) { _, t -> mapper(t) }

/**
 * Maps every element of this collection using [mapper] in parallel and filters out `null`.
 *
 * @param maxConcurrentRequests the maximum amount of concurrent coroutines (`null` means unlimited)
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T, S> Collection<T>.parallelMapNotNull(
    maxConcurrentRequests: Int? = null,
    mapper: suspend (T) -> S?
): List<S> = parallelMap(maxConcurrentRequests, mapper).filterNotNull()

/**
 * Maps every element of this collection using [mapper] in parallel and filters out `null`.
 *
 * @param maxConcurrentRequests the maximum amount of concurrent coroutines (`null` means unlimited)
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T, S> Collection<T>.parallelMapNotNullIndexed(
    maxConcurrentRequests: Int? = null,
    mapper: suspend (index: Int, T) -> S?
): List<S> = parallelMapIndexed(maxConcurrentRequests, mapper).filterNotNull()

/**
 * Maps every element of this collection using [mapper] in parallel.
 *
 * @param maxConcurrentRequests the maximum amount of concurrent coroutines (`null` means unlimited)
 *
 * **This in itself does not launch multiple threads, how many threads are launched is dependent on the used Dispatcher**
 */
public suspend fun <T, S> Collection<T>.parallelMapIndexed(
    maxConcurrentRequests: Int? = null,
    mapper: suspend (index: Int, T) -> S
): List<S> {
    val semaphore = maxConcurrentRequests?.let(::Semaphore)

    val result = coroutineScope {
        mapIndexed { index, item ->
            async {
                val block = suspend {
                    mapper(index, item)
                }

                if (semaphore != null) {
                    semaphore.withPermit { block() }
                } else {
                    block()
                }
            }
        }
    }

    return result.awaitAll()
}
