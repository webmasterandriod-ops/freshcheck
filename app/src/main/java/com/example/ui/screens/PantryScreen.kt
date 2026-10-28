package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.FoodCategory
import com.example.data.model.PantryFilter
import com.example.data.model.PantryItemEntity
import com.example.data.model.StorageLocation
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PantryItemCard
import com.example.ui.components.categoryIcon
import com.example.ui.components.categoryIconTint
import com.example.ui.theme.FreshBorderSubtle
import com.example.ui.theme.FreshCardSurface
import com.example.ui.theme.FreshInk
import com.example.ui.theme.FreshMutedText
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenDark
import com.example.ui.theme.LeafGreenSoft
import com.example.ui.viewmodel.FreshCheckUiState

@Composable
fun PantryScreen(
    uiState: FreshCheckUiState,
    onSelectFilter: (PantryFilter) -> Unit,
    onSelectCategory: (FoodCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (PantryItemEntity) -> Unit,
    onMarkUsed: (PantryItemEntity) -> Unit,
    onMarkWasted: (PantryItemEntity) -> Unit,
    onAddItemClick: () -> Unit,
    onResetDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = uiState.settings.currency
    val totalActiveValue = uiState.pantryDisplayedItems.sumOf { it.cost }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("pantry_screen")
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
                        text = "STORAGE INVENTORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = LeafGreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pantry & Fridge",
                        style = MaterialTheme.typography.displayMedium,
                        color = FreshInk
                    )
                }

                Surface(
                    color = LeafGreenSoft,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.3f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Shown Value",
                            style = MaterialTheme.typography.labelSmall,
                            color = FreshMutedText
                        )
                        Text(
                            text = currency.formatAmount(totalActiveValue),
                            style = MaterialTheme.typography.titleMedium,
                            color = LeafGreenDark
                        )
                    }
                }
            }
        }

        // Search input
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search items, category, or location…",
                        color = FreshMutedText
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = FreshMutedText
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = FreshMutedText
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FreshCardSurface,
                    unfocusedContainerColor = FreshCardSurface,
                    focusedBorderColor = LeafGreen,
                    unfocusedBorderColor = FreshBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pantry_search_input")
            )
        }

        // Primary Location/Status Filters: All, Fridge, Freezer, Pantry, Expired
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                PantryFilter.entries.forEach { filter ->
                    val count = when (filter) {
                        PantryFilter.ALL -> uiState.activeItems.size
                        PantryFilter.FRIDGE -> uiState.activeItems.count { it.storageLocation == StorageLocation.FRIDGE }
                        PantryFilter.FREEZER -> uiState.activeItems.count { it.storageLocation == StorageLocation.FREEZER }
                        PantryFilter.PANTRY -> uiState.activeItems.count { it.storageLocation == StorageLocation.PANTRY }
                        PantryFilter.EXPIRED -> uiState.expiredItems.size
                    }
                    FilterPill(
                        label = "${filter.label} ($count)",
                        selected = uiState.pantryFilter == filter,
                        onClick = { onSelectFilter(filter) },
                        testTag = "pantry_filter_${filter.name.lowercase()}"
                    )
                }
            }
        }

        // Secondary Category Filter Chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                FoodCategory.entries.forEach { category ->
                    val isSelected = uiState.pantryCategoryFilter == category
                    Surface(
                        color = if (isSelected) FreshInk else FreshCardSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isSelected) FreshInk else FreshBorderSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectCategory(category) }
                            .testTag("pantry_cat_chip_${category.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = categoryIcon(category),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else categoryIconTint(category),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else FreshInk
                            )
                        }
                    }
                }
            }
        }

        // Empty State or Filtered List
        if (uiState.activeItems.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Groceries in Storage",
                    subtitle = "Your Fridge, Freezer, and Pantry are currently empty. Tap + to add an item or load the Demo Pantry.",
                    onAddFirstItem = onAddItemClick,
                    onLoadDemoPantry = onResetDemo
                )
            }
        } else if (uiState.pantryDisplayedItems.isEmpty()) {
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
                            text = "No matching items",
                            style = MaterialTheme.typography.titleMedium,
                            color = FreshInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No items match the current location/category filter or search query.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FreshMutedText
                        )
                    }
                }
            }
        } else {
            items(
                items = uiState.pantryDisplayedItems,
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
