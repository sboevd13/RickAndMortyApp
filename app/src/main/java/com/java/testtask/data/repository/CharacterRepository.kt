package com.java.testtask.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.java.testtask.data.local.AppDatabase
import com.java.testtask.data.models.Character
import com.java.testtask.data.remote.RickAndMortyApiService
import com.java.testtask.ui.characters.CharacterFilters
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val apiService: RickAndMortyApiService,
    private val database: AppDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getCharacters(query: String, filters: CharacterFilters): Flow<PagingData<Character>> {
        val pagingSourceFactory = {
            database.characterDao().pagingSource(
                query = query,
                status = filters.status,
                gender = filters.gender,
                species = filters.species
            )
        }
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = CharacterRemoteMediator(apiService, database, query, filters),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun clearCache() {
        database.withTransaction {
            database.characterDao().clearAll()
            database.remoteKeysDao().clearRemoteKeys()
        }
    }

    suspend fun getCharacterDetails(id: Int): Result<Character> {
        return try {
            val networkCharacter = apiService.getCharacterDetails(id)
            database.characterDao().insertAll(listOf(networkCharacter))
            Result.success(networkCharacter)
        } catch (e: Exception) {
            val cachedCharacter = database.characterDao().getCharacterById(id)
            if (cachedCharacter != null) {
                Result.success(cachedCharacter)
            } else {
                Result.failure(e)
            }
        }
    }
}