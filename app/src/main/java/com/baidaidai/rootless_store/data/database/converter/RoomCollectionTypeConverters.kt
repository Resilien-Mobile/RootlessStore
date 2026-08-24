package com.baidaidai.rootless_store.data.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomCollectionTypeConverters {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun encodeStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun decodeStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun encodeStringMap(value: Map<String, String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun decodeStringMap(value: String): Map<String, String> {
        return json.decodeFromString(value)
    }
}
