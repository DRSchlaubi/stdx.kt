# coroutines

Extensions regarding [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
and [suspend functions](https://kotlinlang.org/docs/composing-suspending-functions.html)

# Examples

## ParallelMap

```kotlin
suspend fun main() {
    listOf(1, 2, 3).parallelMap(maxConcurrentRequests = 2) {
        suspendingHttlCall(it)
    }
}
```

## SuspendLazy

```kotlin
val pi by suspendLazy { superAwkwardWayToFetchPi() }

suspend fun main() {
    println(pi())
}
```

# Download

Please refer to [this](../README.md#download) page

# Documentation

Documentation can be found [here](https://stdx.schlau.bi/stdx-coroutines)
