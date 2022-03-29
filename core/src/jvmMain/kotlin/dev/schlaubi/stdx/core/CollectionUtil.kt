@file:JvmName("CollectionUtilJvm")

package dev.schlaubi.stdx.core

import java.util.Queue

/**
 * Polls [amount] items from this [Queue].
 */
public fun <T> Queue<T>.poll(amount: Int): List<T?> = buildList(amount) {
    repeat(amount) {
        add(poll())
    }
}
