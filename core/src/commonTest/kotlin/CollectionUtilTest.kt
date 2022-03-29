import dev.schlaubi.stdx.core.computeIfAbsent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class CollectionUtilTest {
    @Test
    fun testComputeIfAbsent() {
        var counter = 0
        val item = "test"
        val initializer = {
            counter++
            item
        }

        val map = mutableMapOf<String, String>()
        val result = map.computeIfAbsent(item, initializer)
        assertSame(item, result)
        // Ensure initializer is only called once
        assertEquals(1, counter)
    }
}
