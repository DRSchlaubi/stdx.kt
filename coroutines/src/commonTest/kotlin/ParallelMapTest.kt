import dev.schlaubi.stdx.coroutines.parallelMap
import dev.schlaubi.stdx.coroutines.parallelMapIndexed
import dev.schlaubi.stdx.coroutines.parallelMapNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class ParallelMapTest {
    @Test
    fun testParallelMap() = runTest {
        val list = ('a'..'z').toList()

        val result = list.parallelMap {
            delay(1.minutes)
        }

        assertEquals(list.size, result.size)
    }

    @Test
    fun testParallelMapIndexed() = runTest {
        val list = ('a'..'z').toList()

        val result = list.parallelMapIndexed { index, _ ->
            delay(1.minutes)
            index
        }

        assertEquals(list.size, result.size)
        assertEquals(list.indices.toList(), result)
    }

    @Test
    fun testParallelMapNotNull() = runTest {
        val list = (('a'..'z').toList() + null)
        val result = list.parallelMapNotNull {
            delay(1.minutes)
            it
        }

        assertEquals(list.size - 1, result.size)
        assertTrue { (result as List<*>).all { it != null } }
    }
}
