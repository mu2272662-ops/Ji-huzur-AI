package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LearnedMemory
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnedMemoryDao {
    @Query("SELECT * FROM learned_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<LearnedMemory>>

    @Query("SELECT * FROM learned_memories ORDER BY timestamp DESC")
    suspend fun getMemoriesList(): List<LearnedMemory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: LearnedMemory): Long

    @Query("DELETE FROM learned_memories WHERE `key` = :key")
    suspend fun deleteMemoryByKey(key: String)

    @Query("DELETE FROM learned_memories")
    suspend fun clearMemories()
}
