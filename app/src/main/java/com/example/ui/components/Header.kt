package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VioletPrimary

@Composable
fun Header(
  onLogoClick: () -> Unit,
  onGetStartedClick: () -> Unit,
  showGetStarted: Boolean,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
    tonalElevation = 1.dp,
    shadowElevation = 2.dp,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .clickable(onClick = onLogoClick)
          .padding(vertical = 4.dp, horizontal = 2.dp)
          .testTag("header_logo"),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(
              Brush.linearGradient(listOf(VioletPrimary, BlueAccent))
            ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoFixHigh,
            contentDescription = "BGRemover Pro Logo",
            tint = PureWhite,
            modifier = Modifier.size(21.dp),
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "BGRemover Pro",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = "Remove Backgrounds. Keep What Matters.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      if (showGetStarted) {
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = onGetStartedClick,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          modifier = Modifier.testTag("header_get_started_button"),
        ) {
          Text(
            text = "Get Started",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}
