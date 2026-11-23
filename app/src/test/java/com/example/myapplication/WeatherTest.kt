package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
    }

    @Test
    fun testSelectWeatherDay() {
        assertEquals("Thu", viewModel.selectedWeatherDay.value)
        viewModel.setSelectedWeatherDay("Mon")
        assertEquals("Mon", viewModel.selectedWeatherDay.value)
    }
}
