@file:JvmName("SuspendLazyJVM")

package dev.schlaubi.stdx.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater

/**
 * Creates a [SuspendLazy] implementation using [LazyThreadSafetyMode.SYNCHRONIZED].
 *
 * @see SuspendLazy
 */
public actual fun <T> suspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T> = SynchronizedSuspendLazyImpl(initializer)

/**
 * Creates a [SuspendLazy] implementation using [mode].
 *
 * @see SuspendLazy
 */
public actual fun <T> suspendLazy(mode: LazyThreadSafetyMode, initializer: SuspendingInitializer<T>): SuspendLazy<T> =
    when(mode) {
        LazyThreadSafetyMode.SYNCHRONIZED -> SynchronizedSuspendLazyImpl(initializer)
        LazyThreadSafetyMode.PUBLICATION -> SafePublicationSuspendLazyImpl(initializer)
        LazyThreadSafetyMode.NONE -> UnsafeSuspendLazyImpl(initializer)
    }

private class SynchronizedSuspendLazyImpl<out T>(initializer: SuspendingInitializer<T>, lock: Mutex? = null) : SuspendLazy<T> {
    private var initializer: (SuspendingInitializer<T>)? = initializer

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: Mutex()

    @Suppress("LocalVariableName")
    override suspend fun get(): T {
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            return _v1 as T
        }


        return lock.withLock {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST") (_v2 as T)
            } else {
                val typedValue = initializer!!.invoke()
                _value = typedValue
                initializer = null
                typedValue
            }
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) _value.toString() else "Lazy value not initialized yet."
}

private class SafePublicationSuspendLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {

    @Volatile
    private var initializer: (SuspendingInitializer<T>)? = initializer

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // this final field is required to enable safe initialization of the constructed instance
    private val final: Any = UNINITIALIZED_VALUE

    override suspend fun get(): T {
        val value = _value
        if (value !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }

        val initializerValue = initializer
        // if we see null in initializer here, it means that the value is already set by another thread
        if (initializerValue != null) {
            val newValue = initializerValue()
            if (valueUpdater.compareAndSet(this, UNINITIALIZED_VALUE, newValue)) {
                initializer = null
                return newValue
            }
        }
        lazy {  }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) _value.toString() else "Lazy value not initialized yet."

    companion object {
        private val valueUpdater = AtomicReferenceFieldUpdater.newUpdater(
            SafePublicationSuspendLazyImpl::class.java,
            Any::class.java,
            "_value"
        )
    }
}

