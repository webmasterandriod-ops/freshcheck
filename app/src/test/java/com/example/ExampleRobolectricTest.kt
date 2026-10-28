package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `launch MainActivity and verify screens render without crash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FreshCheck", appName)

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_screen").assertExists()
        composeTestRule.onNodeWithTag("bottom_navigation_bar").assertExists()

        // Navigate to Pantry
        composeTestRule.onNodeWithTag("nav_tab_pantry").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pantry_screen").assertExists()

        // Navigate to Waste Log
        composeTestRule.onNodeWithTag("nav_tab_waste_log").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("waste_log_screen").assertExists()

        // Navigate to Settings
        composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_screen").assertExists()

        // Open Add Item form
        composeTestRule.onNodeWithTag("center_add_fab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("add_edit_item_screen").assertExists()
    }
}
