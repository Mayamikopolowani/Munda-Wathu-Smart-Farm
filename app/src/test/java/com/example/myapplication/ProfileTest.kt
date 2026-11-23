package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProfileTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
    }

    @Test
    fun testLanguageSwitching() {
        assertEquals("English", viewModel.language.value)
        viewModel.setLanguage("Chichewa")
        assertEquals("Chichewa", viewModel.language.value)
    }

    @Test
    fun testUpdateFarmProfile() {
        viewModel.login("mayamiko@mundawathu.com", "Mayamiko Polowani")
        viewModel.updateProfile(
            name = "Mayamiko Polowani",
            phone = "+265 991 234 567",
            farmName = "Munda Wathu Main Farm",
            location = "Zomba, Southern Region",
            landSize = "20 Acres",
            primaryCrops = "Maize, Soybeans"
        )
        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("Zomba, Southern Region", user?.location)
        assertEquals("20 Acres", user?.landSize)
        assertEquals("Maize, Soybeans", user?.primaryCrops)
    }
}
