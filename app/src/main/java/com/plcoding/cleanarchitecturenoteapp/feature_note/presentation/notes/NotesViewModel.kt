package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    // the one presented to the UI
    // the state as it says is the state of the UI and holder of UI data
    // compose stuff
    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    // keeps track of the last note deleted
    private var recentlyDeletedNote: Note? = null

    // our job to debounce the action
    private var getNotesJob: Job? = null

    // we init the retrieval of data
    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    // it's the job of the useCase to make validations of the action, not the viewModel

    // the events are thing triggered by the user that alter the state of the app
    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                val currentOrderInstance = state.value.noteOrder::class
                val newOrderInstance = event.noteOrder::class
                //if the current order is the same as the one received do nothing
                if (currentOrderInstance == newOrderInstance &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                // get notes with the new order
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                // deletes and sets the last deleted one
                viewModelScope.launch {
                    noteUseCases.deleteNoteUseCase.invoke(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                // reinserts the node
                viewModelScope.launch {
                    // Throws exception, check
                    try {
                        noteUseCases.addNoteUseCase.invoke(recentlyDeletedNote ?: return@launch)
                        // if is null do not do nothing
                        recentlyDeletedNote = null // resets
                    } catch (e: InvalidNoteException) {
                        Log.d("NotesViewModel", "onEvent: $e")
                        // inform user
                    }
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                // all stays the same but we change the boolean of visibility
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        // this create a new flow every time is called, that is why we cancel the old coroutine and
        // job and we start a new one to observe the new changes of the db
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotesUseCase(noteOrder)
            .onEach { notes ->
                // we set the new value to our state
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope) // because of the on each we need a coroutine to the flow
        // job be launched in
    }
}