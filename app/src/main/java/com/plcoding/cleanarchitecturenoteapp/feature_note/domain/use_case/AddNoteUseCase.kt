package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository

// useCases get repositories injected and the useCases are used in the viewModels, not the
// repositories directly in the viewModel

// useCases are basically single actions that can be performed by the user,
// and should have only a single responsibility, no more
// can have utility functions inside but private, not available to outside, only visible the invoke


class AddNoteUseCase(
    private val repository: NoteRepository
) {

    // it's the job of the useCase to make validations of the action, not the viewModel
    // we override the invoke
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) {
            throw InvalidNoteException("The title of the note can't be empty.")
        }
        if (note.content.isBlank()) {
            throw InvalidNoteException("The content of the note can't be empty.")
        }
        repository.insertNote(note)
    }
}