package com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteDb(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null
)