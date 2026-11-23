package com.example.myapplication

import java.util.UUID

data class User(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val farmName: String = "",
    val location: String = "Zomba, Southern Region",
    val landSize: String = "15 Acres",
    val primaryCrops: String = "Maize, Tobacco, Groundnuts"
)

data class Farm(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val size: String = "",
    val location: String = "",
    val description: String = ""
)

data class Crop(
    val id: String = UUID.randomUUID().toString(),
    val farmId: String = "",
    val farmName: String = "",
    val cropType: String = "",
    val variety: String = "CG7",
    val acreage: String = "2.0 Acres",
    val growthStage: String = "Germination",
    val plantingDate: String = "",
    val expectedHarvestDate: String = "",
    val seedInfo: String = ""
)

data class FarmActivity(
    val id: String = UUID.randomUUID().toString(),
    val farmId: String = "",
    val cropId: String? = null,
    val activityType: String = "",
    val date: String = "",
    val notes: String = "",
    val cost: Double = 0.0
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val farmId: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = ""
)

data class HarvestRecord(
    val id: String = UUID.randomUUID().toString(),
    val cropId: String = "",
    val cropName: String = "",
    val quantity: Double = 0.0,
    val unitPrice: Double = 0.0,
    val totalSales: Double = 0.0,
    val buyer: String = "Local Market",
    val date: String = ""
)

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val itemName: String = "",
    val category: String = "",
    val quantity: String = "",
    val status: String = "In Stock" // In Stock, Low Stock, Needs Service
)

data class WeatherDay(
    val day: String = "",
    val dayChichewa: String = "",
    val condition: String = "",
    val temp: String = "",
    val rainProb: String = ""
)

data class MarketPriceItem(
    val cropName: String = "",
    val market: String = "",
    val pricePerKg: String = "",
    val trend: String = ""
)
