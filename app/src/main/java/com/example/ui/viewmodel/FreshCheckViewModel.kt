package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DateUtils
import com.example.data.local.FreshCheckDatabase
import com.example.data.local.PantryRepository
import com.example.data.local.UserSettings
import com.example.data.model.CurrencyOption
import com.example.data.model.FoodCategory
import com.example.data.model.HomeFilter
import com.example.data.model.ItemStatus
import com.example.data.model.PantryFilter
import com.example.data.model.PantryItemEntity
import com.example.data.model.StorageLocation
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BottomTab(val label: String) {
    HOME("Home"),
    PANTRY("Pantry"),
    WASTE_LOG("Waste Log"),
    SETTINGS("Settings")
}

sealed class ActiveScreen {
    data object MainTabs : ActiveScreen()
    data class AddOrEditItem(val itemToEdit: PantryItemEntity? = null) : ActiveScreen()
    data class ItemDetail(val itemId: Long) : ActiveScreen()
}

data class FormInputState(
    val editingId: Long = 0L,
    val isDemo: Boolean = false,
    val name: String = "",
    val category: FoodCategory = FoodCategory.PRODUCE,
    val location: StorageLocation = StorageLocation.FRIDGE,
    val expiryDateIso: String = "",
    val quantity: String = "1 unit",
    val costText: String = "",
    val validationError: String? = null
)

data class CsvExportResult(
    val fileName: String,
    val filePath: String,
    val csvContent: String
)

data class FreshCheckUiState(
    val isInitialized: Boolean = false,
    val todayEpochDay: Long = DateUtils.todayEpochDay(),
    val currentTab: BottomTab = BottomTab.HOME,
    val activeScreen: ActiveScreen = ActiveScreen.MainTabs,
    val homeFilter: HomeFilter = HomeFilter.USE_FIRST,
    val pantryFilter: PantryFilter = PantryFilter.ALL,
    val pantryCategoryFilter: FoodCategory? = null,
    val searchQuery: String = "",
    val settings: UserSettings = UserSettings(),
    val activeItems: List<PantryItemEntity> = emptyList(),
    val useFirstItems: List<PantryItemEntity> = emptyList(),
    val thisWeekItems: List<PantryItemEntity> = emptyList(),
    val expiredItems: List<PantryItemEntity> = emptyList(),
    val freshItems: List<PantryItemEntity> = emptyList(),
    val homeDisplayedItems: List<PantryItemEntity> = emptyList(),
    val pantryDisplayedItems: List<PantryItemEntity> = emptyList(),
    val savedItems: List<PantryItemEntity> = emptyList(),
    val wastedItems: List<PantryItemEntity> = emptyList(),
    val savedCount: Int = 0,
    val totalValueSaved: Double = 0.0,
    val wastedCount: Int = 0,
    val totalCostWasted: Double = 0.0,
    val hasDemoItems: Boolean = false,
    val formState: FormInputState = FormInputState(),
    val itemPendingDelete: PantryItemEntity? = null,
    val showClearAllConfirm: Boolean = false,
    val csvExportResult: CsvExportResult? = null,
    val snackbarMessage: String? = null
)

private data class LocalUiControls(
    val currentTab: BottomTab = BottomTab.HOME,
    val activeScreen: ActiveScreen = ActiveScreen.MainTabs,
    val homeFilter: HomeFilter = HomeFilter.USE_FIRST,
    val pantryFilter: PantryFilter = PantryFilter.ALL,
    val pantryCategoryFilter: FoodCategory? = null,
    val searchQuery: String = "",
    val formState: FormInputState = FormInputState(),
    val itemPendingDelete: PantryItemEntity? = null,
    val showClearAllConfirm: Boolean = false,
    val csvExportResult: CsvExportResult? = null,
    val snackbarMessage: String? = null
)

