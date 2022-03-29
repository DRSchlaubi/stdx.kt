package dev.schlaubi.stdx.coroutines

/**
 * Similar to [Lazy] but for suspend functions.
 *
 * ```kotlin
 * val suspendLazy = suspendLazy { suspendCall() }
 *
 * suspendLazy() // retrieve
 * ```
 *
 * @see suspendLazy
 */
public interface SuspendLazy<out T> {
    /**
     * Function to retrieve the value.
     */
    public suspend fun get(): T

    /**
     * Whether the value has been already initialized.
     */
    public fun isInitialized(): Boolean

    /**
     * Operator function calling [get] for more idiomatic use.
     */
    public suspend operator fun invoke(): T = get()
}

/**
 * Suspending initializer of a value.
 */
public typealias SuspendingInitializer<T> = suspend () -> T

/**
 * Creates the default [SuspendLazy] implementation, behavior might be different based on platform.
 *
 * @see SuspendLazy
 */
public expect fun <T> suspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T>

/**
 * The same as [suspendLazy] but with [LazyThreadSafetyMode.NONE]
 *
 * Use this for local lazy variables, which only one thread/coroutine can access
 */
public fun <T> localSuspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T> = UnsafeSuspendLazyImpl(initializer)

/**
 * Creates a [SuspendLazy] implementation corresponding to [mode], please refer to the platform documentation for
 * how [mode] is being used.
 *
 * @see SuspendLazy
 */
public expect fun <T> suspendLazy(mode: LazyThreadSafetyMode, initializer: SuspendingInitializer<T>): SuspendLazy<T>

@Suppress("ClassName")
internal object UNINITIALIZED_VALUE

internal class UnsafeSuspendLazyImpl<out T>(initializer: SuspendingInitializer<T>) : SuspendLazy<T> {
    private var initializer: (SuspendingInitializer<T>)? = initializer
    private var _value: Any? = UNINITIALIZED_VALUE

    override suspend fun get(): T {
        if (_value === UNINITIALIZED_VALUE) {
            _value = initializer!!()
            initializer = null
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) _value.toString() else "Lazy value not initialized yet."
}
