import dev.schlaubi.envconf.environment
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
        val HELLO by environment

        assertEquals(HELLO, "HELLO")
    }

    @Test
    fun testSimpleValueWithTransform() {
        val HELLO by getEnv { it.lowercase() }

        assertEquals(HELLO, "hello")
    }

    @Test
    fun testSimpleValueWithPrefix() {
        val HELLO by getEnv("PREFIX_") { it.lowercase() }

        assertEquals(HELLO, "hello")
    }

    @Test
    fun testSimpleValueWithDefault() {
        val HELLO by environment
        val HELLO2 by getEnv(default = "HELLO")

        assertEquals(HELLO, "HELLO")
        assertEquals(HELLO2, "HELLO")
    }
}
