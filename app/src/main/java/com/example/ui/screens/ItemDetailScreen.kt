package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.DateUtils
import com.example.data.model.CurrencyOption
import com.example.data.model.PantryItemEntity
import com.example.data.model.UrgencyLevel
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.categoryContainerColor
import com.example.ui.components.categoryIcon
import com.example.ui.components.categoryIconTint
import com.example.ui.components.locationIcon
import com.example.ui.components.urgencyAccentColor
import com.example.ui.components.urgencyContainerColor
import com.example.ui.components.urgencyTextColor
import com.example.ui.theme.FreshBorderSubtle
import com.example.ui.theme.FreshCardSurface
import com.example.ui.theme.FreshInk
import com.example.ui.theme.FreshMutedText
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedDark
import com.example.ui.theme.UrgentRedSoft

@Composable
fun ItemDetailScreen(
    item: PantryItemEntity,
    todayEpochDay: Long,
    currency: CurrencyOption,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onMarkUsed: () -> Unit,
    onMarkWasted: () -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val daysLeft = item.daysLeft(todayEpochDay)
    val urgency = item.urgencyLevel(todayEpochDay)
    val accentColor = urgencyAccentColor(urgency)
    val softBg = urgencyContainerColor(urgency)
    val statusTextColor = urgencyTextColor(urgency)
    val cat = item.foodCategory
    val loc = item.storageLocation

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 88.dp)
            .testTag("item_detail_screen")
    ) {
        // Top Navigation Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = FreshCardSurface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, FreshBorderSubtle),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack)
                        .testTag("detail_back_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FreshInk
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ITEM DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        color = FreshMutedText
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = FreshInk
                    )
                }
            }

            Surface(
                color = FreshCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onEdit)
                    .testTag("detail_edit_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = FreshInk,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshInk
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Urgency Status Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
            border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(categoryContainerColor(cat))
                    ) {
                        Icon(
                            imageVector = categoryIcon(cat),
                            contentDescription = cat.displayName,
                            tint = categoryIconTint(cat),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    UrgencyBadge(
                        daysLeft = daysLeft,
                        level = urgency
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = FreshInk
                    )
                    if (item.isDemo) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            color = softBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "DEMO ITEM",
                                style = MaterialTheme.typography.labelSmall,
                                color = statusTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val statusExplanation = when (urgency) {
                    UrgencyLevel.EXPIRED_OR_TODAY -> if (daysLeft == 0) {
                        "Expires today — prioritize using this ingredient right away."
                    } else {
                        "Expired ${-daysLeft} ${if (daysLeft == -1) "day" else "days"} ago — check quality or log in Waste Log."
                    }
                    UrgencyLevel.EXPIRING_SOON -> "Expiring within the week ($daysLeft ${if (daysLeft == 1) "day" else "days"} left)."
                    UrgencyLevel.FRESH -> "Fresh and well within shelf life ($daysLeft days remaining)."
                }

                Text(
                    text = statusExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FreshMutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metadata Grid
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MetadataTile(
                icon = locationIcon(loc),
                label = "Storage Location",
                value = loc.displayName,
                modifier = Modifier.weight(1f)
            )
            MetadataTile(
                icon = categoryIcon(cat),
                label = "Category",
                value = cat.displayName,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MetadataTile(
                icon = Icons.Default.Inventory2,
                label = "Quantity",
                value = item.quantity,
                modifier = Modifier.weight(1f)
            )
            MetadataTile(
                icon = Icons.Default.Payments,
                label = "Estimated Cost",
                value = currency.formatAmount(item.cost),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        MetadataTile(
            icon = Icons.Default.CalendarToday,
            label = "Expiry Date",
            value = "${DateUtils.formatFriendlyDate(item.expiryEpochDay)} (${item.expiryDateIso})",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Primary Resolution Actions: Mark Used & Mark Wasted
        Text(
            text = "Resolve Item",
            style = MaterialTheme.typography.titleMedium,
            color = FreshInk
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onMarkUsed,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("detail_mark_used_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark Used",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }

            Button(
                onClick = onMarkWasted,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UrgentRed),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("detail_mark_wasted_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark Wasted",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Secondary Actions: Edit & Delete with Confirm
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = FreshInk,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Edit Details",
                    style = MaterialTheme.typography.labelLarge,
                    color = FreshInk
                )
            }

            OutlinedButton(
                onClick = onRequestDelete,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, UrgentRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = UrgentRedSoft.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("detail_delete_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = UrgentRedDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Delete Item",
                    style = MaterialTheme.typography.labelLarge,
                    color = UrgentRedDark
                )
            }
        }
    }
}

@Composable
private fun MetadataTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
        border = BorderStroke(1.dp, FreshBorderSubtle),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FreshMutedText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = FreshMutedText
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = FreshInk
                )
            }
        }
    }
}
