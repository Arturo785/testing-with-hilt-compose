package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// useCases get repositories injected and the useCases are used in the viewModels, not the
// repositories directly in the viewModel

// useCases are basically single actions that can be performed by the user,
// and should have only a single responsibility, no more
// can have utility functions inside but private, not available to outside, only visible the invoke

class GetNotesUseCase(
    private val repository: NoteRepository
) {

    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending) // by default are sorted by date descending
    ): Flow<List<Note>> {
        return repository.getNotes().map { listNotes ->
            when (noteOrder.orderType) {
                // sort by Ascending
                is OrderType.Ascending -> {
                    // once inside sort by the chosen type
                    when (noteOrder) {
                        is NoteOrder.Title -> listNotes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Date -> listNotes.sortedBy { it.timestamp }
                        is NoteOrder.Color -> listNotes.sortedBy { it.color }
                    }
                }
                // sort by Descending
                is OrderType.Descending -> {
                    // once inside sort by the chosen type
                    when (noteOrder) {
                        is NoteOrder.Title -> listNotes.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date -> listNotes.sortedByDescending { it.timestamp }
                        is NoteOrder.Color -> listNotes.sortedByDescending { it.color }
                    }
                }
            }
        }
    }
}