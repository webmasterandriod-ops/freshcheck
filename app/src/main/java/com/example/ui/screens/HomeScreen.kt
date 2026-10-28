package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.DateUtils
import com.example.data.model.HomeFilter
import com.example.data.model.PantryItemEntity
import com.example.ui.components.DemoPantryBanner
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PantryItemCard
import com.example.ui.theme.FreshBorderSubtle
import com.example.ui.theme.FreshCardSurface
import com.example.ui.theme.FreshInk
import com.example.ui.theme.FreshMutedText
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenDark
import com.example.ui.theme.LeafGreenSoft
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedDark
import com.example.ui.theme.UrgentRedSoft
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmAmberDark
import com.example.ui.theme.WarmAmberSoft
import com.example.ui.viewmodel.FreshCheckUiState

@Composable
fun HomeScreen(
    uiState: FreshCheckUiState,
    onSelectFilter: (HomeFilter) -> Unit,
    onItemClick: (PantryItemEntity) -> Unit,
    onMarkUsed: (PantryItemEntity) -> Unit,
    onMarkWasted: (PantryItemEntity) -> Unit,
    onAddItemClick: () -> Unit,
    onResetDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayFormatted = DateUtils.formatFriendlyDate(uiState.todayEpochDay)
    val currency = uiState.settings.currency
    val reminderWindow = uiState.settings.reminderWindowDays

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "FRESH KITCHEN · ${todayFormatted.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LeafGreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kitchen Overview",
                        style = MaterialTheme.typography.displayMedium,
                        color = FreshInk
                    )
                }

                Surface(
                    color = FreshCardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, FreshBorderSubtle)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Reminder Window",
                            tint = WarmAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${reminderWindow}d alert",
                            style = MaterialTheme.typography.labelMedium,
                            color = FreshInk
                        )
                    }
                }
            }
        }

        // Demo Pantry Banner if demo items are active
        if (uiState.hasDemoItems) {
            item {
                DemoPantryBanner(onResetDemo = onResetDemo)
            }
        }

        // Urgency Summary Strip (Today/Expired, Expiring 1-7d, Fresh >7d)
        item {
            val urgentCount = uiState.activeItems.count { it.daysLeft(uiState.todayEpochDay) <= 0 }
            val soonCount = uiState.activeItems.count { it.daysLeft(uiState.todayEpochDay) in 1..7 }
            val freshCount = uiState.activeItems.count { it.daysLeft(uiState.todayEpochDay) > 7 }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                UrgencySummaryCard(
                    count = urgentCount,
                    label = "Today / Expired",
                    accentColor = UrgentRed,
                    softBg = UrgentRedSoft,
                    textColor = UrgentRedDark,
                    onClick = { onSelectFilter(HomeFilter.EXPIRED) },
                    modifier = Modifier.weight(1f)
                )
                UrgencySummaryCard(
                    count = soonCount,
                    label = "Expiring 1–7d",
                    accentColor = WarmAmber,
                    softBg = WarmAmberSoft,
                    textColor = WarmAmberDark,
                    onClick = { onSelectFilter(HomeFilter.THIS_WEEK) },
                    modifier = Modifier.weight(1f)
                )
                UrgencySummaryCard(
                    count = freshCount,
                    label = "Fresh (>7d)",
                    accentColor = LeafGreen,
                    softBg = LeafGreenSoft,
                    textColor = LeafGreenDark,
                    onClick = { onSelectFilter(HomeFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filter Pills: Use First, This Week, All, Expired
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                HomeFilter.entries.forEach { filter ->
                    val count = when (filter) {
                        HomeFilter.USE_FIRST -> uiState.useFirstItems.size
                        HomeFilter.THIS_WEEK -> uiState.thisWeekItems.size
                        HomeFilter.ALL -> uiState.activeItems.size
                        HomeFilter.EXPIRED -> uiState.expiredItems.size
                    }
                    val isSelected = uiState.homeFilter == filter
                    FilterPill(
                        label = "${filter.label} ($count)",
                        selected = isSelected,
                        onClick = { onSelectFilter(filter) },
                        testTag = "home_filter_${filter.name.lowercase()}"
                    )
                }
            }
        }

        // Section Header Explaining Current Ranking & Reminder Window
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = LeafGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val sectionTitle = when (uiState.homeFilter) {
                        HomeFilter.USE_FIRST -> "Use First (≤ $reminderWindow ${if (reminderWindow == 1) "day" else "days"})"
                        HomeFilter.THIS_WEEK -> "Expiring This Week (0–7 days)"
                        HomeFilter.ALL -> "All Active Kitchen Items"
                        HomeFilter.EXPIRED -> "Expired Items"
                    }
                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = FreshInk
                    )
                }

                Text(
                    text = "Ranked by urgency",
                    style = MaterialTheme.typography.bodySmall,
                    color = FreshMutedText
                )
            }
        }

        // Empty State or Item List
        if (uiState.activeItems.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Your Kitchen is Spotless",
                    subtitle = "No active items in your fridge or pantry. Add your groceries or restore the Demo Pantry to test urgency rankings.",
                    onAddFirstItem = onAddItemClick,
                    onLoadDemoPantry = onResetDemo
                )
            }
        } else if (uiState.homeDisplayedItems.isEmpty()) {
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
                        Text(
                            text = "No items in \"${uiState.homeFilter.label}\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = FreshInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Switch to 'All' to view all ${uiState.activeItems.size} items in your kitchen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FreshMutedText
                        )
                    }
                }
            }
        } else {
            items(
                items = uiState.homeDisplayedItems,
                key = { it.id }
            ) { item ->
                PantryItemCard(
                    item = item,
                    todayEpochDay = uiState.todayEpochDay,
                    currency = currency,
                    onCardClick = { onItemClick(item) },
                    onMarkUsed = { onMarkUsed(item) },
                    onMarkWasted = { onMarkWasted(item) }
                )
            }
        }
    }
}

@Composable
private fun UrgencySummaryCard(
    count: Int,
    label: String,
    accentColor: Color,
    softBg: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = textColor
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = FreshMutedText,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) LeafGreen else FreshCardSurface,
        shape = RoundedCornerShape(50),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) LeafGreen else FreshBorderSubtle
        ),
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else FreshInk,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}
