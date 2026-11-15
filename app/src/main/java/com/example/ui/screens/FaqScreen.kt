package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.FAQSection
import com.example.ui.components.FaqItem
import com.example.ui.components.Footer

private val FAQ_ITEMS = listOf(
  FaqItem(
    question = "How does AI background removal work?",
    answer = "When you tap Remove Background, your selected photo is sent securely over HTTPS to the configured remove.bg AI segmentation endpoint. The model separates the primary foreground subject from the backdrop and returns a clean alpha-masked image.",
  ),
  FaqItem(
    question = "Which image formats are supported?",
    answer = "BGRemover Pro validates and supports JPG, JPEG, PNG, and WebP images up to 12 MB in file size.",
  ),
  FaqItem(
    question = "Is BGRemover Pro free?",
    answer = "The app itself requires no account or payment gateway. Image processing credits depend on the Free or paid quota associated with your configured background removal API key; see the Pricing tab for tier details.",
  ),
  FaqItem(
    question = "Can I download transparent PNG files?",
    answer = "Yes. Every successful result is returned as a genuine 32-bit RGBA PNG with transparency preserved. You can save it directly to Pictures/BGRemover Pro on your device or share it to other apps.",
  ),
  FaqItem(
    question = "Are uploaded images stored?",
    answer = "BGRemover Pro does not maintain a permanent database or cloud gallery of your photos—images are held only temporarily in memory/cache during your session and transmitted directly to the configured background removal API provider to process your request.",
  ),
  FaqItem(
    question = "What happens if background removal fails?",
    answer = "If API credentials are missing, your credit limit is reached, the file is unsupported, or a network/rate-limit error occurs, the app displays an explicit error message and lets you retry or choose another image. It never fakes a cutout result.",
  ),
)

@Composable
fun FaqScreen(
  onNavigateStudio: () -> Unit,
  onNavigatePricing: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 760.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          text = "Frequently Asked Questions",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = "Clear answers on supported formats, transparent PNG downloads, and how your images are handled.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        FAQSection(items = FAQ_ITEMS)

        Spacer(modifier = Modifier.height(6.dp))

        Footer(
          onNavigateHome = onNavigateStudio,
          onNavigateRemover = onNavigateStudio,
          onNavigatePricing = onNavigatePricing,
        )
      }
    }
  }
}
