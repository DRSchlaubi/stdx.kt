@file:OptIn(ExperimentalAtomicApi::class)

package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Creates a [SuspendLazy] implementation using [LazyThreadSafetyMode.SYNCHRONIZED] as JS doesn't provide thread safety.
 *
 * **Memory info:** This requires using [the new memory model](https://blog.jetbrains.com/kotlin/2021/08/try-the-new-kotlin-native-memory-manager-development-preview/)
 *
 * @see SuspendLazy
 */
@OptIn(ExperimentalStdlibApi::class)
public actual fun <T> suspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T> =
    SynchronizedLazyImpl(initializer)

/**
 * Creates a [SuspendLazy] implementation using [mode] as JS doesn't provide thread safety.
 *
 * **Memory info:** This requires using [the new memory model](https://blog.jetbrains.com/kotlin/2021/08/try-the-new-kotlin-native-memory-manager-development-preview/) unless you
 * use [LazyThreadSafetyMode.NONE]
 *
 * @see SuspendLazy
 */
@OptIn(ExperimentalStdlibApi::class)
public actual fun <T> suspendLazy(mode: LazyThreadSafetyMode, initializer: SuspendingInitializer<T>): SuspendLazy<T> =
    when (mode) {
        LazyThreadSafetyMode.SYNCHRONIZED -> SynchronizedLazyImpl(initializer)
        LazyThreadSafetyMode.PUBLICATION -> SafePublicationLazyImpl(initializer)
        LazyThreadSafetyMode.NONE -> UnsafeSuspendLazyImpl(initializer)
    }


@Suppress("UNCHECKED_CAST")
internal class SynchronizedLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {
    private var initializer = AtomicReference<SuspendingInitializer<T>?>(initializer)
    private var valueRef = AtomicReference<Any?>(UNINITIALIZED_VALUE)
    private val lock = Mutex()

    @Suppress("LocalVariableName")
    override suspend fun get(): T {
        val _v1 = valueRef.load()
        if (_v1 !== UNINITIALIZED_VALUE) {
            return _v1 as T
        }

        return lock.withLock {
            val _v2 = valueRef.load()
            if (_v2 === UNINITIALIZED_VALUE) {
                val typedValue = initializer.load()!!()
                valueRef.store(typedValue)
                initializer.store(null)
                typedValue
            } else {
                _v2 as T
            }
        }
    }

    override fun isInitialized() = valueRef.load() !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) valueRef.load().toString() else "Lazy value not initialized yet."
}


@Suppress("UNCHECKED_CAST")
internal class SafePublicationLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {
    private var initializer = AtomicReference<SuspendingInitializer<T>?>(initializer)
    private var valueRef = AtomicReference<Any?>(UNINITIALIZED_VALUE)

    override suspend fun get(): T {
        val value = valueRef.load()
        if (value !== UNINITIALIZED_VALUE) {
            return value as T
        }

        val initializerValue = initializer.load()
        // if we see null in initializer here, it means that the value is already set by another thread
        if (initializerValue != null) {
            val newValue = initializerValue()
            if (valueRef.compareAndSet(UNINITIALIZED_VALUE, newValue)) {
                initializer.store(null)
                return newValue
            }
        }
        return valueRef.load() as T
    }

    override fun isInitialized(): Boolean = valueRef.load() !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) valueRef.load().toString() else "Lazy value not initialized yet."
}
