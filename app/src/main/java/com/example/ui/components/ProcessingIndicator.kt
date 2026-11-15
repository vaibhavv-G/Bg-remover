package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.VioletPrimary

@Composable
fun ProcessingIndicator(
  originalBitmap: Bitmap,
  fileName: String,
  modifier: Modifier = Modifier,
) {
  val imageBitmap = remember(originalBitmap) { originalBitmap.asImageBitmap() }
  val ratio = remember(originalBitmap) {
    (originalBitmap.width.toFloat() / originalBitmap.height.coerceAtLeast(1).toFloat())
      .coerceIn(0.65f, 1.8f)
  }

  val infiniteTransition = rememberInfiniteTransition(label = "ai_scan_transition")
  val scanProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scan_line",
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("processing_indicator_card"),
    shape = RoundedCornerShape(22.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 4.dp,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
        .padding(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(ratio)
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
      ) {
        Image(
          bitmap = imageBitmap,
          contentDescription = "Processing image",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize(),
          alpha = 0.5f,
        )

        // Animated AI scan beam
        Canvas(modifier = Modifier.fillMaxSize()) {
          val y = size.height * scanProgress
          val glowBrush = Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              VioletPrimary.copy(alpha = 0.25f),
              BlueAccent.copy(alpha = 0.65f),
              VioletPrimary.copy(alpha = 0.25f),
              Color.Transparent,
            ),
            startY = (y - 56f).coerceAtLeast(0f),
            endY = (y + 56f).coerceAtMost(size.height),
          )
          drawRect(brush = glowBrush)
          drawLine(
            brush = Brush.horizontalGradient(listOf(VioletPrimary, BlueAccent)),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 4.dp.toPx(),
          )
        }

        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
          shadowElevation = 6.dp,
        ) {
          Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(42.dp),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.primaryContainer,
              strokeWidth = 3.5.dp,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "AI is Removing the Background…",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Securely processing \"$fileName\" via background removal API",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  }
}
