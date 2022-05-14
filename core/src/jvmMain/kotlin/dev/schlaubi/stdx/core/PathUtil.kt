package dev.schlaubi.stdx.core

import java.nio.file.Path

/**
 * Returns the [nth][n] parent of this [Path].
 */
public fun Path.parent(n: Int): Path {
    require(n <= nameCount) { "$this does not have $n parents" }
    return root.resolve(subpath(0, nameCount - n))
}
