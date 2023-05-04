package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case


// All our useCases to clean up a little bit when injecting into the viewModels
data class NoteUseCases(
    val getNotesUseCase: GetNotesUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val addNoteUseCase: AddNoteUseCase,
    val getNoteUseCase: GetNoteUseCase
)