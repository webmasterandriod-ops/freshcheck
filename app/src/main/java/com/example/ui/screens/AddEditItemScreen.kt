package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.DateUtils
import com.example.data.model.CurrencyOption
import com.example.data.model.FoodCategory
import com.example.data.model.StorageLocation
import com.example.data.model.UrgencyLevel
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.categoryIcon
import com.example.ui.components.categoryIconTint
import com.example.ui.components.locationIcon
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
import com.example.ui.viewmodel.FormInputState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditItemScreen(
    formState: FormInputState,
    todayEpochDay: Long,
    currency: CurrencyOption,
    onNameChange: (String) -> Unit,
    onCategoryChange: (FoodCategory) -> Unit,
    onLocationChange: (StorageLocation) -> Unit,
    onExpiryDateChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onApplyPreset: (String, Int, FoodCategory) -> Unit,
    onAdjustDateByDays: (Int) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onCancel)

    var showDatePicker by remember { mutableStateOf(false) }
    val isEditing = formState.editingId != 0L

    val parsedEpochDay = DateUtils.parseIsoDateToEpochDay(formState.expiryDateIso)
    val previewDaysLeft = parsedEpochDay?.let { (it - todayEpochDay).toInt() }
    val previewUrgency = previewDaysLeft?.let { days ->
        when {
            days <= 0 -> UrgencyLevel.EXPIRED_OR_TODAY
            days in 1..7 -> UrgencyLevel.EXPIRING_SOON
            else -> UrgencyLevel.FRESH
        }
    }

    if (showDatePicker) {
        val initialMillis = DateUtils.epochDayToUtcMillis(parsedEpochDay ?: todayEpochDay)
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val epochDay = DateUtils.utcMillisToEpochDay(millis)
                            onExpiryDateChange(DateUtils.formatIsoDate(epochDay))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Select Date", color = LeafGreenDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = FreshMutedText)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 88.dp)
            .testTag("add_edit_item_screen")
    ) {
        // Top Bar
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
                        .clickable(onClick = onCancel)
                        .testTag("form_back_button")
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
                        text = if (isEditing) "EDIT KITCHEN ITEM" else "NEW KITCHEN ITEM",
                        style = MaterialTheme.typography.labelSmall,
                        color = LeafGreenDark
                    )
                    Text(
                        text = if (isEditing) "Update Item" else "Add to Kitchen",
                        style = MaterialTheme.typography.headlineLarge,
                        color = FreshInk
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Validation Error Banner
        AnimatedVisibility(visible = formState.validationError != null) {
            formState.validationError?.let { errorMsg ->
                Surface(
                    color = UrgentRedSoft,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, UrgentRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("form_validation_error")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = UrgentRedDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = UrgentRedDark
                        )
                    }
                }
            }
        }

        // Shelf-Life Preset Chips Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
            border = BorderStroke(1.dp, FreshBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LeafGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quick Shelf-Life Presets",
                        style = MaterialTheme.typography.titleSmall,
                        color = FreshInk
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap a preset to automatically compute the expiry date from today:",
                    style = MaterialTheme.typography.bodySmall,
                    color = FreshMutedText
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    PresetChip(
                        label = "Milk (7 days)",
                        onClick = { onApplyPreset("Whole Milk", 7, FoodCategory.DAIRY) },
                        testTag = "preset_chip_milk"
                    )
                    PresetChip(
                        label = "Eggs (21 days)",
                        onClick = { onApplyPreset("Eggs", 21, FoodCategory.DAIRY) },
                        testTag = "preset_chip_eggs"
                    )
                    PresetChip(
                        label = "Chicken/Fish (2 days)",
                        onClick = { onApplyPreset("Chicken / Fish", 2, FoodCategory.MEAT_SEAFOOD) },
                        testTag = "preset_chip_chicken_fish"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Details Form Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FreshCardSurface),
            border = BorderStroke(1.dp, FreshBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // Item Name
                Column {
                    Text(
                        text = "Item Name *",
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = onNameChange,
                        placeholder = { Text("e.g., Baby Spinach, Whole Milk", color = FreshMutedText) },
                        singleLine = true,
                        trailingIcon = {
                            if (formState.name.isNotEmpty()) {
                                IconButton(onClick = { onNameChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear name")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = FreshBorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_item_name")
                    )
                }

                // Storage Location (Fridge, Freezer, Pantry)
                Column {
                    Text(
                        text = "Storage Location *",
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StorageLocation.entries.forEach { loc ->
                            val selected = formState.location == loc
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
                                    .clickable { onLocationChange(loc) }
                                    .testTag("location_option_${loc.name.lowercase()}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = locationIcon(loc),
                                        contentDescription = null,
                                        tint = if (selected) Color.White else FreshInk,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = loc.displayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) Color.White else FreshInk
                                    )
                                }
                            }
                        }
                    }
                }

                // Category Selector (Produce, Dairy, Meat & Seafood, Bakery, Pantry, Frozen, Other)
                Column {
                    Text(
                        text = "Category *",
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FoodCategory.entries.forEach { cat ->
                            val selected = formState.category == cat
                            Surface(
                                color = if (selected) FreshInk else FreshSurfaceSubtle,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) FreshInk else FreshBorderSubtle
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onCategoryChange(cat) }
                                    .testTag("category_option_${cat.name.lowercase()}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = categoryIcon(cat),
                                        contentDescription = null,
                                        tint = if (selected) Color.White else categoryIconTint(cat),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else FreshInk
                                    )
                                }
                            }
                        }
                    }
                }

                // Expiry Date Input + Live Urgency Preview + Calendar Picker
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Expiry Date (YYYY-MM-DD) *",
                            style = MaterialTheme.typography.labelLarge,
                            color = FreshInk
                        )
                        if (previewDaysLeft != null && previewUrgency != null) {
                            UrgencyBadge(
                                daysLeft = previewDaysLeft,
                                level = previewUrgency
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = formState.expiryDateIso,
                            onValueChange = onExpiryDateChange,
                            placeholder = { Text("YYYY-MM-DD", color = FreshMutedText) },
                            singleLine = true,
                            trailingIcon = {
                                if (formState.expiryDateIso.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onExpiryDateChange("") },
                                        modifier = Modifier.testTag("clear_expiry_date_button")
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear date")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LeafGreen,
                                unfocusedBorderColor = FreshBorderSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_expiry_date")
                        )

                        Surface(
                            color = LeafGreenSoft,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showDatePicker = true }
                                .testTag("open_date_picker_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pick Expiry Date",
                                    tint = LeafGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick relative date adjusters
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickDateStepChip(
                            label = "Today",
                            onClick = { onExpiryDateChange(DateUtils.formatIsoDate(todayEpochDay)) }
                        )
                        QuickDateStepChip(
                            label = "-1d",
                            onClick = { onAdjustDateByDays(-1) }
                        )
                        QuickDateStepChip(
                            label = "+1d",
                            onClick = { onAdjustDateByDays(1) }
                        )
                        QuickDateStepChip(
                            label = "+7d",
                            onClick = { onAdjustDateByDays(7) }
                        )
                    }
                }

                // Quantity & Cost Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.labelLarge,
                            color = FreshInk
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = formState.quantity,
                            onValueChange = onQuantityChange,
                            placeholder = { Text("1 pack", color = FreshMutedText) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LeafGreen,
                                unfocusedBorderColor = FreshBorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_item_quantity")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Optional Cost (${currency.symbol})",
                            style = MaterialTheme.typography.labelLarge,
                            color = FreshInk
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = formState.costText,
                            onValueChange = onCostChange,
                            placeholder = { Text("0.00", color = FreshMutedText) },
                            prefix = { Text("${currency.symbol} ", color = FreshMutedText) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LeafGreen,
                                unfocusedBorderColor = FreshBorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_item_cost")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, FreshBorderSubtle),
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("form_cancel_button")
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleSmall,
                    color = FreshInk
                )
            }

            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier
                    .weight(1.4f)
                    .testTag("form_submit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Save Changes" else "Save to Kitchen",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = LeafGreenSoft,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.45f)),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = LeafGreenDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun QuickDateStepChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = FreshSurfaceSubtle,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, FreshBorderSubtle),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = FreshInk,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
