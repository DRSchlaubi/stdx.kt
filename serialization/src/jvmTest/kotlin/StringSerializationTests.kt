import com.github.jershell.kbson.KBson
import dev.schlaubi.stdx.serialization.UUIDStringSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Wrapper class used for testing [UUID] serialization in bson.
 */
@Serializable
data class UUIDWrapper(@Serializable(with = UUIDStringSerializer::class) val uuid: UUID)

@OptIn(ExperimentalSerializationApi::class)
internal class StringSerializationTests {
    @Test
    fun `Serialize uuid to and from json`() {
        Json.testSerializer(UUID.randomUUID(), UUIDStringSerializer)
    }

    @Test
    fun `Deserialize uuid from json`() {
        val uuid = UUID.randomUUID()
        val jsonText = """"$uuid""""
        val decodedUuid = Json.decodeFromString(UUIDStringSerializer, jsonText)
        assertEquals(uuid, decodedUuid)
    }

    @Test
    fun `Serialize and Deserialize uuid to and from protobuf`() =
        ProtoBuf.testSerializer(UUID.randomUUID(), UUIDStringSerializer)

    @Test
    fun `Serialize and Deserialize uuid to and from bson`() =
        KBson.default.testSerializer(UUIDWrapper(UUID.randomUUID()), UUIDWrapper.serializer())
}
