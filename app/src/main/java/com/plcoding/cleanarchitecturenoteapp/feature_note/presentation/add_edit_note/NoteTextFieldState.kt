package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note



// represents the state of the UI and acts like a holder of the current UI
data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)