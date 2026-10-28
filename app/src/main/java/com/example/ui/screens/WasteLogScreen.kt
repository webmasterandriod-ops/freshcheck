package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.ItemStatus
import com.example.data.model.PantryItemEntity
import com.example.ui.components.categoryContainerColor
import com.example.ui.components.categoryIcon
import com.example.ui.components.categoryIconTint
import com.example.ui.theme.FreshBorderSubtle
import com.example.ui.theme.FreshCardSurface
import com.example.ui.theme.FreshInk
import com.example.ui.theme.FreshMutedText
import com.example.ui.theme.FreshSurfaceSubtle
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenDark
import com.example.ui.theme.LeafGreenSoft
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedDark
import com.example.ui.theme.UrgentRedSoft
import com.example.ui.viewmodel.FreshCheckUiState

@Composable
fun WasteLogScreen(
    uiState: FreshCheckUiState,
    onRestoreItem: (PantryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.settings.currency
    val combinedLog = (uiState.savedItems + uiState.wastedItems)
        .sortedByDescending { it.resolvedAt ?: it.createdAt }

    val totalResolved = uiState.savedCount + uiState.wastedCount
    val savedRatio = if (totalResolved > 0) {
        uiState.savedCount.toFloat() / totalResolved.toFloat()
    } else {
        1f
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("waste_log_screen")
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "SAVINGS & WASTE TRACKER",
                    style = MaterialTheme.typography.labelSmall,
                    color = LeafGreenDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Waste & Savings Log",
                    style = MaterialTheme.typography.displayMedium,
                    color = FreshInk
                )
            }
        }

        // Saved Counter Card & Wasted Totals Card side-by-side
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // SAVED COUNTER CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                    border = BorderStroke(1.5.dp, LeafGreen.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("saved_totals_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(LeafGreenSoft)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = "Saved",
                                    tint = LeafGreenDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Saved",
                                style = MaterialTheme.typography.titleMedium,
                                color = LeafGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currency.formatAmount(uiState.totalValueSaved),
                            style = MaterialTheme.typography.displayMedium,
                            color = FreshInk,
                            modifier = Modifier.testTag("saved_total_value_text")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = LeafGreenSoft,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${uiState.savedCount} ${if (uiState.savedCount == 1) "item" else "items"} used",
                                style = MaterialTheme.typography.labelMedium,
                                color = LeafGreenDark,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("saved_count_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Items used before expiry",
                            style = MaterialTheme.typography.bodySmall,
                            color = FreshMutedText
                        )
                    }
                }

                // WASTED TOTALS CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                    border = BorderStroke(1.5.dp, UrgentRed.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wasted_totals_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(UrgentRedSoft)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Wasted",
                                    tint = UrgentRedDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Wasted",
                                style = MaterialTheme.typography.titleMedium,
                                color = UrgentRedDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currency.formatAmount(uiState.totalCostWasted),
                            style = MaterialTheme.typography.displayMedium,
                            color = FreshInk,
                            modifier = Modifier.testTag("wasted_total_value_text")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = UrgentRedSoft,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${uiState.wastedCount} ${if (uiState.wastedCount == 1) "item" else "items"} wasted",
                                style = MaterialTheme.typography.labelMedium,
                                color = UrgentRedDark,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("wasted_count_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Expired or discarded cost",
                            style = MaterialTheme.typography.bodySmall,
                            color = FreshMutedText
                        )
                    }
                }
            }
        }

        // Kitchen Efficiency Meter
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Kitchen Freshness Efficiency",
                            style = MaterialTheme.typography.titleSmall,
                            color = FreshInk
                        )
                        val pct = (savedRatio * 100).toInt()
                        Text(
                            text = if (totalResolved > 0) "$pct% Saved" else "No items logged yet",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (pct >= 70) LeafGreenDark else UrgentRedDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (totalResolved > 0) UrgentRedSoft else FreshSurfaceSubtle)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = savedRatio.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(LeafGreen)
                        )
                    }
                }
            }
        }

        // Activity History Header
        item {
            Text(
                text = "Resolved Items History (${combinedLog.size})",
                style = MaterialTheme.typography.titleMedium,
                color = FreshInk
            )
        }

        if (combinedLog.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                    border = BorderStroke(1.dp, FreshBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LeafGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Used or Wasted Items Logged Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = FreshInk,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Used' or 'Wasted' on any item in Home or Pantry to test your Saved and Wasted totals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FreshMutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(
                items = combinedLog,
                key = { it.id }
            ) { item ->
                val isSaved = item.itemStatus == ItemStatus.USED
                val cat = item.foodCategory

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                    border = BorderStroke(1.dp, FreshBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(categoryContainerColor(cat))
                            ) {
                                Icon(
                                    imageVector = categoryIcon(cat),
                                    contentDescription = cat.displayName,
                                    tint = categoryIconTint(cat),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = FreshInk
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = if (isSaved) LeafGreenSoft else UrgentRedSoft,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isSaved) "SAVED" else "WASTED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSaved) LeafGreenDark else UrgentRedDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.quantity} · ${currency.formatAmount(item.cost)} · ${item.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FreshMutedText
                                )
                            }
                        }

                        Surface(
                            color = FreshSurfaceSubtle,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, FreshBorderSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onRestoreItem(item) }
                                .testTag("restore_item_${item.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Restore to Pantry",
                                    tint = FreshInk,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Undo",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = FreshInk,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
