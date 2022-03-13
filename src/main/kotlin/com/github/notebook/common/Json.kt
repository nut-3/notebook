package com.github.notebook.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val JsonMapper = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

inline fun <reified T> deSerialize(string: String): T = JsonMapper.decodeFromString(string)
inline fun <reified T> serialize(value: T): String = JsonMapper.encodeToString(value)

class LocalDateTimeAsStringSerializer : KSerializer<LocalDateTime> {

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return LocalDateTime.parse(string)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ISO_DATE_TIME", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val string = value.format(DateTimeFormatter.ISO_DATE_TIME)
        encoder.encodeString(string)
    }
}