class FreshCheckViewModel(
    private val repository: PantryRepository
) : ViewModel() {

    private val controls = MutableStateFlow(LocalUiControls())

    init {
        viewModelScope.launch {
            repository.ensureDemoSeededOnFirstRun()
        }
    }

    val uiState: StateFlow<FreshCheckUiState> = combine(
        repository.allItemsFlow,
        repository.settingsFlow,
        controls
    ) { allItems, settings, ctrl ->
        val today = DateUtils.todayEpochDay()

        val active = allItems
            .filter { it.itemStatus == ItemStatus.ACTIVE }
            .sortedWith(compareBy<PantryItemEntity> { it.expiryEpochDay }.thenBy { it.id })

        val saved = allItems
            .filter { it.itemStatus == ItemStatus.USED && it.usedBeforeExpiry }
            .sortedByDescending { it.resolvedAt ?: it.createdAt }

        val wasted = allItems
            .filter { it.itemStatus == ItemStatus.WASTED }
            .sortedByDescending { it.resolvedAt ?: it.createdAt }

        // Use First list respects the user's Reminder Window setting (1 / 3 / 7 days)
        // Includes items expiring within reminderWindowDays (plus already-expired items needing immediate action), ranked by urgency
        val useFirst = active.filter { item ->
            val days = item.daysLeft(today)
            days <= settings.reminderWindowDays
        }

        val thisWeek = active.filter { item ->
            val days = item.daysLeft(today)
            days in 0..7
        }

        val expired = active.filter { item ->
            item.daysLeft(today) < 0
        }

        val fresh = active.filter { item ->
            item.daysLeft(today) > 7
        }

        val homeDisplayed = when (ctrl.homeFilter) {
            HomeFilter.USE_FIRST -> useFirst
            HomeFilter.THIS_WEEK -> thisWeek
            HomeFilter.ALL -> active
            HomeFilter.EXPIRED -> expired
        }

        val query = ctrl.searchQuery.trim().lowercase()
        val pantryDisplayed = active.filter { item ->
            val matchesFilter = when (ctrl.pantryFilter) {
                PantryFilter.ALL -> true
                PantryFilter.FRIDGE -> item.storageLocation == StorageLocation.FRIDGE
                PantryFilter.FREEZER -> item.storageLocation == StorageLocation.FREEZER
                PantryFilter.PANTRY -> item.storageLocation == StorageLocation.PANTRY
                PantryFilter.EXPIRED -> item.daysLeft(today) < 0
            }
            val matchesCategory = ctrl.pantryCategoryFilter == null || item.foodCategory == ctrl.pantryCategoryFilter
            val matchesSearch = query.isEmpty() ||
                item.name.lowercase().contains(query) ||
                item.category.lowercase().contains(query) ||
                item.location.lowercase().contains(query)
            matchesFilter && matchesCategory && matchesSearch
        }

        FreshCheckUiState(
            isInitialized = true,
            todayEpochDay = today,
            currentTab = ctrl.currentTab,
            activeScreen = ctrl.activeScreen,
            homeFilter = ctrl.homeFilter,
            pantryFilter = ctrl.pantryFilter,
            pantryCategoryFilter = ctrl.pantryCategoryFilter,
            searchQuery = ctrl.searchQuery,
            settings = settings,
            activeItems = active,
            useFirstItems = useFirst,
            thisWeekItems = thisWeek,
            expiredItems = expired,
            freshItems = fresh,
            homeDisplayedItems = homeDisplayed,
            pantryDisplayedItems = pantryDisplayed,
            savedItems = saved,
            wastedItems = wasted,
            savedCount = saved.size,
            totalValueSaved = saved.sumOf { it.cost },
            wastedCount = wasted.size,
            totalCostWasted = wasted.sumOf { it.cost },
            hasDemoItems = active.any { it.isDemo },
            formState = ctrl.formState,
            itemPendingDelete = ctrl.itemPendingDelete,
            showClearAllConfirm = ctrl.showClearAllConfirm,
            csvExportResult = ctrl.csvExportResult,
            snackbarMessage = ctrl.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FreshCheckUiState()
    )

    fun selectTab(tab: BottomTab) {
        controls.value = controls.value.copy(
            currentTab = tab,
            activeScreen = ActiveScreen.MainTabs
        )
    }

    fun selectHomeFilter(filter: HomeFilter) {
        controls.value = controls.value.copy(homeFilter = filter)
    }

    fun selectPantryFilter(filter: PantryFilter) {
        controls.value = controls.value.copy(pantryFilter = filter)
    }

    fun selectPantryCategoryFilter(category: FoodCategory?) {
        val current = controls.value.pantryCategoryFilter
        controls.value = controls.value.copy(
            pantryCategoryFilter = if (current == category) null else category
        )
    }

    fun updateSearchQuery(query: String) {
        controls.value = controls.value.copy(searchQuery = query)
    }

    fun openAddItemForm() {
        val defaultExpiryIso = DateUtils.formatIsoDate(DateUtils.todayEpochDay() + 3)
        controls.value = controls.value.copy(
            activeScreen = ActiveScreen.AddOrEditItem(null),
            formState = FormInputState(
                editingId = 0L,
                isDemo = false,
                name = "",
                category = FoodCategory.PRODUCE,
                location = StorageLocation.FRIDGE,
                expiryDateIso = defaultExpiryIso,
                quantity = "1 unit",
                costText = "",
                validationError = null
            )
        )
    }

    fun openEditItemForm(item: PantryItemEntity) {
        controls.value = controls.value.copy(
            activeScreen = ActiveScreen.AddOrEditItem(item),
            formState = FormInputState(
                editingId = item.id,
                isDemo = item.isDemo,
                name = item.name,
                category = item.foodCategory,
                location = item.storageLocation,
                expiryDateIso = item.expiryDateIso,
                quantity = item.quantity,
                costText = if (item.cost > 0.0) String.format(java.util.Locale.US, "%.2f", item.cost) else "",
                validationError = null
            )
        )
    }

    fun openItemDetail(itemId: Long) {
        controls.value = controls.value.copy(
            activeScreen = ActiveScreen.ItemDetail(itemId)
        )
    }

    fun navigateBackToMain() {
        controls.value = controls.value.copy(
            activeScreen = ActiveScreen.MainTabs,
            formState = controls.value.formState.copy(validationError = null)
        )
    }

    // Form field updaters
    fun updateFormName(name: String) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(name = name, validationError = null)
        )
    }

    fun updateFormCategory(category: FoodCategory) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(category = category, validationError = null)
        )
    }

    fun updateFormLocation(location: StorageLocation) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(location = location, validationError = null)
        )
    }

    fun updateFormExpiryDateIso(dateIso: String) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(expiryDateIso = dateIso, validationError = null)
        )
    }

    fun updateFormQuantity(quantity: String) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(quantity = quantity, validationError = null)
        )
    }

    fun updateFormCost(costText: String) {
        controls.value = controls.value.copy(
            formState = controls.value.formState.copy(costText = costText, validationError = null)
        )
    }

    /**
     * Applies a shelf-life preset chip (Milk 7 days, Eggs 21 days, Chicken/Fish 2 days).
     * Fills the expiry date relative to today, and if the item name is currently blank,
     * pre-fills a helpful name/category/location.
     */
    fun applyShelfLifePreset(presetLabel: String, daysFromToday: Int, suggestedCategory: FoodCategory) {
        val targetEpochDay = DateUtils.todayEpochDay() + daysFromToday
        val targetIso = DateUtils.formatIsoDate(targetEpochDay)
        val currentForm = controls.value.formState
        val updatedName = if (currentForm.name.isBlank()) presetLabel else currentForm.name
        controls.value = controls.value.copy(
            formState = currentForm.copy(
                name = updatedName,
                category = if (currentForm.name.isBlank()) suggestedCategory else currentForm.category,
                location = StorageLocation.FRIDGE,
                expiryDateIso = targetIso,
                validationError = null
            )
        )
    }

    fun adjustFormDateByDays(deltaDays: Int) {
        val currentIso = controls.value.formState.expiryDateIso
        val baseEpochDay = DateUtils.parseIsoDateToEpochDay(currentIso) ?: DateUtils.todayEpochDay()
        val newIso = DateUtils.formatIsoDate(baseEpochDay + deltaDays)
        updateFormExpiryDateIso(newIso)
    }

    fun submitForm() {
        val form = controls.value.formState
        val trimmedName = form.name.trim()
        if (trimmedName.isEmpty()) {
            controls.value = controls.value.copy(
                formState = form.copy(validationError = "Item name cannot be empty. Please enter a name.")
            )
            return
        }

        val trimmedDate = form.expiryDateIso.trim()
        if (trimmedDate.isEmpty()) {
            controls.value = controls.value.copy(
                formState = form.copy(validationError = "Expiry date is missing. Please select or enter a date (YYYY-MM-DD).")
            )
            return
        }

        val parsedEpochDay = DateUtils.parseIsoDateToEpochDay(trimmedDate)
        if (parsedEpochDay == null) {
            controls.value = controls.value.copy(
                formState = form.copy(validationError = "Invalid expiry date format. Please use YYYY-MM-DD (e.g. 2026-11-04).")
            )
            return
        }

        val trimmedCost = form.costText.trim()
        val parsedCost = if (trimmedCost.isEmpty()) {
            0.0
        } else {
            val number = trimmedCost.toDoubleOrNull()
            if (number == null) {
                controls.value = controls.value.copy(
                    formState = form.copy(validationError = "Invalid cost value. Please enter a valid number (e.g. 4.50).")
                )
                return
            }
            if (number < 0.0) {
                controls.value = controls.value.copy(
                    formState = form.copy(validationError = "Cost cannot be negative. Please enter 0 or a positive amount.")
                )
                return
            }
            number
        }

        val finalQuantity = form.quantity.trim().ifEmpty { "1 unit" }
        val normalizedIso = DateUtils.formatIsoDate(parsedEpochDay)

        viewModelScope.launch {
            val entity = PantryItemEntity(
                id = form.editingId,
                name = trimmedName,
                category = form.category.displayName,
                location = form.location.displayName,
                expiryEpochDay = parsedEpochDay,
                expiryDateIso = normalizedIso,
                quantity = finalQuantity,
                cost = parsedCost,
                status = ItemStatus.ACTIVE.name,
                isDemo = form.isDemo
            )
            val savedId = repository.saveItem(entity)
            val isEditing = form.editingId != 0L
            controls.value = controls.value.copy(
                activeScreen = if (isEditing) ActiveScreen.ItemDetail(savedId) else ActiveScreen.MainTabs,
                snackbarMessage = if (isEditing) "Updated \"$trimmedName\"" else "Added \"$trimmedName\" to ${form.location.displayName}"
            )
        }
    }

    fun markUsed(item: PantryItemEntity) {
        viewModelScope.launch {
            val today = DateUtils.todayEpochDay()
            repository.markItemUsed(item, today)
            val currency = repository.settingsFlow.value.currency
            val valueStr = if (item.cost > 0) " (${currency.formatAmount(item.cost)} saved)" else ""
            controls.value = controls.value.copy(
                activeScreen = ActiveScreen.MainTabs,
                snackbarMessage = "Marked \"${item.name}\" as Used$valueStr"
            )
        }
    }

    fun markWasted(item: PantryItemEntity) {
        viewModelScope.launch {
            repository.markItemWasted(item)
            val currency = repository.settingsFlow.value.currency
            val valueStr = if (item.cost > 0) " (${currency.formatAmount(item.cost)} wasted)" else ""
            controls.value = controls.value.copy(
                activeScreen = ActiveScreen.MainTabs,
                snackbarMessage = "Logged \"${item.name}\" in Waste Log$valueStr"
            )
        }
    }

    fun restoreItemToActive(item: PantryItemEntity) {
        viewModelScope.launch {
            repository.restoreItemToActive(item)
            controls.value = controls.value.copy(
                snackbarMessage = "Restored \"${item.name}\" to Pantry"
            )
        }
    }

    fun requestDeleteItem(item: PantryItemEntity) {
        controls.value = controls.value.copy(itemPendingDelete = item)
    }

    fun cancelDeleteItem() {
        controls.value = controls.value.copy(itemPendingDelete = null)
    }

    fun confirmDeleteItem() {
        val target = controls.value.itemPendingDelete ?: return
        viewModelScope.launch {
            repository.deleteItemById(target.id)
            controls.value = controls.value.copy(
                itemPendingDelete = null,
                activeScreen = ActiveScreen.MainTabs,
                snackbarMessage = "Deleted \"${target.name}\""
            )
        }
    }

    fun requestClearAllData() {
        controls.value = controls.value.copy(showClearAllConfirm = true)
    }

    fun cancelClearAllData() {
        controls.value = controls.value.copy(showClearAllConfirm = false)
    }

    fun confirmClearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            controls.value = controls.value.copy(
                showClearAllConfirm = false,
                activeScreen = ActiveScreen.MainTabs,
                snackbarMessage = "All kitchen data cleared"
            )
        }
    }

    fun resetDemoPantry() {
        viewModelScope.launch {
            repository.resetToDemoPantry()
            controls.value = controls.value.copy(
                activeScreen = ActiveScreen.MainTabs,
                snackbarMessage = "Demo Pantry seeded with live relative expiry dates"
            )
        }
    }

    fun setReminderWindow(days: Int) {
        repository.setReminderWindowDays(days)
        controls.value = controls.value.copy(
            snackbarMessage = "Use First reminder window set to $days ${if (days == 1) "day" else "days"}"
        )
    }

    fun setCurrency(currency: CurrencyOption) {
        repository.setCurrency(currency)
        controls.value = controls.value.copy(
            snackbarMessage = "Display currency changed to ${currency.label}"
        )
    }

    fun exportCsvToDevice(context: Context) {
        viewModelScope.launch {
            val today = DateUtils.todayEpochDay()
            val csvContent = repository.buildCsvExport(today)
            val fileName = "freshcheck_export_${DateUtils.formatIsoDate(today)}.csv"
            val exportDir = context.getExternalFilesDir(null) ?: context.filesDir
            val outFile = File(exportDir, fileName)
            runCatching {
                outFile.writeText(csvContent)
            }
            controls.value = controls.value.copy(
                csvExportResult = CsvExportResult(
                    fileName = fileName,
                    filePath = outFile.absolutePath,
                    csvContent = csvContent
                ),
                snackbarMessage = "Exported $fileName"
            )
        }
    }

    fun dismissCsvDialog() {
        controls.value = controls.value.copy(csvExportResult = null)
    }

    fun clearSnackbar() {
        controls.value = controls.value.copy(snackbarMessage = null)
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = FreshCheckDatabase.getInstance(context)
                    val repo = PantryRepository(db.pantryDao(), context)
                    return FreshCheckViewModel(repo) as T
                }
            }
        }
    }
}
