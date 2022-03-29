@file:JvmName("StringUtilJvm")

package dev.schlaubi.stdx.core

import java.security.MessageDigest

/**
 * Hashes this String using `SHA-256`.
 *
 * @see hash
 */
public fun String.sha256(): String = hash("SHA-256")

/**
 * Hashes this string using [algorithm].
 *
 * @param algorithm the algorithm according to
 * [the Java spec](https://docs.oracle.com/en/java/javase/18/docs/specs/security/standard-names.html#messagedigest-algorithms)
 *
 * @see MessageDigest.digest
 */
public fun String.hash(algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}
