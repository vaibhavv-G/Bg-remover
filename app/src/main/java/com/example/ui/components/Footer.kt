package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.VioletPrimary

private enum class FooterInfoSheet(val title: String, val body: String) {
  CONTACT(
    title = "Contact BGRemover Pro",
    body = "Need help with API configuration, supported image formats, or export issues? Reach out to your workspace administrator or configure your remove.bg API credentials in the AI Studio Secrets panel.",
  ),
  PRIVACY(
    title = "Privacy Policy",
    body = "Images you select are transmitted securely over HTTPS directly to the configured background removal API provider solely to generate your transparent PNG. BGRemover Pro does not build a permanent cloud gallery or retain uploaded photos after processing finishes.",
  ),
  TERMS(
    title = "Terms of Service",
    body = "BGRemover Pro provides AI-assisted background removal subject to your configured API provider's usage limits and terms. Ensure you hold the rights to any image you process.",
  ),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Footer(
  onNavigateHome: () -> Unit,
  onNavigateRemover: () -> Unit,
  onNavigatePricing: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var activeDialog by remember { mutableStateOf<FooterInfoSheet?>(null) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("app_footer"),
    shape = RoundedCornerShape(22.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Brush.linearGradient(listOf(VioletPrimary, BlueAccent))),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoFixHigh,
            contentDescription = "BGRemover Pro",
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "BGRemover Pro",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "Remove Backgrounds. Keep What Matters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FooterLink("Home", onClick = onNavigateHome)
        FooterLink("Background Remover", onClick = onNavigateRemover)
        FooterLink("Pricing", onClick = onNavigatePricing)
        FooterLink("Contact", onClick = { activeDialog = FooterInfoSheet.CONTACT })
        FooterLink("Privacy Policy", onClick = { activeDialog = FooterInfoSheet.PRIVACY })
        FooterLink("Terms of Service", onClick = { activeDialog = FooterInfoSheet.TERMS })
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline)
      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "© 2026 BGRemover Pro. All rights reserved.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }

  activeDialog?.let { info ->
    AlertDialog(
      onDismissRequest = { activeDialog = null },
      title = {
        Text(
          text = info.title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
        )
      },
      text = {
        Text(
          text = info.body,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      },
      confirmButton = {
        TextButton(onClick = { activeDialog = null }) {
          Text("Close")
        }
      },
    )
  }
}

@Composable
private fun FooterLink(
  label: String,
  onClick: () -> Unit,
) {
  Text(
    text = label,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.SemiBold,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp, vertical = 6.dp),
  )
}
