package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.BackdropMode
import com.example.ui.ResultViewMode
import kotlin.math.roundToInt

@Composable
fun ComparisonSlider(
  originalBitmap: Bitmap,
  processedBitmap: Bitmap,
  backdropMode: BackdropMode,
  viewMode: ResultViewMode,
  onBackdropChange: (BackdropMode) -> Unit,
  onViewModeChange: (ResultViewMode) -> Unit,
  modifier: Modifier = Modifier,
) {
  val originalImage = remember(originalBitmap) { originalBitmap.asImageBitmap() }
  val processedImage = remember(processedBitmap) { processedBitmap.asImageBitmap() }
  val ratio = remember(processedBitmap) {
    (processedBitmap.width.toFloat() / processedBitmap.height.coerceAtLeast(1).toFloat())
      .coerceIn(0.65f, 1.8f)
  }

  var sliderFraction by remember { mutableFloatStateOf(0.5f) }
  var zoomScale by remember { mutableFloatStateOf(1f) }
  var panOffset by remember { mutableStateOf(Offset.Zero) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("comparison_slider_card"),
    shape = RoundedCornerShape(22.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 4.dp,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
        .padding(16.dp),
    ) {
      // View mode selector: Compare / Result / Original
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        ViewModePill(
          label = "Compare",
          selected = viewMode == ResultViewMode.COMPARE,
          onClick = { onViewModeChange(ResultViewMode.COMPARE) },
          modifier = Modifier
            .weight(1f)
            .testTag("view_mode_compare"),
        )
        ViewModePill(
          label = "Result",
          selected = viewMode == ResultViewMode.RESULT,
          onClick = { onViewModeChange(ResultViewMode.RESULT) },
          modifier = Modifier
            .weight(1f)
            .testTag("view_mode_result"),
        )
        ViewModePill(
          label = "Original",
          selected = viewMode == ResultViewMode.ORIGINAL,
          onClick = { onViewModeChange(ResultViewMode.ORIGINAL) },
          modifier = Modifier
            .weight(1f)
            .testTag("view_mode_original"),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Interactive Preview Canvas
      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(ratio)
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
          .clipToBounds(),
      ) {
        val boxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val boxHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

        // Backdrop Layer
        when {
          viewMode == ResultViewMode.ORIGINAL -> {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
            )
          }
          backdropMode == BackdropMode.CHECKERBOARD -> {
            CheckerboardBackground(modifier = Modifier.fillMaxSize())
          }
          backdropMode == BackdropMode.LIGHT -> {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
            )
          }
          backdropMode == BackdropMode.DARK -> {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
            )
          }
        }

        val transformModifier = Modifier
          .fillMaxSize()
          .pointerInput(viewMode) {
            detectTransformGestures { _, pan, zoom, _ ->
              val nextScale = (zoomScale * zoom).coerceIn(1f, 4f)
              zoomScale = nextScale
              if (nextScale <= 1.01f) {
                panOffset = Offset.Zero
              } else {
                val maxPanX = (boxWidthPx * (nextScale - 1f)) / 2f
                val maxPanY = (boxHeightPx * (nextScale - 1f)) / 2f
                panOffset = Offset(
                  x = (panOffset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                  y = (panOffset.y + pan.y).coerceIn(-maxPanY, maxPanY),
                )
              }
            }
          }
          .graphicsLayer {
            scaleX = zoomScale
            scaleY = zoomScale
            translationX = panOffset.x
            translationY = panOffset.y
          }

        Box(modifier = transformModifier) {
          when (viewMode) {
            ResultViewMode.ORIGINAL -> {
              Image(
                bitmap = originalImage,
                contentDescription = "Original image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
              )
            }
            ResultViewMode.RESULT -> {
              Image(
                bitmap = processedImage,
                contentDescription = "Background removed transparent image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
              )
            }
            ResultViewMode.COMPARE -> {
              // Full processed result underneath (right side = After)
              Image(
                bitmap = processedImage,
                contentDescription = "After background removal",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
              )
              // Clipped original image on left side (= Before)
              Image(
                bitmap = originalImage,
                contentDescription = "Before background removal",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                  .fillMaxSize()
                  .clip(FractionWidthShape(sliderFraction)),
              )
            }
          }
        }

        // Before / After corner labels and draggable divider
        if (viewMode == ResultViewMode.COMPARE) {
          CornerBadge(
            text = "Before",
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(10.dp),
          )
          CornerBadge(
            text = "After (PNG)",
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(10.dp),
          )

          val dividerX = (boxWidthPx * sliderFraction).roundToInt()
          val handleHalfSizePx = with(LocalDensity.current) { 24.dp.toPx() }

          // Vertical divider line
          Box(
            modifier = Modifier
              .offset { IntOffset(dividerX - 1, 0) }
              .width(2.dp)
              .fillMaxHeight()
              .background(Color.White.copy(alpha = 0.95f))
          )

          // Draggable handle (48dp touch target)
          Box(
            modifier = Modifier
              .offset {
                IntOffset(
                  x = (dividerX - handleHalfSizePx).roundToInt(),
                  y = (boxHeightPx / 2f - handleHalfSizePx).roundToInt(),
                )
              }
              .size(48.dp)
              .pointerInput(boxWidthPx) {
                detectHorizontalDragGestures { change, dragAmount ->
                  change.consume()
                  if (boxWidthPx > 0f) {
                    sliderFraction = (sliderFraction + dragAmount / boxWidthPx).coerceIn(0.05f, 0.95f)
                  }
                }
              }
              .testTag("comparison_drag_handle"),
            contentAlignment = Alignment.Center,
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              shadowElevation = 6.dp,
              modifier = Modifier
                .size(38.dp)
                .border(2.dp, Color.White, CircleShape),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Rounded.CompareArrows,
                  contentDescription = "Drag to compare before and after",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Backdrop & Zoom Control Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        // Backdrop toggles (Checkerboard / Light / Dark)
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          BackdropChip(
            icon = Icons.Rounded.GridOn,
            contentDescription = "Checkerboard transparency background",
            selected = backdropMode == BackdropMode.CHECKERBOARD,
            onClick = { onBackdropChange(BackdropMode.CHECKERBOARD) },
            modifier = Modifier.testTag("backdrop_checkerboard"),
          )
          BackdropChip(
            icon = Icons.Rounded.LightMode,
            contentDescription = "Light preview background",
            selected = backdropMode == BackdropMode.LIGHT,
            onClick = { onBackdropChange(BackdropMode.LIGHT) },
            modifier = Modifier.testTag("backdrop_light"),
          )
          BackdropChip(
            icon = Icons.Rounded.DarkMode,
            contentDescription = "Dark preview background",
            selected = backdropMode == BackdropMode.DARK,
            onClick = { onBackdropChange(BackdropMode.DARK) },
            modifier = Modifier.testTag("backdrop_dark"),
          )
        }

        // Zoom controls
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          IconButton(
            onClick = {
              zoomScale = (zoomScale - 0.5f).coerceAtLeast(1f)
              if (zoomScale <= 1.01f) panOffset = Offset.Zero
            },
            enabled = zoomScale > 1f,
            modifier = Modifier
              .size(48.dp)
              .testTag("zoom_out_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Remove,
              contentDescription = "Zoom out",
              modifier = Modifier.size(18.dp),
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
          ) {
            Text(
              text = "${(zoomScale * 100).roundToInt()}%",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
          }

          IconButton(
            onClick = { zoomScale = (zoomScale + 0.5f).coerceAtMost(4f) },
            enabled = zoomScale < 4f,
            modifier = Modifier
              .size(48.dp)
              .testTag("zoom_in_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Add,
              contentDescription = "Zoom in",
              modifier = Modifier.size(18.dp),
            )
          }

          if (zoomScale > 1f) {
            IconButton(
              onClick = {
                zoomScale = 1f
                panOffset = Offset.Zero
              },
              modifier = Modifier
                .size(48.dp)
                .testTag("zoom_reset_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.RestartAlt,
                contentDescription = "Reset zoom",
                modifier = Modifier.size(18.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CheckerboardBackground(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val tileSize = 12.dp.toPx()
    val cols = (size.width / tileSize).toInt() + 1
    val rows = (size.height / tileSize).toInt() + 1
    val lightTile = Color(0xFFFFFFFF)
    val darkTile = Color(0xFFE2E8F0)

    for (row in 0..rows) {
      for (col in 0..cols) {
        val isLight = (row + col) % 2 == 0
        drawRect(
          color = if (isLight) lightTile else darkTile,
          topLeft = Offset(col * tileSize, row * tileSize),
          size = Size(tileSize, tileSize),
        )
      }
    }
  }
}

@Composable
private fun ViewModePill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier
      .height(40.dp)
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(10.dp),
    color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
    shadowElevation = if (selected) 2.dp else 0.dp,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.onSurfaceVariant
        },
      )
    }
  }
}

@Composable
private fun BackdropChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDescription: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier
      .size(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    color = if (selected) {
      MaterialTheme.colorScheme.primaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceVariant
    },
    border = if (selected) {
      androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    } else {
      null
    },
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (selected) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier.size(20.dp),
      )
    }
  }
}

@Composable
private fun CornerBadge(
  text: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFF0F172A).copy(alpha = 0.72f),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = Color.White,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    )
  }
}

private class FractionWidthShape(private val fraction: Float) : Shape {
  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density,
  ): Outline {
    val clamped = fraction.coerceIn(0f, 1f)
    return Outline.Rectangle(Rect(0f, 0f, size.width * clamped, size.height))
  }
}
