package com.github.notebook.common

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val JsonMapper = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

inline fun <reified T> deSerialize(string: String): T = JsonMapper.decodeFromString(string)
inline fun <reified T> serialize(value: T): String = JsonMapper.encodeToString(value)