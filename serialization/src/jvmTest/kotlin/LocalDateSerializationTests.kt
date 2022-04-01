import com.github.jershell.kbson.KBson
import dev.schlaubi.stdx.serialization.LocalDateSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.time.LocalDate
import kotlin.test.Test

/**
 * Wrapper class used for testing [LocalDate] serialization in bson.
 */
@Serializable
data class LocalDateWrapper(@Serializable(with = LocalDateSerializer::class) val value: LocalDate)

@OptIn(ExperimentalSerializationApi::class)
internal class LocalDateSerializationTests {

    @Test
    fun `Serialize LocalDate to and from json`() {
        Json.testSerializer(LocalDate.now(), LocalDateSerializer)
    }

    @Test
    fun `Serialize LocalDate to and from protobuf`() {
        ProtoBuf.testSerializer(LocalDate.now(), LocalDateSerializer)
    }

    @Test
    fun `Serialize LocalDate to and from bson`() {
        KBson.default.testSerializer(
            LocalDateWrapper(LocalDate.now()), LocalDateWrapper.serializer()
        )
    }
}
