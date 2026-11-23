package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FarmViewModelTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
        viewModel.addFarm("Main Farm", "10 Hectares", "Zone A", "Test")
        viewModel.addInventoryItem("Solar Water Pump", "Equipment", "1 Unit", "In Stock")
    }

    @Test
    fun testPasswordValidation_tooShort() {
        val result = viewModel.validatePassword("pass1")
        assertNotNull(result)
        assertEquals("Password must be at least 8 characters long", result)
    }

    @Test
    fun testPasswordValidation_missingNumbers() {
        val result = viewModel.validatePassword("onlyletters")
        assertNotNull(result)
        assertEquals("Password must contain both letters and numbers", result)
    }

    @Test
    fun testPasswordValidation_valid() {
        val result = viewModel.validatePassword("FarmerPass2026")
        assertNull(result)
    }

    @Test
    fun testUserLogin() {
        viewModel.login("chisomo@mundawathu.com", "Chisomo")
        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("chisomo@mundawathu.com", user?.email)
        assertEquals("Chisomo", user?.name)
    }

    @Test
    fun testAddCropAndCalculateTotals() {
        val initialCropsCount = viewModel.crops.value.size
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
        assertEquals(initialCropsCount + 1, viewModel.crops.value.size)
        val addedCrop = viewModel.crops.value.last()
        assertEquals("Groundnuts", addedCrop.cropType)
        assertEquals("CG7", addedCrop.variety)
    }

    @Test
    fun testInventoryStatusToggle() {
        val initialItem = viewModel.inventory.value[0]
        val initialStatus = initialItem.status
        viewModel.toggleInventoryStatus(initialItem.id)
        val updatedItem = viewModel.inventory.value.find { it.id == initialItem.id }
        assertNotNull(updatedItem)
        assertNotEquals(initialStatus, updatedItem?.status)
    }
}
