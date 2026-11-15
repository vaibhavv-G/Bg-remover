package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun DownloadButton(
  isSaving: Boolean,
  onDownloadClick: () -> Unit,
  onShareClick: () -> Unit,
  onTryAnotherClick: () -> Unit,
  onResetClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Button(
        onClick = onDownloadClick,
        enabled = !isSaving,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .testTag("download_png_button"),
      ) {
        if (isSaving) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.5.dp,
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Saving PNG…",
            style = MaterialTheme.typography.labelLarge,
          )
        } else {
          Icon(
            imageVector = Icons.Rounded.Download,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Download Transparent PNG",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      OutlinedButton(
        onClick = onShareClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .height(54.dp)
          .testTag("share_png_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Share,
          contentDescription = "Share transparent PNG",
          modifier = Modifier.size(20.dp),
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      OutlinedButton(
        onClick = onTryAnotherClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("try_another_image_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.AddPhotoAlternate,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Try Another Image",
          style = MaterialTheme.typography.labelLarge,
        )
      }

      TextButton(
        onClick = onResetClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .height(48.dp)
          .testTag("reset_tool_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.RestartAlt,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Reset",
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}
