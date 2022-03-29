package dev.schlaubi.stdx.coroutines

/**
 * Creates a [SuspendLazy] implementation using [LazyThreadSafetyMode.NONE] as JS doesn't provide thread safety.
 *
 * @see SuspendLazy
 */
public actual fun <T> suspendLazy(initializer: SuspendingInitializer<T>): SuspendLazy<T> = UnsafeSuspendLazyImpl(initializer)

/**
 * Creates a [SuspendLazy] implementation using [LazyThreadSafetyMode.NONE] as JS doesn't provide thread safety.
 *
 * @see SuspendLazy
 */
public actual fun <T> suspendLazy(mode: LazyThreadSafetyMode, initializer: SuspendingInitializer<T>): SuspendLazy<T> =
    suspendLazy(initializer)
