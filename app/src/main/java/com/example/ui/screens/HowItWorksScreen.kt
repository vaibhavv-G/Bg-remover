package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.Footer
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.VioletPrimary

@Composable
fun HowItWorksScreen(
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
          text = "How It Works",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = "Three focused steps from raw photo to a production-ready transparent PNG.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        StepCard(
          stepNumber = "01",
          title = "1. Upload Your Image",
          description = "Pick any JPG, JPEG, PNG, or WebP photo from your gallery or device files up to 12 MB. Preview dimensions and file details before sending.",
          icon = Icons.Rounded.CloudUpload,
        )

        StepCard(
          stepNumber = "02",
          title = "2. AI Removes the Background",
          description = "Your image is sent securely over HTTPS to the configured remove.bg AI endpoint, cleanly separating foreground subjects, hair, and product edges.",
          icon = Icons.Rounded.AutoFixHigh,
        )

        StepCard(
          stepNumber = "03",
          title = "3. Download Your Transparent Image",
          description = "Inspect the cutout over checkerboard, light, or dark backdrops with zoom and a before/after slider, then export a genuine transparent PNG.",
          icon = Icons.Rounded.Download,
        )

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.HighQuality,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "True Alpha-Channel PNG Export",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Every exported file preserves full 32-bit RGBA transparency—ready for e-commerce listings, thumbnails, presentations, and design tools.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        Button(
          onClick = onNavigateStudio,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("how_it_works_try_now_button"),
        ) {
          Text(
            text = "Try Background Remover Now",
            style = MaterialTheme.typography.labelLarge,
          )
        }

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

@Composable
private fun StepCard(
  stepNumber: String,
  title: String,
  description: String,
  icon: ImageVector,
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    shadowElevation = 2.dp,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.Top,
    ) {
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(15.dp))
          .background(Brush.linearGradient(listOf(VioletPrimary, BlueAccent))),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(26.dp),
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "STEP $stepNumber",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
