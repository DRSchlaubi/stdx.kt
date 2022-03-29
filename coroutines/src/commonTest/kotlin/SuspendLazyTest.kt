import dev.schlaubi.stdx.coroutines.suspendLazy
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class SuspendLazyTest {

    @Test
    fun testUnsafeSuspendLazy() = runTest {
        testUnsafeMode()
    }

    @Test
    fun testPublicationSuspendLazy() = runTest {
        if (!isJS) {
            testSafeMode(LazyThreadSafetyMode.PUBLICATION)
        }
    }

    @Test
    fun testSynchronizedSuspendLazy() = runTest {
        if (!isJS) {
            testSafeMode(LazyThreadSafetyMode.SYNCHRONIZED)
        }
    }

    private suspend fun testSafeMode(mode: LazyThreadSafetyMode) = coroutineScope {
        var count = 0
        val obj = Any()
        val initializer = suspend {
            count++
            delay(1.minutes)
            if (count == 1) {
                obj
            } else {
                obj
            }
        }

        val lazy = suspendLazy(mode, initializer)

        val children = (1..5).map {
            async { lazy() }
        }

        assertTrue(children.awaitAll().all { it === obj })
    }
}

private suspend fun testUnsafeMode() {
    val obj = Any()
    val lazy = suspendLazy {
        delay(1.minutes)
        obj
    }

    assertSame(obj, lazy())
}
