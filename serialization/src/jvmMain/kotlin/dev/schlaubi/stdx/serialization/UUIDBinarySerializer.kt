package dev.schlaubi.stdx.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.util.*

public object UUIDBinarySerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UUID") {
        element<Long>("mostSignificantBits")
        element<Long>("leastSignificantBits")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): UUID = decoder.decodeStructure(descriptor) {
        var mostSignificantBits: Long? = null
        var leastSignificantBits: Long? = null
        if (decodeSequentially()) {
            mostSignificantBits = decodeLongElement(descriptor, 0)
            leastSignificantBits = decodeLongElement(descriptor, 1)
        } else while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> mostSignificantBits = decodeLongElement(descriptor, 0)
                1 -> leastSignificantBits = decodeLongElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        requireNotNull(mostSignificantBits)
        requireNotNull(leastSignificantBits)
        UUID(mostSignificantBits, leastSignificantBits)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.mostSignificantBits)
            encodeLongElement(descriptor, 1, value.leastSignificantBits)
        }
    }
}
