package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.DateUtils
import com.example.data.model.CurrencyOption
import com.example.data.model.FoodCategory
import com.example.data.model.PantryItemEntity
import com.example.data.model.StorageLocation
import com.example.data.model.UrgencyLevel
import com.example.ui.theme.CatBakeryBg
import com.example.ui.theme.CatDairyBg
import com.example.ui.theme.CatFrozenBg
import com.example.ui.theme.CatMeatBg
import com.example.ui.theme.CatOtherBg
import com.example.ui.theme.CatPantryBg
import com.example.ui.theme.CatProduceBg
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
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmAmberDark
import com.example.ui.theme.WarmAmberSoft
import com.example.ui.viewmodel.CsvExportResult

fun urgencyAccentColor(level: UrgencyLevel): Color = when (level) {
    UrgencyLevel.EXPIRED_OR_TODAY -> UrgentRed
    UrgencyLevel.EXPIRING_SOON -> WarmAmber
    UrgencyLevel.FRESH -> LeafGreen
}

fun urgencyContainerColor(level: UrgencyLevel): Color = when (level) {
    UrgencyLevel.EXPIRED_OR_TODAY -> UrgentRedSoft
    UrgencyLevel.EXPIRING_SOON -> WarmAmberSoft
    UrgencyLevel.FRESH -> LeafGreenSoft
}

fun urgencyTextColor(level: UrgencyLevel): Color = when (level) {
    UrgencyLevel.EXPIRED_OR_TODAY -> UrgentRedDark
    UrgencyLevel.EXPIRING_SOON -> WarmAmberDark
    UrgencyLevel.FRESH -> LeafGreenDark
}

fun categoryIcon(category: FoodCategory): ImageVector = when (category) {
    FoodCategory.PRODUCE -> Icons.Default.Eco
    FoodCategory.DAIRY -> Icons.Default.EggAlt
    FoodCategory.MEAT_SEAFOOD -> Icons.Default.SetMeal
    FoodCategory.BAKERY -> Icons.Default.BakeryDining
    FoodCategory.PANTRY -> Icons.Default.Inventory2
    FoodCategory.FROZEN -> Icons.Default.AcUnit
    FoodCategory.OTHER -> Icons.Default.Restaurant
}

fun categoryContainerColor(category: FoodCategory): Color = when (category) {
    FoodCategory.PRODUCE -> CatProduceBg
    FoodCategory.DAIRY -> CatDairyBg
    FoodCategory.MEAT_SEAFOOD -> CatMeatBg
    FoodCategory.BAKERY -> CatBakeryBg
    FoodCategory.PANTRY -> CatPantryBg
    FoodCategory.FROZEN -> CatFrozenBg
    FoodCategory.OTHER -> CatOtherBg
}

fun categoryIconTint(category: FoodCategory): Color = when (category) {
    FoodCategory.PRODUCE -> LeafGreen
    FoodCategory.DAIRY -> Color(0xFF2B6CB0)
    FoodCategory.MEAT_SEAFOOD -> UrgentRed
    FoodCategory.BAKERY -> WarmAmberDark
    FoodCategory.PANTRY -> Color(0xFF6B5344)
    FoodCategory.FROZEN -> Color(0xFF0987A0)
    FoodCategory.OTHER -> FreshMutedText
}

fun locationIcon(location: StorageLocation): ImageVector = when (location) {
    StorageLocation.FRIDGE -> Icons.Default.Kitchen
    StorageLocation.FREEZER -> Icons.Default.AcUnit
    StorageLocation.PANTRY -> Icons.Default.Inventory2
}

@Composable
fun UrgencyBadge(
    daysLeft: Int,
    level: UrgencyLevel,
    modifier: Modifier = Modifier
) {
    val label = DateUtils.formatDaysLeftBadge(daysLeft)
    val dotColor = urgencyAccentColor(level)
    val bgColor = urgencyContainerColor(level)
    val textColor = urgencyTextColor(level)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DemoPantryBanner(
    onResetDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LeafGreenSoft,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("demo_pantry_banner")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = LeafGreen,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "DEMO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Demo Pantry Active",
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshInk
                    )
                    Text(
                        text = "Expiry dates computed relative to today",
                        style = MaterialTheme.typography.bodySmall,
                        color = FreshMutedText
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = FreshCardSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onResetDemo)
                    .testTag("reset_demo_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Demo Pantry",
                        tint = LeafGreenDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium,
                        color = LeafGreenDark
                    )
                }
            }
        }
    }
}

