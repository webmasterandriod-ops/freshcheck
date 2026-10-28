package com.example

import com.example.data.local.DateUtils
import com.example.data.model.CurrencyOption
import com.example.data.model.UrgencyLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun demoPantryItems_haveRelativeExpiryDatesAndCosts() {
        val today = DateUtils.todayEpochDay()
        val demoItems = DateUtils.buildDemoPantryItems(today)

        assertEquals(5, demoItems.size)
        assertTrue(demoItems.all { it.isDemo && it.cost > 0.0 })

        val byName = demoItems.associateBy { it.name }
        val spinach = byName["Baby Spinach"]!!
        val chicken = byName["Chicken Breasts"]!!
        val milk = byName["Whole Milk"]!!
        val eggs = byName["Eggs"]!!
        val cream = byName["Heavy Cream"]!!

        assertEquals(0, spinach.daysLeft(today))
        assertEquals(UrgencyLevel.EXPIRED_OR_TODAY, spinach.urgencyLevel(today))

        assertEquals(1, chicken.daysLeft(today))
        assertEquals(UrgencyLevel.EXPIRING_SOON, chicken.urgencyLevel(today))

        assertEquals(2, milk.daysLeft(today))
        assertEquals(UrgencyLevel.EXPIRING_SOON, milk.urgencyLevel(today))

        assertEquals(12, eggs.daysLeft(today))
        assertEquals(UrgencyLevel.FRESH, eggs.urgencyLevel(today))

        assertEquals(-1, cream.daysLeft(today))
        assertEquals(UrgencyLevel.EXPIRED_OR_TODAY, cream.urgencyLevel(today))
    }

    @Test
    fun dateFormattingAndParsing_roundTripsAccurately() {
        val epochDay = DateUtils.ymdToEpochDay(2026, 9, 28) // Oct 28, 2026 (0-based month)
        val iso = DateUtils.formatIsoDate(epochDay)
        assertEquals("2026-10-28", iso)

        val parsed = DateUtils.parseIsoDateToEpochDay(iso)
        assertNotNull(parsed)
        assertEquals(epochDay, parsed)

        assertNull(DateUtils.parseIsoDateToEpochDay("invalid-date"))
        assertNull(DateUtils.parseIsoDateToEpochDay("2026-02-30"))
    }

    @Test
    fun currencyFormatting_switchesSymbolsCorrectly() {
        assertEquals("$4.29", CurrencyOption.USD.formatAmount(4.29))
        assertEquals("€4.29", CurrencyOption.EUR.formatAmount(4.29))
    }
}
