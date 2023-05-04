package com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // it's type flow to be reactive to changes of the db
    @Query("SELECT * FROM NoteDb")
    fun getNotes(): Flow<List<NoteDb>>

    @Query("SELECT * FROM NoteDb WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteDb?

    // takes care also of the update if receives the same id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteDb)

    @Delete
    suspend fun deleteNote(note: NoteDb)
}