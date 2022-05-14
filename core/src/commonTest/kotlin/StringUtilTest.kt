import dev.schlaubi.stdx.core.isNotNumeric
import dev.schlaubi.stdx.core.isNumeric
import dev.schlaubi.stdx.core.limit
import dev.schlaubi.stdx.core.nullIfBlank
import dev.schlaubi.stdx.core.paginate
import dev.schlaubi.stdx.core.removeLineBreaks
import dev.schlaubi.stdx.core.splitList
import dev.schlaubi.stdx.core.splitListStrict
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StringUtilTest {

    @Test
    fun testIsNumeric() {
        assertTrue("1234".isNumeric())
        assertTrue("1".isNumeric())
        assertTrue((Long.MAX_VALUE.toString() + '1').isNumeric())
        assertFalse("1.1".isNumeric())
        assertFalse("NaN".isNumeric())
        assertFalse("Google is cool!".isNumeric())
    }

    @Test
    fun testIsNotNumeric() {
        assertFalse("1234".isNotNumeric())
        assertFalse("1".isNotNumeric())
        assertFalse((Long.MAX_VALUE.toString() + '1').isNotNumeric())
        assertTrue("1.1".isNotNumeric())
        assertTrue("NaN".isNotNumeric())
        assertTrue("Google is cool!".isNotNumeric())
    }

    @Test
    fun testNullIfBlank() {
        assertNull("".nullIfBlank())
        assertNull(" ".nullIfBlank())
        assertNull("     ".nullIfBlank())
//         zero width space (for non IntelliJ users ;))
//        assertNull("\u200B".nullIfBlank())
        assertNull("\n\n\n".nullIfBlank())
        assertNotNull("abc".nullIfBlank())
    }

    @Test
    fun testSplitIntoPagesNormalBehavior() {
        val testList = ('a'..'z').map(Any::toString)

        val pageSize = 1

        val pages = testList.paginate(pageSize, separator = "")
        pages.assertPagination(pageSize, testList)
    }

    @Test
    fun testSplitIntoPagesUnsuitedChars() {
        val testList = ('a'..'z').map {
            // make some items longer than the page length
            it.toString().repeat(if (it.code % 2 == 0) 2 else 1)
        }

        val pageSize = 1

        val pages = testList.paginate(pageSize, separator = "")
        pages.assertPagination(pageSize, testList)
    }

    private fun List<String>.assertPagination(pageSize: Int, original: List<String>) {
        // compare the first char
        assertEquals(original.first().take(1), first().take(1))
        // compare the last char
        assertEquals(original.last().takeLast(1), last().takeLast(1))

        // Ensure length is never exceeded
        assertTrue {
            none { it.length > pageSize }
        }
    }

    @Test
    fun testSplitList() {
        val list = (1..5).map(Any::toString)
        val csv = list.joinToString(",").splitList()
        val commaAndSpace = list.joinToString(", ").splitList()
        val commaAndMoreSpace = list.joinToString(",   ").splitList()

        assertEquals(list, csv)
        assertEquals(list, commaAndSpace)
        assertEquals(list, commaAndMoreSpace)
    }

    @Test
    fun testSplitListStrict() {
        val list = (1..5).map(Any::toString)
        val csv = list.joinToString(",").splitListStrict()
        val commaAndSpace = list.joinToString(", ").splitListStrict()
        val commaAndMoreSpace = list.joinToString(",   ").splitListStrict()

        assertEquals(list, csv)
        assertNotEquals(list, commaAndSpace)
        assertNotEquals(list, commaAndMoreSpace)
    }

    @Test
    fun testLimit() {
        val longString = ('a'..'z').joinToString("")
        val limit = 10

        assertTrue(longString.limit(limit).length <= limit)
        assertSame(longString.limit(longString.length), longString)
    }

    @Test
    fun testLineBreaks() {
        val multiLineString = buildString {
            repeat(5) {
                appendLine("test")
            }
        }
        val nonMultilineString = buildString {
            repeat(5) {
                append("test")
                append(" ")
            }
        }

        assertEquals(nonMultilineString, multiLineString.removeLineBreaks())
    }
}
