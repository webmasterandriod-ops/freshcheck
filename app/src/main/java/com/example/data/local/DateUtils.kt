package com.example.data.local

import com.example.data.model.FoodCategory
import com.example.data.model.ItemStatus
import com.example.data.model.PantryItemEntity
import com.example.data.model.StorageLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val utcZone: TimeZone = TimeZone.getTimeZone("UTC")
    private const val MILLIS_PER_DAY = 86_400_000L

    /**
     * Returns today's calendar date converted to UTC epoch days so day-difference
     * calculations are completely immune to daylight-saving shifts.
     */
    fun todayEpochDay(): Long {
        val localCal = Calendar.getInstance()
        val year = localCal.get(Calendar.YEAR)
        val month = localCal.get(Calendar.MONTH)
        val day = localCal.get(Calendar.DAY_OF_MONTH)
        return ymdToEpochDay(year, month, day)
    }

    /**
     * Converts year, 0-based month, and dayOfMonth into epoch days since 1970-01-01.
     */
    fun ymdToEpochDay(year: Int, monthZeroBased: Int, dayOfMonth: Int): Long {
        val utcCal = Calendar.getInstance(utcZone).apply {
            clear()
            set(year, monthZeroBased, dayOfMonth, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return utcCal.timeInMillis / MILLIS_PER_DAY
    }

    /**
     * Converts epoch days back into a UTC Calendar for formatting.
     */
    fun epochDayToUtcCalendar(epochDay: Long): Calendar {
        return Calendar.getInstance(utcZone).apply {
            clear()
            timeInMillis = epochDay * MILLIS_PER_DAY
        }
    }

    /**
     * Formats an epoch day as YYYY-MM-DD.
     */
    fun formatIsoDate(epochDay: Long): String {
        val cal = epochDayToUtcCalendar(epochDay)
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    /**
     * Formats an epoch day as a friendly readable date, e.g. "Oct 28, 2026".
     */
    fun formatFriendlyDate(epochDay: Long): String {
        val cal = epochDayToUtcCalendar(epochDay)
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US).apply {
            timeZone = utcZone
        }
        return sdf.format(cal.time)
    }

    /**
     * Parses a YYYY-MM-DD string strictly into an epochDay, or returns null if invalid.
     */
    fun parseIsoDateToEpochDay(isoString: String): Long? {
        val trimmed = isoString.trim()
        val parts = trimmed.split("-")
        if (parts.size != 3) return null
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null
        if (year !in 2000..2100 || month !in 1..12 || day !in 1..31) return null

        val cal = Calendar.getInstance(utcZone).apply {
            isLenient = false
            clear()
            try {
                set(year, month - 1, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
                // Trigger validation
                timeInMillis
            } catch (e: Exception) {
                return null
            }
        }
        return cal.timeInMillis / MILLIS_PER_DAY
    }

    /**
     * Converts UTC millis from Material3 DatePickerState into an epochDay.
     */
    fun utcMillisToEpochDay(utcMillis: Long): Long {
        val cal = Calendar.getInstance(utcZone).apply {
            timeInMillis = utcMillis
        }
        return ymdToEpochDay(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun epochDayToUtcMillis(epochDay: Long): Long {
        return epochDay * MILLIS_PER_DAY
    }

    /**
     * Human-readable days-left status badge text.
     */
    fun formatDaysLeftBadge(daysLeft: Int): String {
        return when {
            daysLeft < -1 -> "Expired ${-daysLeft}d ago"
            daysLeft == -1 -> "Expired 1d ago"
            daysLeft == 0 -> "Expires today"
            daysLeft == 1 -> "1d left"
            else -> "${daysLeft}d left"
        }
    }

    /**
     * Generates the 5 required DEMO pantry items with expiry dates dynamically computed
     * relative to today in code:
     * - Baby Spinach: expiring today (0 days)
     * - Chicken Breasts: in 1 day (+1 day)
     * - Whole Milk: in 2 days (+2 days)
     * - Eggs: in 12 days (+12 days)
     * - Heavy Cream: expired 1 day ago (-1 day)
     */
    fun buildDemoPantryItems(baseEpochDay: Long = todayEpochDay()): List<PantryItemEntity> {
        fun item(
            name: String,
            category: FoodCategory,
            location: StorageLocation,
            offsetDays: Int,
            quantity: String,
            cost: Double
        ): PantryItemEntity {
            val targetEpochDay = baseEpochDay + offsetDays
            return PantryItemEntity(
                name = name,
                category = category.displayName,
                location = location.displayName,
                expiryEpochDay = targetEpochDay,
                expiryDateIso = formatIsoDate(targetEpochDay),
                quantity = quantity,
                cost = cost,
                status = ItemStatus.ACTIVE.name,
                isDemo = true
            )
        }

        return listOf(
            item(
                name = "Baby Spinach",
                category = FoodCategory.PRODUCE,
                location = StorageLocation.FRIDGE,
                offsetDays = 0,
                quantity = "1 bag (200g)",
                cost = 3.49
            ),
            item(
                name = "Chicken Breasts",
                category = FoodCategory.MEAT_SEAFOOD,
                location = StorageLocation.FRIDGE,
                offsetDays = 1,
                quantity = "500g pack",
                cost = 8.99
            ),
            item(
                name = "Whole Milk",
                category = FoodCategory.DAIRY,
                location = StorageLocation.FRIDGE,
                offsetDays = 2,
                quantity = "1 carton (1L)",
                cost = 4.29
            ),
            item(
                name = "Eggs",
                category = FoodCategory.DAIRY,
                location = StorageLocation.FRIDGE,
                offsetDays = 12,
                quantity = "12 large",
                cost = 5.49
            ),
            item(
                name = "Heavy Cream",
                category = FoodCategory.DAIRY,
                location = StorageLocation.FRIDGE,
                offsetDays = -1,
                quantity = "1 carton (250ml)",
                cost = 3.79
            )
        )
    }
}
