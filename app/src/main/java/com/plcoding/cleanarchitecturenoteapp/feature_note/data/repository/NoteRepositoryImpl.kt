package com.plcoding.cleanarchitecturenoteapp.feature_note.data.repository

import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDao
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.mapper.toNote
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.mapper.toNoteDb
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform


// the implementation of our bluePrint in this case for db

// repositories are not used directly into the viewModel but used and injected in useCases
class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    /*In general, you should use map when you want to transform each emitted value into a new value
    , and transform when you want to transform each emitted value into a new flow.*/

    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes().map { noteDbList ->
            noteDbList.map { noteDb ->
                noteDb.toNote()
            }
        }
    }

    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)?.toNote()
    }

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note.toNoteDb())
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note.toNoteDb())
    }
}