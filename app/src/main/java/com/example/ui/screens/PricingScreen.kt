package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.Footer
import com.example.ui.components.PricingCard

@Composable
fun PricingScreen(
  onNavigateStudio: () -> Unit,
  onShowPlanNote: (String) -> Unit,
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
          .widthIn(max = 820.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          text = "Simple, Transparent Pricing",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = "Compare Free and Pro plans designed for individual creators and high-volume workflows.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val isWide = maxWidth >= 620.dp
          if (isWide) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              FreePlanCard(
                onSelect = onNavigateStudio,
                modifier = Modifier.weight(1f),
              )
              ProPlanCard(
                onSelect = {
                  onShowPlanNote("Pro plan (₹99/month) is informational in this release—no payment or subscription is required.")
                },
                modifier = Modifier.weight(1f),
              )
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
              FreePlanCard(onSelect = onNavigateStudio)
              ProPlanCard(
                onSelect = {
                  onShowPlanNote("Pro plan (₹99/month) is informational in this release—no payment or subscription is required.")
                },
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Pricing tiers shown here are informational. Image processing uses your configured remove.bg API credentials with no in-app checkout or subscription lock.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Footer(
          onNavigateHome = onNavigateStudio,
          onNavigateRemover = onNavigateStudio,
          onNavigatePricing = {},
        )
      }
    }
  }
}

@Composable
private fun FreePlanCard(
  onSelect: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PricingCard(
    tierName = "FREE",
    priceLabel = "₹0",
    periodLabel = "starter",
    description = "Everything you need to test AI cutouts and download transparent PNGs.",
    features = listOf(
      "Limited background removal credits",
      "Standard PNG download",
      "Basic processing",
    ),
    highlighted = false,
    badgeText = null,
    actionLabel = "Use Remover",
    onActionClick = onSelect,
    modifier = modifier,
  )
}

@Composable
private fun ProPlanCard(
  onSelect: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PricingCard(
    tierName = "PRO",
    priceLabel = "₹99",
    periodLabel = "/month",
    description = "Built for creators, sellers, and teams needing faster daily capacity.",
    features = listOf(
      "Higher processing limits",
      "Batch background removal (up to 5 images)",
      "Priority processing",
      "No advertisements",
    ),
    highlighted = true,
    badgeText = "POPULAR",
    actionLabel = "View Pro Details",
    onActionClick = onSelect,
    modifier = modifier,
  )
}
