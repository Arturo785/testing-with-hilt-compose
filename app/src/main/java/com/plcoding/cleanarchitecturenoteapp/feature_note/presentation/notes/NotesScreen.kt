package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.plcoding.cleanarchitecturenoteapp.core.TestTags
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.components.NoteItem
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.components.OrderSection
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.util.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



/*              You don't really need to instantiate the Viewmodel in the activity. Just do 2 composables;
                one as a container for the viewmodel injection and a stateless one.
                the other one a stateless inside the container
                This way you can reuse the composables and are perfectly testables for previews and ui tests.*/
@ExperimentalAnimationApi
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    routeToNavigate: (String) -> Unit,
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    NoteScreenContent(
        state = state,
        scaffoldState = scaffoldState,
        scope = scope,
        eventToTrigger = viewModel::onEvent,
        routeToNavigate = routeToNavigate
    )
}

@Composable
fun NoteScreenContent(
    state: NotesState,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    eventToTrigger: (NotesEvent) -> Unit,
    routeToNavigate: (String) -> Unit
) {
    //this allows us to show the floating action button on top of all the stuff, like our
    // layout controlling stuff
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    routeToNavigate.invoke(Screen.AddEditNoteScreen.route)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note") // always use constants
            }
        },
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // first we show our title and the button to show the sort options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your notes",
                    style = MaterialTheme.typography.h4
                )
                // this triggers the change of state that leads to this state.isOrderSectionVisible
                IconButton(
                    onClick = {
                        eventToTrigger.invoke(NotesEvent.ToggleOrderSection)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort"
                    )
                }
            }
            // triggered by the icon button above
            // and how the animation is shown
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(), // we concatenate animations
                exit = fadeOut() + slideOutVertically()
            ) {
                // our composable we did
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .testTag(TestTags.ORDER_SECTION),
                    noteOrder = state.noteOrder,
                    onOrderChange = {
                        eventToTrigger.invoke(NotesEvent.Order(it))
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // now the time of our notes
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.notes) { note ->
                    // we create the composable of the note per each note data we receive
                    NoteItem(
                        note = note,
                        modifier = Modifier
                            .fillMaxWidth()
                            // goes to note detail with the given route
                            .clickable {
                                routeToNavigate.invoke(
                                    Screen.AddEditNoteScreen.route +
                                            "?noteId=${note.id}&noteColor=${note.color}"
                                )
                            },
                        onDeleteClick = {
                            // triggers the event and launches a snackbar able to call reinsert action
                            eventToTrigger.invoke(NotesEvent.DeleteNote(note))
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Note deleted",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    eventToTrigger.invoke(NotesEvent.RestoreNote)
                                }
                            }
                        }
                    )
                    // a little space between each card
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}







