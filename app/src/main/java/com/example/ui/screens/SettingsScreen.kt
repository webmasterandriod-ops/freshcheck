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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CurrencyOption
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
fun SettingsScreen(
    uiState: FreshCheckUiState,
    onSelectReminderWindow: (Int) -> Unit,
    onSelectCurrency: (CurrencyOption) -> Unit,
    onExportCsv: () -> Unit,
    onResetDemo: () -> Unit,
    onRequestClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentWindow = uiState.settings.reminderWindowDays
    val currentCurrency = uiState.settings.currency

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "PREFERENCES & DATA",
                    style = MaterialTheme.typography.labelSmall,
                    color = LeafGreenDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Kitchen Settings",
                    style = MaterialTheme.typography.displayMedium,
                    color = FreshInk
                )
            }
        }

        // 1. Reminder Window Setting (1 / 3 / 7 days)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(LeafGreenSoft)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = LeafGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Use First Reminder Window",
                                style = MaterialTheme.typography.titleMedium,
                                color = FreshInk
                            )
                            Text(
                                text = "Changes which items appear in your Home Use First list",
                                style = MaterialTheme.typography.bodySmall,
                                color = FreshMutedText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(1, 3, 7).forEach { days ->
                            val selected = currentWindow == days
                            Surface(
                                color = if (selected) LeafGreen else FreshSurfaceSubtle,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) LeafGreen else FreshBorderSubtle
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectReminderWindow(days) }
                                    .testTag("reminder_window_${days}d")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "$days ${if (days == 1) "Day" else "Days"}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else FreshInk
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = LeafGreenSoft.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Currently matching ${uiState.useFirstItems.size} ${if (uiState.useFirstItems.size == 1) "item" else "items"} in your Home 'Use First' list (≤ $currentWindow ${if (currentWindow == 1) "day" else "days"}).",
                            style = MaterialTheme.typography.bodySmall,
                            color = LeafGreenDark,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("settings_use_first_preview")
                        )
                    }
                }
            }
        }

        // 2. Currency Setting (USD / EUR)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(LeafGreenSoft)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = LeafGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Display Currency",
                                style = MaterialTheme.typography.titleMedium,
                                color = FreshInk
                            )
                            Text(
                                text = "Updates all item prices, Saved value, and Wasted totals",
                                style = MaterialTheme.typography.bodySmall,
                                color = FreshMutedText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CurrencyOption.entries.forEach { option ->
                            val selected = currentCurrency == option
                            Surface(
                                color = if (selected) LeafGreen else FreshSurfaceSubtle,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) LeafGreen else FreshBorderSubtle
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectCurrency(option) }
                                    .testTag("currency_option_${option.code.lowercase()}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else FreshInk
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Data Management Card: CSV Export, Demo Pantry Reset, Clear All
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Kitchen Data & Export",
                        style = MaterialTheme.typography.titleMedium,
                        color = FreshInk
                    )

                    // Export CSV Button
                    Button(
                        onClick = onExportCsv,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_csv_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export Kitchen CSV File",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                    }

                    // Reset Demo Pantry Button
                    OutlinedButton(
                        onClick = onResetDemo,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, LeafGreen),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_reset_demo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = LeafGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seed / Reset Demo Pantry",
                            style = MaterialTheme.typography.titleSmall,
                            color = LeafGreenDark
                        )
                    }

                    // Clear All Kitchen Data with Confirm
                    OutlinedButton(
                        onClick = onRequestClearAll,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, UrgentRed.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = UrgentRedSoft.copy(alpha = 0.45f)
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_all_data_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = UrgentRedDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear All Kitchen Data",
                            style = MaterialTheme.typography.titleSmall,
                            color = UrgentRedDark
                        )
                    }
                }
            }
        }

        // 4. Privacy Note Card (Local storage only)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LeafGreenSoft),
                border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_note_card")
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LeafGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Privacy",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% Local On-Device Privacy",
                            style = MaterialTheme.typography.titleSmall,
                            color = LeafGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Local on-device storage only — no account, no backend, no data leaves the device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FreshInk
                        )
                    }
                }
            }
        }
    }
}
