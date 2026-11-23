package com.example.myapplication

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthTest {

    private lateinit var viewModel: FarmViewModel

    @Before
    fun setUp() {
        viewModel = FarmViewModel()
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
    fun testUserLoginAndDynamicNaming() {
        viewModel.login("chisomo@mundawathu.com", "Chisomo")
        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("chisomo@mundawathu.com", user?.email)
        assertEquals("Chisomo", user?.name)
    }

    @Test
    fun testUserLoginEmailPrefixFallback() {
        viewModel.login("banda@mundawathu.com", "")
        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("Banda", user?.name)
    }

    @Test
    fun testUserRegistration() {
        viewModel.register("Tamara Phiri", "Tamara Green Farm", "tamara@mundawathu.com", "FarmerPass2026")
        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("tamara@mundawathu.com", user?.email)
        assertEquals("Tamara Phiri", user?.name)
        assertEquals("Tamara Green Farm", user?.farmName)
    }
}
