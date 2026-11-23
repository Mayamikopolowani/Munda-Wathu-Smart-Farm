package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class InventoryTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
        viewModel.addInventoryItem("Solar Water Pump", "Equipment", "1 Unit", "In Stock")
    }

    @Test
    fun testAddInventoryItem() {
        val initialSize = viewModel.inventory.value.size
        viewModel.addInventoryItem("Knapsack Sprayer", "Equipment", "2 Units", "In Stock")
        assertEquals(initialSize + 1, viewModel.inventory.value.size)
        val item = viewModel.inventory.value.last()
        assertEquals("Knapsack Sprayer", item.itemName)
        assertEquals("In Stock", item.status)
    }

    @Test
    fun testToggleInventoryStatus() {
        val item = viewModel.inventory.value[0]
        assertEquals("In Stock", item.status)
        viewModel.toggleInventoryStatus(item.id)
        val updated = viewModel.inventory.value.find { it.id == item.id }
        assertEquals("Low Stock", updated?.status)
    }
}
