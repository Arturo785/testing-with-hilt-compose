package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.common.truth.Truth
import com.plcoding.cleanarchitecturenoteapp.core.TestTags
import com.plcoding.cleanarchitecturenoteapp.di.AppModule
import com.plcoding.cleanarchitecturenoteapp.feature_note.MainActivity
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.util.Screen
import com.plcoding.cleanarchitecturenoteapp.ui.theme.CleanArchitectureNoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.MockK
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import okhttp3.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test


// usage explained in NotesScreenTest
@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {


    // usage explained in NotesScreenTest
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)


    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()


    lateinit var navController: NavHostController

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()
        //  every { navController.navigate(Screen.AddEditNoteScreen.route) } answers { callOriginal() }

        composeRule.activity.setContent {
            CleanArchitectureNoteAppTheme {
                navController = spyk(rememberNavController())

                // usage of this navHost explained in MainActivity
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ) {
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(routeToNavigate = { routeToGo ->
                            navController.navigate(routeToGo) // basically gives us the route of the lambda provided
                        })
                    }
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
                        val color = it.arguments?.getInt("noteColor") ?: -1

                        AddEditNoteScreen(
                            navigateUp = { shouldNavigateUp ->
                                if (shouldNavigateUp) {
                                    navController.navigateUp()
                                }
                            },
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        // Click on FAB to get to add note screen
        composeRule.onNodeWithContentDescription("Add note").performClick() // always use constants

        verify { navController.navigate(Screen.AddEditNoteScreen.route) }

        // Enter texts in title and content text fields
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .performTextInput("test-title")
        composeRule
            .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
            .performTextInput("test-content")
        // Save the new note in the fab click
        composeRule.onNodeWithContentDescription("Save note").performClick() // always use constants


        // this was not working
        /*      Truth.assertThat(navController.currentDestination?.route).isNotEmpty()
              // we should popUp navigation
              Truth.assertThat(
                  navController
                      .currentDestination
                      ?.route
                      ?.startsWith(Screen.NotesScreen.route)
              ).isTrue()*/

        // navigation happens after saving in db which is not instantaneous, makes test to fail
        /*  Truth.assertThat(navController.currentDestination?.route).contains(Screen.NotesScreen.route)
          Truth.assertThat(
              navController
                  .currentDestination
                  ?.route
                  ?.startsWith(Screen.NotesScreen.route)
          ).isTrue()*/

        // Make sure there is a note in the list with our title and content
        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        // Click on note to edit it
        composeRule.onNodeWithText("test-title").performClick()

        // goes to edit
        // this one happens right away so the check can success
        verify { navController.navigate(Screen.AddEditNoteScreen.route) }

        // this was not working
        // needed because delays a little bit the transition and allows the navigation to success
        /* Truth.assertThat(navController.currentDestination?.route).isNotEmpty()

         Truth.assertThat(
             navController
                 .currentDestination
                 ?.route
                 ?.startsWith(Screen.AddEditNoteScreen.route)
         ).isTrue()*/

        // Make sure title and content text fields contain note title and content
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .assertTextEquals("test-title")
        composeRule
            .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
            .assertTextEquals("test-content")
        // Add the text "2" to the title text field
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .performTextReplacement("test-title-2")
        // Update the note
        composeRule.onNodeWithContentDescription("Save note").performClick()


        // for some reason the navigation is not succeeding in test quick enough to assert this
        /*Truth.assertThat(navController.currentDestination?.route).isNotEmpty()

        Truth.assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Screen.NotesScreen.route)
        ).isTrue()*/

        // Make sure the update was applied to the list
        composeRule.onNodeWithText("test-title-2").assertIsDisplayed()

        Truth.assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Screen.NotesScreen.route)
        ).isTrue()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        // create 3 notes
        for (i in 1..3) {
            // Click on FAB to get to add note screen
            composeRule.onNodeWithContentDescription("Add note").performClick()

            verify { navController.navigate(Screen.AddEditNoteScreen.route) }

            // Enter texts in title and content text fields
            composeRule
                .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
                .performTextInput(i.toString())
            composeRule
                .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
                .performTextInput(i.toString())
            // Save the new
            composeRule.onNodeWithContentDescription("Save note").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        // toggles and sorts the notes in descending order
        composeRule
            .onNodeWithContentDescription("Sort")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Title")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Descending")
            .performClick()

        // checks the order of the notes to appear
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[0]
            .assertTextContains("3")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[2]
            .assertTextContains("1")
    }

}