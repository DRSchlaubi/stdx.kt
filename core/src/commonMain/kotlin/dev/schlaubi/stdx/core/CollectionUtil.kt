package dev.schlaubi.stdx.core

/**
 * Retrieves the element corresponding to [key] from this map if it already exists
 * or creates it by calling [initializer] and saving the result to the map
 */
public inline fun <K, E> MutableMap<K, E>.computeIfAbsent(key: K, initializer: () -> E): E {
    val v1 = this[key]
    if (v1 == null) {
        val v2 = initializer()
        this[key] = v2
        return v2
    }
    return v1
}

/**
 * Performs the given [action] on each element.
 */
public inline fun <T> Iterable<T>.onEach(action: T.() -> Unit): Unit = forEach { it.action() }
0
