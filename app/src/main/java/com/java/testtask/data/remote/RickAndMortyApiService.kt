package com.java.testtask.data.remote

import com.java.testtask.data.models.Character
import com.java.testtask.data.models.CharacterListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("api/character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("gender") gender: String?,
        @Query("species") species: String?
    ): CharacterListResponse

    @GET("api/character/{id}")
    suspend fun getCharacterDetails(@Path("id") id: Int): Character
}