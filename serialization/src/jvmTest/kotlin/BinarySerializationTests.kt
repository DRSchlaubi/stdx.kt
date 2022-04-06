import com.github.jershell.kbson.KBson
import dev.schlaubi.stdx.serialization.UUIDBinarySerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.intellij.lang.annotations.Language
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
internal class BinarySerializationTests {

    @Test
    fun `Serialize uuid to and from json`() {
        Json.testSerializer(UUID.randomUUID(), UUIDBinarySerializer)
    }

    @Test
    fun `Deserialize uuid from json`() {
        val uuid = UUID.randomUUID()
        @Language("JSON")
        val jsonText =
            """{"mostSignificantBits":${uuid.mostSignificantBits},"leastSignificantBits":"${uuid.leastSignificantBits}"}"""
        val decodedUuid = Json.decodeFromString(UUIDBinarySerializer, jsonText)
        assertEquals(uuid, decodedUuid)
    }

    @Test
    fun `Serialize and Deserialize uuid to and from protobuf`() =
        ProtoBuf.testSerializer(UUID.randomUUID(), UUIDBinarySerializer)

    @Test
    fun `Serialize and Deserialize uuid to and from bson`() =
        KBson.default.testSerializer(UUID.randomUUID(), UUIDBinarySerializer)
}
