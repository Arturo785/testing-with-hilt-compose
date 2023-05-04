package com.plcoding.cleanarchitecturenoteapp.feature_note.data.mapper

import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDb
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note


// may seem unuseful with is made to not mix data stuff into domain

fun Note.toNoteDb(): NoteDb {
    return NoteDb(
        title = this.title,
        content = this.content,
        timestamp = this.timestamp,
        color = this.color,
        id = this.id
    )
}

fun NoteDb.toNote(): Note {
    return Note(
        title = this.title,
        content = this.content,
        timestamp = this.timestamp,
        color = this.color,
        id = this.id
    )
}