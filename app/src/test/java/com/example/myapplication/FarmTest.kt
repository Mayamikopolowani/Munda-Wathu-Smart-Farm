package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FarmTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
    }

    @Test
    fun testAddFarmSuccessfully() {
        val initialCount = viewModel.farms.value.size
        viewModel.addFarm(
            name = "Hillside Plot",
            size = "8 Hectares",
            location = "Zone C - West",
            description = "Upper terrace farming plot"
        )
        assertEquals(initialCount + 1, viewModel.farms.value.size)
        val farm = viewModel.farms.value.last()
        assertEquals("Hillside Plot", farm.name)
        assertEquals("8 Hectares", farm.size)
        assertEquals("Zone C - West", farm.location)
    }
}
