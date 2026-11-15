package com.example.ui.components

import android.content.ClipDescription
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.VioletPrimary

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun UploadArea(
  onBrowsePhotoPicker: () -> Unit,
  onBrowseDocumentFiles: () -> Unit,
  onTakePhoto: () -> Unit,
  onPickBatchPhotos: () -> Unit,
  onDropImageUris: (List<Uri>) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isDraggingOver by remember { mutableStateOf(false) }

  val dropTarget = remember(onDropImageUris) {
    object : DragAndDropTarget {
      override fun onEntered(event: DragAndDropEvent) {
        isDraggingOver = true
      }

      override fun onExited(event: DragAndDropEvent) {
        isDraggingOver = false
      }

      override fun onEnded(event: DragAndDropEvent) {
        isDraggingOver = false
      }

      override fun onDrop(event: DragAndDropEvent): Boolean {
        isDraggingOver = false
        val clipData = event.toAndroidDragEvent().clipData ?: return false
        val uris = buildList {
          for (i in 0 until clipData.itemCount) {
            clipData.getItemAt(i).uri?.let { add(it) }
          }
        }
        if (uris.isNotEmpty()) {
          onDropImageUris(uris)
          return true
        }
        return false
      }
    }
  }

  val borderColor = if (isDraggingOver) {
    MaterialTheme.colorScheme.secondary
  } else {
    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
  }

  val gradientBg = Brush.verticalGradient(
    colors = listOf(
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDraggingOver) 0.72f else 0.45f),
      MaterialTheme.colorScheme.secondaryContainer.copy(alpha = if (isDraggingOver) 0.55f else 0.35f),
    )
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
          val desc = event.toAndroidDragEvent().clipDescription
          desc?.hasMimeType("image/*") == true ||
            desc?.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST) == true
        },
        target = dropTarget,
      )
      .clickable(onClick = onBrowsePhotoPicker)
      .drawBehind {
        val stroke = Stroke(
          width = (if (isDraggingOver) 3.dp else 2.dp).toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f),
        )
        drawRoundRect(
          color = borderColor,
          cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
          style = stroke,
        )
      }
      .testTag("upload_area_card"),
    shape = RoundedCornerShape(24.dp),
    color = Color.Transparent,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(gradientBg)
        .padding(horizontal = 20.dp, vertical = 26.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(listOf(VioletPrimary, BlueAccent))
          ),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.CloudUpload,
          contentDescription = "Upload an image",
          tint = Color.White,
          modifier = Modifier.size(30.dp),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isDraggingOver) "Drop Image to Upload" else "Upload or Drop Your Image",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Choose a photo, snap with your camera, browse files, or select up to 5 images for batch removal.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(14.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        listOf("JPG", "JPEG", "PNG", "WebP", "Max 12 MB").forEach { tag ->
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
          ) {
            Text(
              text = tag,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = if (tag == "Max 12 MB") {
                MaterialTheme.colorScheme.secondary
              } else {
                MaterialTheme.colorScheme.primary
              },
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Button(
          onClick = onBrowsePhotoPicker,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("browse_photos_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.AddPhotoAlternate,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Choose Photo",
            style = MaterialTheme.typography.labelLarge,
          )
        }

        OutlinedButton(
          onClick = onTakePhoto,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("take_photo_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.PhotoCamera,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Camera",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedButton(
          onClick = onBrowseDocumentFiles,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("browse_files_button"),
        ) {
          Icon(
            imageVector = Icons.Outlined.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Browse Files",
            style = MaterialTheme.typography.labelLarge,
          )
        }

        OutlinedButton(
          onClick = onPickBatchPhotos,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("batch_photos_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Collections,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Batch (Max 5)",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}
