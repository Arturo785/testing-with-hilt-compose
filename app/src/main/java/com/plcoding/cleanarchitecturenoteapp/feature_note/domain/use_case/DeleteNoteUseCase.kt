package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository


// useCases get repositories injected and the useCases are used in the viewModels, not the
// repositories directly in the viewModel

// useCases are basically single actions that can be performed by the user,
// and should have only a single responsibility, no more
// can have utility functions inside but private, not available to outside, only visible the invoke

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {

    // we override the invoke
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}