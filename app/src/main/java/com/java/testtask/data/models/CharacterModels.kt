package com.java.testtask.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Модель для ответа API
data class CharacterListResponse(
    val info: PageInfo,
    val results: List<Character>
)

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

// Основная модель данных, используется и для Room, и для Retrofit
@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    @SerializedName("image")
    val imageUrl: String,
    val episode: List<String>
)

data class Origin(
    val name: String,
    val url: String
)

data class Location(
    val name: String,
    val url: String
)

// Вспомогательная сущность для Room для хранения ключей пагинации
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val characterId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)