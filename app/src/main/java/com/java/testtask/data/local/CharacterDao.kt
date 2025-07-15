package com.java.testtask.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.java.testtask.data.models.Character

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Character>)

    @Query("SELECT * FROM characters WHERE " +
            "(:query = '' OR name LIKE '%' || :query || '%') AND " +
            "(:status = '' OR status LIKE :status) AND " +
            "(:gender = '' OR gender LIKE :gender) AND " +
            "(:species = '' OR species LIKE '%' || :species || '%')")
    fun pagingSource(query: String, status: String, gender: String, species: String): PagingSource<Int, Character>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): Character?

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}