import com.github.jershell.kbson.KBson
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlin.test.assertEquals

/**
 * Signature of a function of [SerialFormat] which encodes [T] to [Serialized].
 *
 * @param Serialized the type of the formats serialized value.
 */
typealias Encoder<Format, Serialized, T> = Format.(serializer: KSerializer<T>, obj: T) -> Serialized

/**
 * Signature of a function of [SerialFormat] which decodes [Serialized] to [T].
 *
 * @param Serialized the type of the formats serialized value.
 */
typealias Decoder<Format, Serialized, T> = Format.(serializer: KSerializer<T>, serialized: Serialized) -> T

fun <Format : SerialFormat, Serialized, T> Format.testSerializer(
    obj: T,
    serializer: KSerializer<T>,
    encoder: Encoder<Format, Serialized, T>,
    decoder: Decoder<Format, Serialized, T>
) {
    val serialized = encoder(serializer, obj)
    val deserialized = decoder(serializer, serialized)

    assertEquals(obj, deserialized, "Deserialized value must be equal to original value")
}

fun <T> BinaryFormat.testSerializer(obj: T, serializer: KSerializer<T>) = testSerializer(
    obj,
    serializer,
    BinaryFormat::encodeToByteArray,
    BinaryFormat::decodeFromByteArray
)

fun <T> StringFormat.testSerializer(obj: T, serializer: KSerializer<T>) = testSerializer(
    obj,
    serializer,
    StringFormat::encodeToString,
    StringFormat::decodeFromString
)

fun <T> KBson.testSerializer(obj: T, serializer: KSerializer<T>) = testSerializer(
    obj,
    serializer,
    KBson::dump,
    KBson::load
)
