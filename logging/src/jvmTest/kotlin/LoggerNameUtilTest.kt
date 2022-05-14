import dev.schlaubi.stdx.logging.logger
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggerNameUtilTest {
    @Test
    fun `test logger name is current class`() {
        val `class` = this::class.qualifiedName
        val logger = logger()

        assertEquals(`class`, logger.name, "Logger and class name must be identical")
    }
}
