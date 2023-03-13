import dev.schlaubi.envconf.Environment
import dev.schlaubi.envconf.getEnv
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("LocalVariableName")
class PlainTest {

    @Test
    fun testSimpleValue() {
        val HELLO by getEnv()

        assertEquals(HELLO, "HELLO")
    }

    @Test
    fun testSimpleValueWithShortcut() {
        val HELLO by Environment

        assertEquals(HELLO, "HELLO")
    }

    @Test
    fun testSimpleValueWithTransform() {
        val HELLO by getEnv(transform = String::lowercase)

        assertEquals(HELLO, "hello")
    }

    @Test
    fun testSimpleValueWithPrefix() {
        val HELLO by getEnv("PREFIX_", transform = String::lowercase)

        assertEquals(HELLO, "hello")
    }

    @Test
    fun testSimpleValueWithDefault() {
        val HELLO by Environment
        val HELLO2 by getEnv(default = "HELLO")

        assertEquals(HELLO, "HELLO")
        assertEquals(HELLO2, "HELLO")
    }
}
