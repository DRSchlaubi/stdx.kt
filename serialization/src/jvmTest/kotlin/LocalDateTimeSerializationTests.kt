import com.github.jershell.kbson.KBson
import dev.schlaubi.stdx.serialization.LocalDateTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.time.LocalDateTime
import kotlin.test.Test

/**
 * Wrapper class used for testing [LocalDateTime] serialization in bson.
 */
@Serializable
data class LocalDateTimeWrapper(@Serializable(with = LocalDateTimeSerializer::class) val value: LocalDateTime)

@OptIn(ExperimentalSerializationApi::class)
internal class LocalDateTimeSerializationTests {

    @Test
    fun `Serialize LocalDateTime to and from json`() {
        Json.testSerializer(LocalDateTime.now(), LocalDateTimeSerializer)
    }

    @Test
    fun `Serialize LocalDateTime to and from protobuf`() {
        ProtoBuf.testSerializer(LocalDateTime.now(), LocalDateTimeSerializer)
    }

    @Test
    fun `Serialize LocalDateTime to and from bson`() {
        KBson.default.testSerializer(
            LocalDateTimeWrapper(LocalDateTime.now()),
            LocalDateTimeWrapper.serializer()
        )
    }
}
