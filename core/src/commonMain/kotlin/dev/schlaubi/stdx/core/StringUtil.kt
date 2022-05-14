@file:Suppress("NOTHING_TO_INLINE")

package dev.schlaubi.stdx.core

import kotlin.contracts.contract

private val LIST_SEPARATOR_REGEX = ",\\s*".toRegex()

/**
 * Checks whether a String is numeric (meaning it contains an Integer)
 *
 * This does not allow for doubles or check for Int ranges.
 *
 * @see isNotNumeric
 */
public fun CharSequence.isNumeric(): Boolean = all(Char::isDigit)

/**
 * Checks whether a String is not numeric (meaning it contains an Integer)
 *
 * This does not account for doubles or check for Int ranges.
 *
 * @see isNumeric
 */
public inline fun CharSequence.isNotNumeric(): Boolean = !isNumeric()

/**
 * Returns `null` if this String [isBlank] or the String itself
 *
 * @see isBlank
 * @see ifBlank
 */
public inline fun <C : CharSequence> C.nullIfBlank(): C? = ifBlank { null }

/**
 * Returns `false` if this nullable char sequence is either `null` or empty.
 *
 * @see isNullOrEmpty
 */
public inline fun CharSequence?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(false) implies (this@isNotNullOrEmpty != null)
    }

    return !isNullOrEmpty()
}

/**
 * Returns `false` if this nullable char sequence is either `null` or empty or consists solely of whitespace characters.
 *
 * @see isNullOrBlank
 */
public inline fun CharSequence?.isNotNullOrBlank(): Boolean {
    contract {
        returns(false) implies (this@isNotNullOrBlank != null)
    }

    return !isNullOrBlank()
}

/**
 * Tries to paginate this Strings (seperated by [separator]) into pages of [pageLength] with words in mind.
 *
 * This function will try to not cut through single elements if the element does not exceed [pageLength]
 */
public fun List<String>.paginate(pageLength: Int, separator: String = "\n"): List<String> {
    // Split strings into sub-pages, if one string is longer than a page
    if (any { it.length > pageLength }) return flatMap { it.chunked(pageLength) }.paginate(pageLength, separator)

    var currentLength = 0
    var currentList = ArrayList<String>(10)
    val paged = ArrayList<String>(size)

    val iterator = iterator()

    fun addCurrentList() {
        paged.add(currentList.joinToString(separator))
    }

    while (iterator.hasNext()) {
        val line = iterator.next()
        val fullLength = line.length + separator.length
        if ((currentLength + fullLength) > pageLength) {
            addCurrentList()
            currentList = ArrayList(10)
            currentLength = 0
        }

        currentList.add(line)
        currentLength += fullLength
    }
    addCurrentList()

    return paged
}

/**
 * Splits a comma seperated list.
 */
public fun CharSequence.splitList(): List<String> = split(LIST_SEPARATOR_REGEX)

/**
 * Splits a comma seperated list.
 */
public fun CharSequence.splitListStrict(): List<String> = split(',')

/**
 * Limits this string to [maxLength] and adds [truncate] at the end if the string was shortened-
 */
public fun String.limit(maxLength: Int, truncate: String = "..."): String = if (length > maxLength) {
    substring(0, maxLength - truncate.length) + truncate
} else {
    this
}

/**
 * Removes all lines and replaces them with [separator].
 */
public fun CharSequence.removeLineBreaks(separator: String = " "): String = lines().joinToString(separator)
