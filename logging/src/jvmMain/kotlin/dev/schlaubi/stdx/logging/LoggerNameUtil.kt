package dev.schlaubi.stdx.logging

import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory

/**
 * Creates a [KLogger] for [StackWalker.getCallerClass].
 */
@Suppress("Since15") // It doesn't really matter here, since this method isn't used internally
public fun logger(): KLogger {
    val callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
    val logger = LoggerFactory.getLogger(callerClass)
    return KotlinLogging.logger(logger)
}
