package dev.schlaubi.envconf

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Shortcut to make API usable like this
 *
 * ```kotlin
 *val PORT by environment
 * ```
 */
public val environment: EnvironmentVariable<String>
    get() = getEnv()

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if the found variable is empty or invalid
 */
public fun getEnv(
    prefix: String? = null,
    default: String? = null
): EnvironmentVariable<String> =
    EnvironmentVariable(prefix, { it }, default)

/**
 * Returns a delegated environment variable prefixed by [prefix] that fallbacks to [default] if the found variable is empty or invalid.
 *
 * The variable is transformed to [T] by [transform]
 */
public fun <T> getEnv(
    prefix: String? = null,
    default: T? = null,
    transform: (String) -> T?
): EnvironmentVariable<T> =
    EnvironmentVariable(prefix, transform, default)

/**
 * Delegated property for a environment variable.
 *
 * @param prefix the prefix for the variable
 * @param transform a transformer to map the value to another type
 * @param default an optional default value
 *
 * @param T the type of the (transformed) variable
 *
 * @see getEnv
 * @see Config
 * @see ReadOnlyProperty
 */
@Suppress("LocalVariableName")
public sealed class EnvironmentVariable<T>(
    private val prefix: String?,
    private val transform: (String) -> T?,
    private val default: T?,
) : ReadOnlyProperty<Any?, T> {

    protected lateinit var name: String
    private val value: T? by lazy {
        getEnv(name, default, transform)
    }

    /**
     * Computes the name of the variable prefixed by [prefix].
     */
    private val KProperty<*>.prefixedName: String
        get() = prefix?.let { it + name } ?: name

    /**
     * Makes this variable optional.
     *
     * @return a new [EnvironmentVariable] being optional
     */
    public open fun optional(): EnvironmentVariable<T?> = Optional(prefix, transform, default)

    final override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        name = property.prefixedName
        return convert(value)
    }

    protected abstract fun convert(value: T?): T

    /**
     * Internal getter.
     */
    private fun <T> getEnv(
        name: String,
        default: T? = null,
        transform: (String) -> T?
    ): T? = getEnvValue(name)?.let(transform) ?: default

    private class Required<T>(prefix: String?, transform: (String) -> T?, default: T?) :
        EnvironmentVariable<T>(prefix, transform, default) {

        override fun convert(value: T?): T = value ?: missing(name)

        private fun missing(name: String): Nothing = error("Missing env variable: $name")

    }

    private class Optional<T>(prefix: String?, transform: (String) -> T?, default: T?) :
        EnvironmentVariable<T?>(prefix, transform, default) {

        override fun convert(value: T?): T? = value

        override fun optional(): EnvironmentVariable<T?> = this

    }

    public companion object {
        /**
         * @see EnvironmentVariable
         */
        public operator fun <T> invoke(
            prefix: String?,
            transform: (String) -> T?,
            default: T?,
        ): EnvironmentVariable<T> = Required(prefix, transform, default)
    }
}
