package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ActivityTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
        viewModel.addFarm("Main Farm", "10 Hectares", "Zone A", "Test")
    }

    @Test
    fun testRecordFarmActivityWithCost() {
        val initialActCount = viewModel.activities.value.size
        val initialExpCount = viewModel.expenses.value.size
        viewModel.addActivity(
            farmId = viewModel.farms.value[0].id,
            cropId = null,
            activityType = "Pest Control",
            date = "2026-02-10",
            notes = "Sprayed Cypermethrin",
            cost = 15000.0
        )
        assertEquals(initialActCount + 1, viewModel.activities.value.size)
        assertEquals(initialExpCount + 1, viewModel.expenses.value.size)
        val act = viewModel.activities.value.last()
        assertEquals("Pest Control", act.activityType)
        assertEquals(15000.0, act.cost, 0.001)
    }
}
