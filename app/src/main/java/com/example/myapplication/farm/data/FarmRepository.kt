package com.example.myapplication.farm.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FarmViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(
        User(email = "farmer@mundawathu.com", name = "John Munda", phone = "+265 991 234 567", farmName = "Munda Wathu Main Farm")
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _farms = MutableStateFlow(
        listOf(
            Farm(name = "Main Valley Farm", size = "10 Hectares", location = "Zone A - North", description = "Primary maize and bean production plot."),
            Farm(name = "Riverbank Plot", size = "4 Hectares", location = "Zone B - East", description = "Vegetables and horticulture irrigation plot.")
        )
    )
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()

    private val _crops = MutableStateFlow(
        listOf(
            Crop(farmId = _farms.value[0].id, farmName = "Main Valley Farm", cropType = "Maize (Hybrid)", plantingDate = "2025-11-15", expectedHarvestDate = "2026-04-20", seedInfo = "SC 403 Certified Hybrid"),
            Crop(farmId = _farms.value[1].id, farmName = "Riverbank Plot", cropType = "Tomatoes", plantingDate = "2026-01-10", expectedHarvestDate = "2026-04-10", seedInfo = "Roma VF Improved")
        )
    )
    val crops: StateFlow<List<Crop>> = _crops.asStateFlow()

    private val _activities = MutableStateFlow(
        listOf(
            FarmActivity(farmId = _farms.value[0].id, cropId = _crops.value[0].id, activityType = "Land Preparation", date = "2025-11-01", notes = "Ploughing and harrowing tractor service", cost = 45000.0),
            FarmActivity(farmId = _farms.value[0].id, cropId = _crops.value[0].id, activityType = "Fertilizer Application", date = "2025-12-10", notes = "Basal dressing D-Compound", cost = 30000.0)
        )
    )
    val activities: StateFlow<List<FarmActivity>> = _activities.asStateFlow()

    private val _expenses = MutableStateFlow(
        listOf(
            Expense(farmId = _farms.value[0].id, category = "Seed", amount = 25000.0, date = "2025-11-10", description = "Certified Maize seeds 25kg"),
            Expense(farmId = _farms.value[0].id, category = "Fertiliser", amount = 60000.0, date = "2025-12-05", description = "NPK and Urea fertilizer bags"),
            Expense(farmId = _farms.value[1].id, category = "Labour", amount = 15000.0, date = "2026-01-08", description = "Weeding and nursery preparation")
        )
    )
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _harvests = MutableStateFlow(
        listOf(
            HarvestRecord(cropId = _crops.value[1].id, cropName = "Tomatoes", quantity = 1200.0, unitPrice = 500.0, totalSales = 600000.0, date = "2026-03-15")
        )
    )
    val harvests: StateFlow<List<HarvestRecord>> = _harvests.asStateFlow()

    // Auth actions
    fun login(email: String, name: String) {
        _currentUser.value = User(email = email, name = name.ifEmpty { "Farmer" }, phone = "+265 999 000 111", farmName = "My Farm")
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(name: String, phone: String, farmName: String) {
        _currentUser.update { current ->
            current?.copy(name = name, phone = phone, farmName = farmName)
        }
    }

    // Farm actions
    fun addFarm(name: String, size: String, location: String, description: String) {
        val newFarm = Farm(name = name, size = size, location = location, description = description)
        _farms.update { it + newFarm }
    }

    // Crop actions
    fun addCrop(farmId: String, cropType: String, plantingDate: String, expectedHarvestDate: String, seedInfo: String) {
        val farm = _farms.value.find { it.id == farmId }
        val newCrop = Crop(
            farmId = farmId,
            farmName = farm?.name ?: "Unknown Farm",
            cropType = cropType,
            plantingDate = plantingDate,
            expectedHarvestDate = expectedHarvestDate,
            seedInfo = seedInfo
        )
        _crops.update { it + newCrop }
    }

    // Activity actions
    fun addActivity(farmId: String, cropId: String?, activityType: String, date: String, notes: String, cost: Double) {
        val newAct = FarmActivity(
            farmId = farmId,
            cropId = cropId,
            activityType = activityType,
            date = date,
            notes = notes,
            cost = cost
        )
        _activities.update { it + newAct }
        // Also add to expenses automatically if cost > 0
        if (cost > 0) {
            val newExp = Expense(
                farmId = farmId,
                category = "Activity: $activityType",
                amount = cost,
                date = date,
                description = notes
            )
            _expenses.update { it + newExp }
        }
    }

    // Expense actions
    fun addExpense(farmId: String, category: String, amount: Double, date: String, description: String) {
        val newExp = Expense(
            farmId = farmId,
            category = category,
            amount = amount,
            date = date,
            description = description
        )
        _expenses.update { it + newExp }
    }

    // Harvest actions
    fun addHarvest(cropId: String, quantity: Double, unitPrice: Double, date: String) {
        val crop = _crops.value.find { it.id == cropId }
        val total = quantity * unitPrice
        val newHarv = HarvestRecord(
            cropId = cropId,
            cropName = crop?.cropType ?: "Crop",
            quantity = quantity,
            unitPrice = unitPrice,
            totalSales = total,
            date = date
        )
        _harvests.update { it + newHarv }
    }
}
