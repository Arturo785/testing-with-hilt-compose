package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plcoding.cleanarchitecturenoteapp.core.TestTags
import com.plcoding.cleanarchitecturenoteapp.di.AppModule
import com.plcoding.cleanarchitecturenoteapp.feature_note.MainActivity
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.util.Screen
import com.plcoding.cleanarchitecturenoteapp.ui.theme.CleanArchitectureNoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test


// is our entry point to inject dependencies
// we don't want normal dependencies in here because they contain real repositories, real database
// and real api calls that is why we create our own hilt module for testing purposes
@HiltAndroidTest
@UninstallModules(AppModule::class) // we say to not use this module and get rid of it for this class
class NotesScreenTest {

    // makes our hiltConfiguration works
    @get:Rule(order = 0) // this rule goes first
    val hiltRule = HiltAndroidRule(this)

    // also exists   createComposeRule() and this provides an automatic activity to test
    @get:Rule(order = 1) // then this one goes
    val composeRule =
        createAndroidComposeRule<MainActivity>() // we just use our MainActivity as bluePrint, contains @AndroidEntryPoint


    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject() // injects the dependencies
        // populates our screen
        composeRule.activity.setContent {
            val navController = rememberNavController()

            // our theme
            CleanArchitectureNoteAppTheme {
                // we create a navHost just as our real app
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route // define which screen we want to show
                ) {
                    // creates that screen
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(routeToNavigate = {
                            // not necessary in this case
                        })
                    }
                }
            }
        }
    }

    // no order visible at first then toggles it
    // we are able to use TestTags because we are inside the module which basically
    // contains source, test and android test, same module
    @Test
    fun clickToggleOrderSection_isVisible() {
        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertIsDisplayed()
    }

}