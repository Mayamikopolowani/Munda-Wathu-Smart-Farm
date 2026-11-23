package com.example.myapplication.farm.data

data class User(
    val email: String,
    val name: String,
    val phone: String,
    val farmName: String
)

data class Farm(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val size: String, // e.g. "5 Acres"
    val location: String, // e.g. "North Sector"
    val description: String
)

data class Crop(
    val id: String = java.util.UUID.randomUUID().toString(),
    val farmId: String,
    val farmName: String,
    val cropType: String, // e.g. Maize, Beans, Tomatoes
    val plantingDate: String,
    val expectedHarvestDate: String,
    val seedInfo: String
)

data class FarmActivity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val farmId: String,
    val cropId: String?,
    val activityType: String, // Land Preparation, Planting, Weeding, Fertilizer Application, Pest Control
    val date: String,
    val notes: String,
    val cost: Double
)

data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val farmId: String,
    val category: String, // Seed, Fertiliser, Labour, Pesticides, Transport, Other
    val amount: Double,
    val date: String,
    val description: String
)

data class HarvestRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val cropId: String,
    val cropName: String,
    val quantity: Double, // e.g. 500 kg
    val unitPrice: Double,
    val totalSales: Double, // quantity * unitPrice
    val date: String
)