@Composable
fun PantryItemCard(
    item: PantryItemEntity,
    todayEpochDay: Long,
    currency: CurrencyOption,
    onCardClick: () -> Unit,
    onMarkUsed: () -> Unit,
    onMarkWasted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = item.daysLeft(todayEpochDay)
    val urgency = item.urgencyLevel(todayEpochDay)
    val accentColor = urgencyAccentColor(urgency)
    val cat = item.foodCategory

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
        border = BorderStroke(1.dp, FreshBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onCardClick)
            .testTag("pantry_item_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = accentColor,
                        topLeft = Offset.Zero,
                        size = Size(width = 6.dp.toPx(), height = size.height)
                    )
                }
                .padding(start = 18.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
        ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(categoryContainerColor(cat))
                        ) {
                            Icon(
                                imageVector = categoryIcon(cat),
                                contentDescription = cat.displayName,
                                tint = categoryIconTint(cat),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = FreshInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (item.isDemo) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = FreshSurfaceSubtle,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "DEMO",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FreshMutedText,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val pricePart = if (item.cost > 0.0) " · ${currency.formatAmount(item.cost)}" else ""
                            Text(
                                text = "${item.location} · ${item.category} · ${item.quantity}$pricePart",
                                style = MaterialTheme.typography.bodySmall,
                                color = FreshMutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    UrgencyBadge(
                        daysLeft = daysLeft,
                        level = urgency
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom row: Expiry date + quick Used / Wasted actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Exp: ${DateUtils.formatFriendlyDate(item.expiryEpochDay)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FreshMutedText
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = LeafGreenSoft,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onMarkUsed)
                                .testTag("card_mark_used_${item.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Mark Used",
                                    tint = LeafGreenDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Used",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LeafGreenDark
                                )
                            }
                        }

                        Surface(
                            color = UrgentRedSoft,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, UrgentRed.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onMarkWasted)
                                .testTag("card_mark_wasted_${item.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Mark Wasted",
                                    tint = UrgentRedDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Wasted",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = UrgentRedDark
                                )
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    onAddFirstItem: () -> Unit,
    onLoadDemoPantry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
        border = BorderStroke(1.dp, FreshBorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .testTag("empty_state_view")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            // Custom Fresh Kitchen vector illustration using Compose Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(LeafGreenSoft)
            ) {
                Canvas(modifier = Modifier.size(96.dp)) {
                    val w = size.width
                    val h = size.height

                    // Wooden shelf plank
                    drawRoundRect(
                        color = Color(0xFFD6B88D),
                        topLeft = Offset(w * 0.08f, h * 0.72f),
                        size = Size(w * 0.84f, h * 0.09f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Left glass jar body
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.16f, h * 0.30f),
                        size = Size(w * 0.30f, h * 0.42f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    // Jar lid
                    drawRoundRect(
                        color = Color(0xFF2E9E5B),
                        topLeft = Offset(w * 0.19f, h * 0.22f),
                        size = Size(w * 0.24f, h * 0.08f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Leaf inside jar
                    val leafPath = Path().apply {
                        moveTo(w * 0.31f, h * 0.62f)
                        quadraticTo(w * 0.20f, h * 0.48f, w * 0.34f, h * 0.38f)
                        quadraticTo(w * 0.42f, h * 0.50f, w * 0.31f, h * 0.62f)
                        close()
                    }
                    drawPath(leafPath, color = Color(0xFF2E9E5B))

                    // Right milk bottle
                    drawRoundRect(
                        color = Color(0xFFFFF4DE),
                        topLeft = Offset(w * 0.54f, h * 0.35f),
                        size = Size(w * 0.28f, h * 0.37f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                    // Bottle neck & cap
                    drawRoundRect(
                        color = Color(0xFFE8A020),
                        topLeft = Offset(w * 0.61f, h * 0.24f),
                        size = Size(w * 0.14f, h * 0.11f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = FreshInk,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = FreshMutedText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onLoadDemoPantry,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LeafGreen),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("empty_load_demo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = LeafGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Seed Demo",
                        color = LeafGreenDark,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onAddFirstItem,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("empty_add_item_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Item",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeleteSheet(
    itemName: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FreshCardSurface
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .testTag("confirm_delete_sheet")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(UrgentRedSoft)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = UrgentRed,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Delete \"$itemName\"?",
                style = MaterialTheme.typography.headlineMedium,
                color = FreshInk,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This permanently removes \"$itemName\" from your kitchen without recording it in your Saved or Wasted totals.",
                style = MaterialTheme.typography.bodyMedium,
                color = FreshMutedText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_delete_button")
                ) {
                    Text("Cancel", color = FreshInk)
                }

                Button(
                    onClick = onConfirmDelete,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UrgentRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("confirm_delete_button")
                ) {
                    Text("Delete Item", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmClearAllSheet(
    onConfirmClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FreshCardSurface
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .testTag("confirm_clear_all_sheet")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(UrgentRedSoft)
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = UrgentRed,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Clear All Kitchen Data?",
                style = MaterialTheme.typography.headlineMedium,
                color = FreshInk,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This will permanently delete all active items and reset your Saved and Wasted logs on this device. You can re-seed the Demo Pantry anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = FreshMutedText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_clear_all_button")
                ) {
                    Text("Cancel", color = FreshInk)
                }

                Button(
                    onClick = onConfirmClear,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UrgentRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("confirm_clear_all_button")
                ) {
                    Text("Clear Everything", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CsvExportDialog(
    result: CsvExportResult,
    onSaveToFilePicker: () -> Unit,
    onShareCsv: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
            border = BorderStroke(1.dp, FreshBorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("csv_export_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LeafGreenSoft)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            tint = LeafGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CSV File Exported",
                            style = MaterialTheme.typography.titleLarge,
                            color = FreshInk
                        )
                        Text(
                            text = result.fileName,
                            style = MaterialTheme.typography.bodySmall,
                            color = LeafGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Saved locally to device storage:\n${result.filePath}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FreshMutedText
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = FreshSurfaceSubtle,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FreshBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = result.csvContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = FreshInk
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = onSaveToFilePicker,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = LeafGreenSoft,
                            contentColor = LeafGreenDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("csv_save_as_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save As…")
                    }

                    FilledTonalButton(
                        onClick = onShareCsv,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = FreshSurfaceSubtle,
                            contentColor = FreshInk
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("csv_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("csv_close_button")
                ) {
                    Text("Done")
                }
            }
        }
    }
}
