package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learned_memories")
data class LearnedMemory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,        // e.g. "NAME", "FAVORITE_DRINK", "CUSTOM_TITLE", "AI_NAME", "GENERAL_FACT"
    val originalText: String, // What user said
    val adaptedFact: String,  // How the AI understands it/will remember it
    val timestamp: Long = System.currentTimeMillis()
)
