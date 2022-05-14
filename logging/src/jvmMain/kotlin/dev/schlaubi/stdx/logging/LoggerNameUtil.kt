package dev.schlaubi.stdx.logging

import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory

/**
 * Creates a [KLogger] for [StackWalker.getCallerClass].
 */
public fun logger(): KLogger {
    val callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
    val logger = LoggerFactory.getLogger(callerClass)
    return KotlinLogging.logger(logger)
}
