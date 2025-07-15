package com.java.testtask.data.local

import androidx.room.TypeConverter
import com.java.testtask.data.models.Location
import com.java.testtask.data.models.Origin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromOrigin(origin: Origin): String {
        return gson.toJson(origin)
    }

    @TypeConverter
    fun toOrigin(originString: String): Origin {
        return gson.fromJson(originString, Origin::class.java)
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        return gson.fromJson(locationString, Location::class.java)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}