package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PricingCard(
  tierName: String,
  priceLabel: String,
  periodLabel: String,
  description: String,
  features: List<String>,
  highlighted: Boolean,
  badgeText: String? = null,
  actionLabel: String,
  onActionClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("pricing_card_${tierName.lowercase()}"),
    shape = RoundedCornerShape(24.dp),
    color = if (highlighted) {
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
    } else {
      MaterialTheme.colorScheme.surface
    },
    border = BorderStroke(
      width = if (highlighted) 2.dp else 1.dp,
      color = if (highlighted) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.outline
      },
    ),
    shadowElevation = if (highlighted) 6.dp else 2.dp,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(22.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = tierName,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        if (badgeText != null) {
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.primary,
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = priceLabel,
          style = MaterialTheme.typography.displaySmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        if (periodLabel.isNotBlank()) {
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = periodLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 5.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(18.dp))

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        features.forEach { feature ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = if (highlighted) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.secondary
              },
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = feature,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (highlighted) {
        Button(
          onClick = onActionClick,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        ) {
          Text(
            text = actionLabel,
            style = MaterialTheme.typography.labelLarge,
          )
        }
      } else {
        OutlinedButton(
          onClick = onActionClick,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        ) {
          Text(
            text = actionLabel,
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}
