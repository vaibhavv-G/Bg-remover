package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.BackgroundRemovalRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BGRemover Pro", appName)
  }

  @Test
  fun `repository reports unconfigured when placeholder api key is used`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BackgroundRemovalRepository(context)
    assertFalse(repository.isServiceConfigured())
    assertEquals(
      "Background removal service is not configured yet. Please configure your API credentials.",
      BackgroundRemovalRepository.NOT_CONFIGURED_MESSAGE,
    )
  }

  @Test
  fun `studio and navigation tabs render key sections`() {
    composeTestRule.onNodeWithText("Remove Image Backgrounds in Seconds.").assertIsDisplayed()
    composeTestRule.onNodeWithTag("upload_area_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("take_photo_button").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("batch_photos_button").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_how_it_works").performClick()
    composeTestRule.onNodeWithText("1. Upload Your Image").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_pricing").performClick()
    composeTestRule.onNodeWithText("₹99").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Batch background removal (up to 5 images)")
      .performScrollTo()
      .assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_faq").performClick()
    composeTestRule.onNodeWithText("Frequently Asked Questions").performScrollTo().assertIsDisplayed()
  }
}
