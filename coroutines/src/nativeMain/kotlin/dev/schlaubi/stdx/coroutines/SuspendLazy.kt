package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.native.concurrent.*

private fun throwMMError(): Nothing =
    throw UnsupportedOperationException("suspendLazy only supports the new memory model")

/**
 * Creates a [SuspendLazy] implementation using [LazyThreadSafetyMode.SYNCHRONIZED] as JS doesn't provide thread safety.
 *
 * **Memory info:** This requires using [the new memory model](https://blog.jetbrains.com/kotlin/2021/08/try-the-new-kotlin-native-memory-manager-development-preview/)
 *
 * @see SuspendLazy
 */
@OptIn(ExperimentalStdlibApi::class)
public actual fun <T> suspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T> =
    if (isExperimentalMM())
        SynchronizedLazyImpl(initializer)
    else
        throwMMError()

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
        LazyThreadSafetyMode.SYNCHRONIZED -> if (isExperimentalMM()) SynchronizedLazyImpl(initializer) else throwMMError()
        LazyThreadSafetyMode.PUBLICATION -> if (isExperimentalMM()) SafePublicationLazyImpl(initializer) else throwMMError()
        LazyThreadSafetyMode.NONE -> UnsafeSuspendLazyImpl(initializer)
    }


@Suppress("UNCHECKED_CAST")
internal class SynchronizedLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {
    private var initializer = AtomicReference<SuspendingInitializer<T>?>(initializer)
    private var valueRef = AtomicReference<Any?>(UNINITIALIZED_VALUE)
    private val lock = Mutex()

    @Suppress("LocalVariableName")
    override suspend fun get(): T {
        val _v1 = valueRef.value
        if (_v1 !== UNINITIALIZED_VALUE) {
            return _v1 as T
        }

        return lock.withLock {
            val _v2 = valueRef.value
            if (_v2 === UNINITIALIZED_VALUE) {
                val typedValue = initializer.value!!()
                valueRef.value = typedValue
                initializer.value = null
                typedValue
            } else {
                _v2 as T
            }
        }
    }

    override fun isInitialized() = valueRef.value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) valueRef.value.toString() else "Lazy value not initialized yet."
}


@Suppress("UNCHECKED_CAST")
internal class SafePublicationLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {
    private var initializer = AtomicReference<SuspendingInitializer<T>?>(initializer)
    private var valueRef = AtomicReference<Any?>(UNINITIALIZED_VALUE)

    override suspend fun get(): T {
        val value = valueRef.value
        if (value !== UNINITIALIZED_VALUE) {
            return value as T
        }

        val initializerValue = initializer.value
        // if we see null in initializer here, it means that the value is already set by another thread
        if (initializerValue != null) {
            val newValue = initializerValue()
            if (valueRef.compareAndSet(UNINITIALIZED_VALUE, newValue)) {
                initializer.value = null
                return newValue
            }
        }
        return valueRef.value as T
    }

    override fun isInitialized(): Boolean = valueRef.value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) valueRef.value.toString() else "Lazy value not initialized yet."
}
