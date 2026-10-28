package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CurrencyOption
import com.example.data.model.ItemStatus
import com.example.data.model.PantryItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val reminderWindowDays: Int = 3,
    val currency: CurrencyOption = CurrencyOption.USD,
    val hasSeededDemo: Boolean = false
)

class PantryRepository(
    private val pantryDao: PantryDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("freshcheck_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<UserSettings> = _settingsFlow.asStateFlow()

    val allItemsFlow: Flow<List<PantryItemEntity>> = pantryDao.observeAllItems()

    private fun loadSettings(): UserSettings {
        val window = prefs.getInt(KEY_REMINDER_WINDOW, 3).let {
            if (it in listOf(1, 3, 7)) it else 3
        }
        val currencyCode = prefs.getString(KEY_CURRENCY, CurrencyOption.USD.code) ?: CurrencyOption.USD.code
        val seeded = prefs.getBoolean(KEY_HAS_SEEDED_DEMO, false)
        return UserSettings(
            reminderWindowDays = window,
            currency = CurrencyOption.fromCode(currencyCode),
            hasSeededDemo = seeded
        )
    }

    suspend fun ensureDemoSeededOnFirstRun() {
        val currentSettings = _settingsFlow.value
        if (!currentSettings.hasSeededDemo) {
            val count = pantryDao.getTotalItemCount()
            if (count == 0) {
                val demoItems = DateUtils.buildDemoPantryItems()
                pantryDao.insertItems(demoItems)
            }
            prefs.edit().putBoolean(KEY_HAS_SEEDED_DEMO, true).apply()
            _settingsFlow.value = loadSettings()
        }
    }

    suspend fun resetToDemoPantry() {
        pantryDao.deleteAllItems()
        val demoItems = DateUtils.buildDemoPantryItems()
        pantryDao.insertItems(demoItems)
        prefs.edit().putBoolean(KEY_HAS_SEEDED_DEMO, true).apply()
        _settingsFlow.value = loadSettings()
    }

    fun setReminderWindowDays(days: Int) {
        val validDays = if (days in listOf(1, 3, 7)) days else 3
        prefs.edit().putInt(KEY_REMINDER_WINDOW, validDays).apply()
        _settingsFlow.value = loadSettings()
    }

    fun setCurrency(currency: CurrencyOption) {
        prefs.edit().putString(KEY_CURRENCY, currency.code).apply()
        _settingsFlow.value = loadSettings()
    }

    suspend fun saveItem(item: PantryItemEntity): Long {
        return if (item.id == 0L) {
            pantryDao.insertItem(item)
        } else {
            pantryDao.updateItem(item)
            item.id
        }
    }

    suspend fun markItemUsed(item: PantryItemEntity, todayEpochDay: Long) {
        val updated = item.copy(
            status = ItemStatus.USED.name,
            resolvedAt = System.currentTimeMillis(),
            usedBeforeExpiry = true
        )
        pantryDao.updateItem(updated)
    }

    suspend fun markItemWasted(item: PantryItemEntity) {
        val updated = item.copy(
            status = ItemStatus.WASTED.name,
            resolvedAt = System.currentTimeMillis(),
            usedBeforeExpiry = false
        )
        pantryDao.updateItem(updated)
    }

    suspend fun restoreItemToActive(item: PantryItemEntity) {
        val updated = item.copy(
            status = ItemStatus.ACTIVE.name,
            resolvedAt = null,
            usedBeforeExpiry = false
        )
        pantryDao.updateItem(updated)
    }

    suspend fun deleteItemById(id: Long) {
        pantryDao.deleteItemById(id)
    }

    suspend fun clearAllData() {
        pantryDao.deleteAllItems()
        // Keep hasSeededDemo = true so clearing all shows the Empty State instead of auto-reseeding
        prefs.edit().putBoolean(KEY_HAS_SEEDED_DEMO, true).apply()
        _settingsFlow.value = loadSettings()
    }

    suspend fun buildCsvExport(todayEpochDay: Long): String {
        val allItems = pantryDao.getAllItemsOnce()
        val currency = _settingsFlow.value.currency
        val sb = StringBuilder()
        sb.appendLine("ID,Name,Category,Location,ExpiryDate,DaysLeft,Quantity,Cost,Currency,Status,UsedBeforeExpiry,IsDemo")
        for (item in allItems) {
            val daysLeft = item.daysLeft(todayEpochDay)
            sb.append(item.id).append(',')
            sb.append(escapeCsv(item.name)).append(',')
            sb.append(escapeCsv(item.category)).append(',')
            sb.append(escapeCsv(item.location)).append(',')
            sb.append(escapeCsv(item.expiryDateIso)).append(',')
            sb.append(daysLeft).append(',')
            sb.append(escapeCsv(item.quantity)).append(',')
            sb.append(String.format(java.util.Locale.US, "%.2f", item.cost)).append(',')
            sb.append(currency.code).append(',')
            sb.append(item.status).append(',')
            sb.append(item.usedBeforeExpiry).append(',')
            sb.append(item.isDemo)
            sb.appendLine()
        }
        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        val needsQuotes = value.contains(',') || value.contains('"') || value.contains('\n')
        return if (needsQuotes) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    companion object {
        private const val KEY_REMINDER_WINDOW = "reminder_window_days"
        private const val KEY_CURRENCY = "currency_code"
        private const val KEY_HAS_SEEDED_DEMO = "has_seeded_demo"
    }
}
