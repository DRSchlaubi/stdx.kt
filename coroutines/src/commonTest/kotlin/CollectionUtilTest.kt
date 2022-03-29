import dev.schlaubi.stdx.coroutines.forEachParallel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class CollectionUtilTest {

    @Test
    fun testParallelForEach() = runTest {
        val list = 'a'..'z'
        var count = 0
        list.forEachParallel {
            delay(1.minutes)
            count++
        }

        assertEquals(list.count(), count)
    }
}
