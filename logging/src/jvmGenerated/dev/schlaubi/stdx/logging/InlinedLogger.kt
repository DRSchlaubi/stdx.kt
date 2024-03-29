// This file is generated by Gradle task generateLoggerFunctions please do not edit it manually
package dev.schlaubi.stdx.logging

import mu.KLogger

/**
 * Inline version of [KLogger.debug] so it can call suspend functions
 */
public actual inline fun KLogger.debugInlined(message: LazyLogMessage): Unit {
  if (isDebugEnabled) {
      debug(message())
  }
}

/**
 * Inline version of [KLogger.debug] so it can call suspend functions
 */
public actual inline fun KLogger.debugInlined(throwable: Throwable, message: LazyLogMessage): Unit {
  if (isDebugEnabled) {
      val computedMessage = message()
      
      debug(throwable) { computedMessage }
  }
}

/**
 * Inline version of [KLogger.trace] so it can call suspend functions
 */
public actual inline fun KLogger.traceInlined(message: LazyLogMessage): Unit {
  if (isTraceEnabled) {
      trace(message())
  }
}

/**
 * Inline version of [KLogger.trace] so it can call suspend functions
 */
public actual inline fun KLogger.traceInlined(throwable: Throwable, message: LazyLogMessage): Unit {
  if (isTraceEnabled) {
      val computedMessage = message()
      
      trace(throwable) { computedMessage }
  }
}

/**
 * Inline version of [KLogger.error] so it can call suspend functions
 */
public actual inline fun KLogger.errorInlined(message: LazyLogMessage): Unit {
  if (isErrorEnabled) {
      error(message())
  }
}

/**
 * Inline version of [KLogger.error] so it can call suspend functions
 */
public actual inline fun KLogger.errorInlined(throwable: Throwable, message: LazyLogMessage): Unit {
  if (isErrorEnabled) {
      val computedMessage = message()
      
      error(throwable) { computedMessage }
  }
}

/**
 * Inline version of [KLogger.info] so it can call suspend functions
 */
public actual inline fun KLogger.infoInlined(message: LazyLogMessage): Unit {
  if (isInfoEnabled) {
      info(message())
  }
}

/**
 * Inline version of [KLogger.info] so it can call suspend functions
 */
public actual inline fun KLogger.infoInlined(throwable: Throwable, message: LazyLogMessage): Unit {
  if (isInfoEnabled) {
      val computedMessage = message()
      
      info(throwable) { computedMessage }
  }
}

/**
 * Inline version of [KLogger.warn] so it can call suspend functions
 */
public actual inline fun KLogger.warnInlined(message: LazyLogMessage): Unit {
  if (isWarnEnabled) {
      warn(message())
  }
}

/**
 * Inline version of [KLogger.warn] so it can call suspend functions
 */
public actual inline fun KLogger.warnInlined(throwable: Throwable, message: LazyLogMessage): Unit {
  if (isWarnEnabled) {
      val computedMessage = message()
      
      warn(throwable) { computedMessage }
  }
}
