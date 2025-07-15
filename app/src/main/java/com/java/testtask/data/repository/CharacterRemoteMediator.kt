package com.java.testtask.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.java.testtask.data.local.AppDatabase
import com.java.testtask.data.models.Character
import com.java.testtask.data.models.RemoteKeys
import com.java.testtask.data.remote.RickAndMortyApiService
import com.java.testtask.ui.characters.CharacterFilters
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val apiService: RickAndMortyApiService,
    private val database: AppDatabase,
    private val query: String,
    private val filters: CharacterFilters
) : RemoteMediator<Int, Character>() {

    private val characterDao = database.characterDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Character>): MediatorResult {
        return try {
            val page = when (loadType) {
                // REFRESH вызывается при первом запуске и при Pull-to-Refresh
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }

            val response = apiService.getCharacters(
                page = page,
                name = query.ifEmpty { null },
                status = filters.status.ifEmpty { null },
                gender = filters.gender.ifEmpty { null },
                species = filters.species.ifEmpty { null }
            )
            val characters = response.results
            val endOfPaginationReached = response.info.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    characterDao.clearAll()
                    remoteKeysDao.clearRemoteKeys()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = characters.map {
                    RemoteKeys(characterId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys)
                characterDao.insertAll(characters)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Character>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character -> remoteKeysDao.remoteKeysCharacterId(character.id) }
    }
}