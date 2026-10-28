package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ConfirmClearAllSheet
import com.example.ui.components.ConfirmDeleteSheet
import com.example.ui.components.CsvExportDialog
import com.example.ui.screens.AddEditItemScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.PantryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WasteLogScreen
import com.example.ui.theme.FreshBackground
import com.example.ui.theme.FreshBorderSubtle
import com.example.ui.theme.FreshCardSurface
import com.example.ui.theme.FreshCheckTheme
import com.example.ui.theme.FreshInk
import com.example.ui.theme.FreshMutedText
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenDark
import com.example.ui.theme.LeafGreenSoft
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.FreshCheckViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreshCheckTheme {
                FreshCheckApp()
            }
        }
    }
}

@Composable
fun FreshCheckApp(
    viewModel: FreshCheckViewModel = viewModel(
        factory = FreshCheckViewModel.provideFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // System Document Saver for downloading CSV to user-chosen location
    val saveCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val csvContent = uiState.csvExportResult?.csvContent
        if (uri != null && csvContent != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(csvContent.toByteArray())
                }
            }
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Confirm Delete Sheet
    uiState.itemPendingDelete?.let { pendingItem ->
        ConfirmDeleteSheet(
            itemName = pendingItem.name,
            onConfirmDelete = { viewModel.confirmDeleteItem() },
            onDismiss = { viewModel.cancelDeleteItem() }
        )
    }

    // Confirm Clear All Sheet
    if (uiState.showClearAllConfirm) {
        ConfirmClearAllSheet(
            onConfirmClear = { viewModel.confirmClearAllData() },
            onDismiss = { viewModel.cancelClearAllData() }
        )
    }

    // CSV Export Dialog
    uiState.csvExportResult?.let { csvResult ->
        CsvExportDialog(
            result = csvResult,
            onSaveToFilePicker = {
                saveCsvLauncher.launch(csvResult.fileName)
            },
            onShareCsv = {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "FreshCheck Export - ${csvResult.fileName}")
                    putExtra(Intent.EXTRA_TEXT, csvResult.csvContent)
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export CSV"))
            },
            onDismiss = { viewModel.dismissCsvDialog() }
        )
    }

    Scaffold(
        containerColor = FreshBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 84.dp)
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .background(FreshBackground)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
            ) {
                when (val screen = uiState.activeScreen) {
                    is ActiveScreen.MainTabs -> {
                        when (uiState.currentTab) {
                            BottomTab.HOME -> {
                                HomeScreen(
                                    uiState = uiState,
                                    onSelectFilter = viewModel::selectHomeFilter,
                                    onItemClick = { viewModel.openItemDetail(it.id) },
                                    onMarkUsed = viewModel::markUsed,
                                    onMarkWasted = viewModel::markWasted,
                                    onAddItemClick = viewModel::openAddItemForm,
                                    onResetDemo = viewModel::resetDemoPantry
                                )
                            }
                            BottomTab.PANTRY -> {
                                PantryScreen(
                                    uiState = uiState,
                                    onSelectFilter = viewModel::selectPantryFilter,
                                    onSelectCategory = viewModel::selectPantryCategoryFilter,
                                    onSearchQueryChange = viewModel::updateSearchQuery,
                                    onItemClick = { viewModel.openItemDetail(it.id) },
                                    onMarkUsed = viewModel::markUsed,
                                    onMarkWasted = viewModel::markWasted,
                                    onAddItemClick = viewModel::openAddItemForm,
                                    onResetDemo = viewModel::resetDemoPantry
                                )
                            }
                            BottomTab.WASTE_LOG -> {
                                WasteLogScreen(
                                    uiState = uiState,
                                    onRestoreItem = viewModel::restoreItemToActive
                                )
                            }
                            BottomTab.SETTINGS -> {
                                SettingsScreen(
                                    uiState = uiState,
                                    onSelectReminderWindow = viewModel::setReminderWindow,
                                    onSelectCurrency = viewModel::setCurrency,
                                    onExportCsv = { viewModel.exportCsvToDevice(context) },
                                    onResetDemo = viewModel::resetDemoPantry,
                                    onRequestClearAll = viewModel::requestClearAllData
                                )
                            }
                        }
                    }

                    is ActiveScreen.AddOrEditItem -> {
                        AddEditItemScreen(
                            formState = uiState.formState,
                            todayEpochDay = uiState.todayEpochDay,
                            currency = uiState.settings.currency,
                            onNameChange = viewModel::updateFormName,
                            onCategoryChange = viewModel::updateFormCategory,
                            onLocationChange = viewModel::updateFormLocation,
                            onExpiryDateChange = viewModel::updateFormExpiryDateIso,
                            onQuantityChange = viewModel::updateFormQuantity,
                            onCostChange = viewModel::updateFormCost,
                            onApplyPreset = viewModel::applyShelfLifePreset,
                            onAdjustDateByDays = viewModel::adjustFormDateByDays,
                            onSubmit = viewModel::submitForm,
                            onCancel = viewModel::navigateBackToMain
                        )
                    }

                    is ActiveScreen.ItemDetail -> {
                        val item = uiState.activeItems.firstOrNull { it.id == screen.itemId }
                        if (item != null) {
                            ItemDetailScreen(
                                item = item,
                                todayEpochDay = uiState.todayEpochDay,
                                currency = uiState.settings.currency,
                                onBack = viewModel::navigateBackToMain,
                                onEdit = { viewModel.openEditItemForm(item) },
                                onMarkUsed = { viewModel.markUsed(item) },
                                onMarkWasted = { viewModel.markWasted(item) },
                                onRequestDelete = { viewModel.requestDeleteItem(item) }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                viewModel.navigateBackToMain()
                            }
                        }
                    }
                }

                // Bottom Tab Bar (Home, Pantry, Waste Log, Settings) + Center Floating + Add Button
                FreshKitchenBottomBar(
                    currentTab = uiState.currentTab,
                    isMainTabsActive = uiState.activeScreen is ActiveScreen.MainTabs,
                    onSelectTab = viewModel::selectTab,
                    onCenterAddClick = viewModel::openAddItemForm,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun FreshKitchenBottomBar(
    currentTab: BottomTab,
    isMainTabsActive: Boolean,
    onSelectTab: (BottomTab) -> Unit,
    onCenterAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("bottom_navigation_bar")
    ) {
        Surface(
            color = FreshCardSurface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, FreshBorderSubtle),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp)
            ) {
                BottomTabItem(
                    tab = BottomTab.HOME,
                    selected = isMainTabsActive && currentTab == BottomTab.HOME,
                    activeIcon = Icons.Filled.Home,
                    inactiveIcon = Icons.Outlined.Home,
                    onClick = { onSelectTab(BottomTab.HOME) },
                    testTag = "nav_tab_home",
                    modifier = Modifier.weight(1f)
                )

                BottomTabItem(
                    tab = BottomTab.PANTRY,
                    selected = isMainTabsActive && currentTab == BottomTab.PANTRY,
                    activeIcon = Icons.Filled.Inventory2,
                    inactiveIcon = Icons.Outlined.Inventory2,
                    onClick = { onSelectTab(BottomTab.PANTRY) },
                    testTag = "nav_tab_pantry",
                    modifier = Modifier.weight(1f)
                )

                // Center space for the floating + Add button
                Spacer(modifier = Modifier.width(68.dp))

                BottomTabItem(
                    tab = BottomTab.WASTE_LOG,
                    selected = isMainTabsActive && currentTab == BottomTab.WASTE_LOG,
                    activeIcon = Icons.Filled.PieChart,
                    inactiveIcon = Icons.Outlined.PieChart,
                    onClick = { onSelectTab(BottomTab.WASTE_LOG) },
                    testTag = "nav_tab_waste_log",
                    modifier = Modifier.weight(1f)
                )

                BottomTabItem(
                    tab = BottomTab.SETTINGS,
                    selected = isMainTabsActive && currentTab == BottomTab.SETTINGS,
                    activeIcon = Icons.Filled.Settings,
                    inactiveIcon = Icons.Outlined.Settings,
                    onClick = { onSelectTab(BottomTab.SETTINGS) },
                    testTag = "nav_tab_settings",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Floating + Add Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = (-16).dp)
                .size(58.dp)
                .shadow(elevation = 10.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(LeafGreen)
                .clickable(onClick = onCenterAddClick)
                .testTag("center_add_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Item",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun BottomTabItem(
    tab: BottomTab,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (selected) LeafGreenSoft else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = tab.label,
                tint = if (selected) LeafGreenDark else FreshMutedText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) LeafGreenDark else FreshMutedText
        )
    }
}
