package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.SelectedImageInfo
import java.util.Locale

@Composable
fun ImagePreview(
  imageInfo: SelectedImageInfo,
  bitmap: Bitmap,
  modifier: Modifier = Modifier,
) {
  val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
  val sizeText = remember(imageInfo.sizeBytes) { formatFileSize(imageInfo.sizeBytes) }
  val ratio = remember(bitmap) {
    (bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1).toFloat()).coerceIn(0.65f, 1.8f)
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("image_preview_card"),
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
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Rounded.Image,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = imageInfo.fileName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = "${imageInfo.width} × ${imageInfo.height} px • $sizeText",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          shape = RoundedCornerShape(999.dp),
          color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
          Text(
            text = imageInfo.formatLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

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
          contentDescription = "Selected original image preview",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }
}

private fun formatFileSize(bytes: Long): String {
  return when {
    bytes >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
    bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
    else -> "$bytes B"
  }
}
