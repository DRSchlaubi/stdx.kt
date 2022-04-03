@file:JvmName("UtilsJvmKt")

import com.github.jershell.kbson.KBson
import kotlinx.serialization.KSerializer

fun <T> KBson.testSerializer(obj: T, serializer: KSerializer<T>) = testSerializer(
    obj,
    serializer,
    KBson::dump,
    KBson::load
)
