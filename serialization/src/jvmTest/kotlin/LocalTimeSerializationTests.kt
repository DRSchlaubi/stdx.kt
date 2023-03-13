import com.github.jershell.kbson.KBson
import dev.schlaubi.stdx.serialization.LocalTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.time.LocalTime
import kotlin.test.Test

/**
 * Wrapper class used for testing [LocalTime] serialization in bson.
 */
@Serializable
data class LocalTimeWrapper(@Serializable(with = LocalTimeSerializer::class) val value: LocalTime)

@OptIn(ExperimentalSerializationApi::class)
internal class LocalTimeSerializationTests {

    @Test
    fun `Serialize LocalTime to and from json`() {
        Json.testSerializer(LocalTime.now(), LocalTimeSerializer)
    }

    @Test
    fun `Serialize LocalTime to and from protobuf`() {
        ProtoBuf.testSerializer(LocalTime.now(), LocalTimeSerializer)
    }

    @Test
    fun `Serialize LocalTime to and from bson`() {
        KBson.default.testSerializer(
            LocalTimeWrapper(LocalTime.now()),
            LocalTimeWrapper.serializer()
        )
    }
}
