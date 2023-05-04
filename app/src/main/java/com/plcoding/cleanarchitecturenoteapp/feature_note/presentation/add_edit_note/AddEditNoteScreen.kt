package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.Composable
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.plcoding.cleanarchitecturenoteapp.core.TestTags
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/*              You don't really need to instantiate the Viewmodel in the activity. Just do 2 composables;
                one as a container for the viewmodel injection and a stateless one.
                the other one a stateless inside the container
                This way you can reuse the composables and are perfectly testables for previews and ui tests.*/

@Composable
fun AddEditNoteScreen(
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel(),
    navigateUp: (Boolean) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()

    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value


    // means that already has a color or default one
    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )
    }
    val scope = rememberCoroutineScope()


    // let see if this approach works, may fail
/*    LaunchedEffect(key1 = true) {
        uiEvent.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navigateUp.invoke(true)
                }
            }
        }
    }*/

    AddEditNoteScreenContent(
        scaffoldState = scaffoldState,
        noteBackgroundAnimatable = noteBackgroundAnimatable,
        scope = scope,
        titleState = titleState,
        contentState = contentState,
        uiEvent = viewModel.eventFlow,
        defaultColor = viewModel.noteColor.value,
        eventToTrigger = viewModel::onEvent,
        navigateUp = navigateUp
    )

}


@Composable
fun AddEditNoteScreenContent(
    scaffoldState: ScaffoldState,
    noteBackgroundAnimatable: Animatable<Color, AnimationVector4D>,
    scope: CoroutineScope,
    titleState: NoteTextFieldState,
    contentState: NoteTextFieldState,
    uiEvent: SharedFlow<AddEditNoteViewModel.UiEvent>,
    defaultColor: Int,
    eventToTrigger: (AddEditNoteEvent) -> Unit,
    navigateUp: (Boolean) -> Unit
) {

    // let's see if it works
    LaunchedEffect(key1 = true) {
        uiEvent.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navigateUp.invoke(true)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventToTrigger.invoke(AddEditNoteEvent.SaveNote)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save note") // always use constants
            }
        },
        scaffoldState = scaffoldState
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundAnimatable.value)
                .padding(scaffoldPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Note.noteColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (defaultColor == colorInt) {
                                    Color.Black
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    noteBackgroundAnimatable.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(
                                            durationMillis = 500
                                        )
                                    )
                                }
                                eventToTrigger.invoke(AddEditNoteEvent.ChangeColor(colorInt))
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = {
                    eventToTrigger.invoke(AddEditNoteEvent.EnteredTitle(it))
                },
                onFocusChange = {
                    eventToTrigger.invoke(AddEditNoteEvent.ChangeTitleFocus(it))
                },
                isHintVisible = titleState.isHintVisible,
                singleLine = true,
                testTag = TestTags.TITLE_TEXT_FIELD,
                textStyle = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = {
                    eventToTrigger.invoke(AddEditNoteEvent.EnteredContent(it))
                },
                onFocusChange = {
                    eventToTrigger.invoke(AddEditNoteEvent.ChangeContentFocus(it))
                },
                isHintVisible = contentState.isHintVisible,
                textStyle = MaterialTheme.typography.body1,
                testTag = TestTags.CONTENT_TEXT_FIELD,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }

}