package com.plcoding.cleanarchitecturenoteapp.feature_note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.util.Screen
import com.plcoding.cleanarchitecturenoteapp.ui.theme.CleanArchitectureNoteAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanArchitectureNoteAppTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    // our navController
                    val navController = rememberNavController()

                    // our navController to navigate in the different screens
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
                    ) {

                        // our initial screen
                        composable(route = Screen.NotesScreen.route) {
                            /*You don't really need to instantiate the Viewmodel in the activity. Just do 2 composables;
                               one as a container for the viewmodel injection and a stateless one.
                               the other one a stateless inside the container
                               This way you can reuse the composables and are perfectly testables for previews and ui tests.*/

                            NotesScreen(routeToNavigate = { newRoute ->
                                navController.navigate(newRoute)
                            })
                        }

                        // our composable with the given route and the params in the route
                        // like a webPage URL
                        composable(
                            route = Screen.AddEditNoteScreen.route +
                                    "?noteId={noteId}&noteColor={noteColor}",
                            arguments = listOf(
                                navArgument(
                                    name = "noteId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument(
                                    name = "noteColor"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                            )
                        ) {

                            /*You don't really need to instantiate the Viewmodel in the activity. Just do 2 composables;
                                one as a container for the viewmodel injection and a stateless one.
                                the other one a stateless inside the container
                                This way you can reuse the composables and are perfectly testables for previews and ui tests.*/

                            val color = it.arguments?.getInt("noteColor") ?: -1
                            AddEditNoteScreen(noteColor = color, navigateUp = { shouldNavigateUp ->
                                if (shouldNavigateUp) {
                                    navController.navigateUp()
                                }
                            })
                        }
                    }

                }
            }
        }
    }
}
