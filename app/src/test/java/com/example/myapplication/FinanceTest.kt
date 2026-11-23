package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FinanceTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
        viewModel.addFarm("Main Farm", "10 Hectares", "Zone A", "Test")
        viewModel.addCrop(
            farmId = viewModel.farms.value[0].id,
            cropType = "Maize",
            variety = "SC 403",
            acreage = "5 Acres",
            growthStage = "Flowering",
            plantingDate = "2025-11-15",
            expectedHarvestDate = "2026-04-20",
            seedInfo = "Certified"
        )
    }

    @Test
    fun testAddExpenseAndActivity() {
        val initialExpCount = viewModel.expenses.value.size
        viewModel.addExpense(
            farmId = viewModel.farms.value[0].id,
            category = "Fertiliser",
            amount = 50000.0,
            date = "2026-03-01",
            description = "NPK top dressing"
        )
        assertEquals(initialExpCount + 1, viewModel.expenses.value.size)
        val exp = viewModel.expenses.value.last()
        assertEquals(50000.0, exp.amount, 0.001)
        assertEquals("Fertiliser", exp.category)
    }

    @Test
    fun testAddHarvestAndSalesRecord() {
        val initialHarvCount = viewModel.harvests.value.size
        val crop = viewModel.crops.value[0]
        viewModel.addHarvest(
            cropId = crop.id,
            quantity = 500.0,
            unitPrice = 400.0,
            buyer = "Local Grain Trader",
            date = "2026-03-10"
        )
        assertEquals(initialHarvCount + 1, viewModel.harvests.value.size)
        val harv = viewModel.harvests.value.last()
        assertEquals(200000.0, harv.totalSales, 0.001)
        assertEquals("Local Grain Trader", harv.buyer)
    }
}
