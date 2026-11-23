package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CropTest {

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
    fun testAddCropSuccessfully() {
        val initialCount = viewModel.crops.value.size
        viewModel.addCrop(
            farmId = viewModel.farms.value[0].id,
            cropType = "Groundnuts",
            variety = "CG7",
            acreage = "2.0 Acres",
            growthStage = "Germination",
            plantingDate = "2026-01-20",
            expectedHarvestDate = "2026-05-15",
            seedInfo = "Certified"
        )
        assertEquals(initialCount + 1, viewModel.crops.value.size)
        val crop = viewModel.crops.value.last()
        assertEquals("Groundnuts", crop.cropType)
        assertEquals("CG7", crop.variety)
        assertEquals("2.0 Acres", crop.acreage)
    }

    @Test
    fun testUpdateCropGrowthStage() {
        val crop = viewModel.crops.value[0]
        assertEquals("Flowering", crop.growthStage)
        viewModel.updateCropStage(crop.id, "Harvesting")
        val updatedCrop = viewModel.crops.value.find { it.id == crop.id }
        assertEquals("Harvesting", updatedCrop?.growthStage)
    }
}
