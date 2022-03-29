import dev.schlaubi.stdx.core.poll
import java.util.LinkedList
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmCollectionUtilTest {

    @Test
    fun testPoll() {
        val list = LinkedList(listOf(1, 2, 3, 4, 5))
        val copy = list.toList()

        val amount = 3
        val polled = list.poll(amount)
        assertEquals(copy.take(3), polled)
        // Ensure polling worked
        assertEquals(copy.subList(amount, copy.size), list)
    }
}
