package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FoodCategory(val displayName: String) {
    PRODUCE("Produce"),
    DAIRY("Dairy"),
    MEAT_SEAFOOD("Meat & Seafood"),
    BAKERY("Bakery"),
    PANTRY("Pantry"),
    FROZEN("Frozen"),
    OTHER("Other");

    companion object {
        fun fromName(value: String): FoodCategory {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                    it.displayName.equals(value, ignoreCase = true)
            } ?: OTHER
        }
    }
}

enum class StorageLocation(val displayName: String) {
    FRIDGE("Fridge"),
    FREEZER("Freezer"),
    PANTRY("Pantry");

    companion object {
        fun fromName(value: String): StorageLocation {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                    it.displayName.equals(value, ignoreCase = true)
            } ?: FRIDGE
        }
    }
}

enum class ItemStatus {
    ACTIVE,
    USED,
    WASTED
}

enum class UrgencyLevel {
    EXPIRED_OR_TODAY, // <= 0 days left (Red #D64545)
    EXPIRING_SOON,    // 1..7 days left (Amber #E8A020)
    FRESH             // > 7 days left (Leaf Green #2E9E5B)
}

enum class CurrencyOption(val code: String, val symbol: String, val label: String) {
    USD("USD", "$", "USD ($)"),
    EUR("EUR", "€", "EUR (€)");

    fun formatAmount(amount: Double): String {
        return String.format(java.util.Locale.US, "%s%.2f", symbol, amount)
    }

    companion object {
        fun fromCode(code: String): CurrencyOption {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}

enum class HomeFilter(val label: String) {
    USE_FIRST("Use First"),
    THIS_WEEK("This Week"),
    ALL("All"),
    EXPIRED("Expired")
}

enum class PantryFilter(val label: String) {
    ALL("All"),
    FRIDGE("Fridge"),
    FREEZER("Freezer"),
    PANTRY("Pantry"),
    EXPIRED("Expired")
}

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val location: String,
    val expiryEpochDay: Long,
    val expiryDateIso: String,
    val quantity: String,
    val cost: Double = 0.0,
    val status: String = ItemStatus.ACTIVE.name,
    val isDemo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val usedBeforeExpiry: Boolean = false
) {
    val foodCategory: FoodCategory
        get() = FoodCategory.fromName(category)

    val storageLocation: StorageLocation
        get() = StorageLocation.fromName(location)

    val itemStatus: ItemStatus
        get() = runCatching { ItemStatus.valueOf(status) }.getOrDefault(ItemStatus.ACTIVE)

    fun daysLeft(todayEpochDay: Long): Int {
        return (expiryEpochDay - todayEpochDay).toInt()
    }

    fun urgencyLevel(todayEpochDay: Long): UrgencyLevel {
        val days = daysLeft(todayEpochDay)
        return when {
            days <= 0 -> UrgencyLevel.EXPIRED_OR_TODAY
            days in 1..7 -> UrgencyLevel.EXPIRING_SOON
            else -> UrgencyLevel.FRESH
        }
    }
}
