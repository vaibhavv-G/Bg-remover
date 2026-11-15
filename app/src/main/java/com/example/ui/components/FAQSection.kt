package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FaqItem(
  val question: String,
  val answer: String,
)

@Composable
fun FAQSection(
  items: List<FaqItem>,
  modifier: Modifier = Modifier,
) {
  var expandedIndex by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    items.forEachIndexed { index, item ->
      val expanded = expandedIndex == index
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .clickable {
            expandedIndex = if (expanded) -1 else index
          }
          .testTag("faq_item_$index"),
        shape = RoundedCornerShape(18.dp),
        color = if (expanded) {
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
          MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
          1.dp,
          if (expanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
          } else {
            MaterialTheme.colorScheme.outline
          },
        ),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = item.question,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
              contentDescription = if (expanded) "Collapse answer" else "Expand answer",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp),
            )
          }

          AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
          ) {
            Column {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}
