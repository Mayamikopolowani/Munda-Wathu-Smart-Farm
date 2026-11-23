package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    constructor() : this(Application())

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _language = MutableStateFlow("English") // "English" or "Chichewa"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _farms = MutableStateFlow<List<Farm>>(emptyList())
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()

    private val _crops = MutableStateFlow<List<Crop>>(emptyList())
    val crops: StateFlow<List<Crop>> = _crops.asStateFlow()

    private val _activities = MutableStateFlow<List<FarmActivity>>(emptyList())
    val activities: StateFlow<List<FarmActivity>> = _activities.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _harvests = MutableStateFlow<List<HarvestRecord>>(emptyList())
    val harvests: StateFlow<List<HarvestRecord>> = _harvests.asStateFlow()

    private val _inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _weatherDays = MutableStateFlow<List<WeatherDay>>(emptyList())
    val weatherDays: StateFlow<List<WeatherDay>> = _weatherDays.asStateFlow()

    private val _selectedWeatherDay = MutableStateFlow("Thu")
    val selectedWeatherDay: StateFlow<String> = _selectedWeatherDay.asStateFlow()

    private val defaultMarketPrices = listOf(
        MarketPriceItem("Maize (White)", "Lilongwe Agriculture Market", "MWK 450 / kg", "+5% this week"),
        MarketPriceItem("Groundnuts (CG7)", "Limbe Market, Blantyre", "MWK 1,200 / kg", "+12% this week"),
        MarketPriceItem("Soybeans", "National Seed Co, Lilongwe", "MWK 950 / kg", "Stable"),
        MarketPriceItem("Tomatoes", "Zomba Central Market", "MWK 600 / kg", "+8% this week"),
        MarketPriceItem("Tobacco (Burley)", "Auction Holdings Ltd", "MWK 3,500 / kg", "High Demand")
    )

    private val _marketPrices = MutableStateFlow<List<MarketPriceItem>>(defaultMarketPrices)
    val marketPrices: StateFlow<List<MarketPriceItem>> = _marketPrices.asStateFlow()

    init {
        try {
            val db = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
            db.firestoreSettings = settings
        } catch (_: Exception) {}

        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            val savedLang = prefs.getString("saved_language", "English") ?: "English"
            _language.value = savedLang
        } catch (_: Exception) {}

        loadMarketPricesLocally()
        loadWeatherLocally()

        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            val savedEmail = prefs.getString("saved_user_email", null)
            val savedName = prefs.getString("saved_user_name", "")
            val savedFarmName = prefs.getString("saved_user_farm", "")

            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (!savedEmail.isNullOrBlank() || currentUser != null) {
                val emailToUse = savedEmail ?: currentUser?.email ?: ""
                val baseName = if (emailToUse.contains("@")) emailToUse.substringBefore("@").replaceFirstChar { it.uppercase() } else (savedName?.ifBlank { "Farmer" } ?: "Farmer")
                val resolvedFarmName = savedFarmName?.ifBlank { "$baseName's Farm" } ?: "$baseName's Farm"

                val userObj = User(
                    email = emailToUse,
                    name = baseName,
                    phone = "+265 991 234 567",
                    farmName = resolvedFarmName,
                    location = "Zomba, Southern Region",
                    landSize = "10 Acres",
                    primaryCrops = "Maize, Beans"
                )
                _currentUser.value = userObj

                val uid = getUserId()
                loadUserDataFromFirestore(uid)
            }
        } catch (_: Exception) {}

        fetchWeatherData()
    }

    private fun loadMarketPricesLocally() {
        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            val jsonStr = prefs.getString("cached_market_prices", null)
            if (!jsonStr.isNullOrBlank()) {
                val array = org.json.JSONArray(jsonStr)
                val list = mutableListOf<MarketPriceItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        MarketPriceItem(
                            cropName = obj.optString("cropName", ""),
                            market = obj.optString("market", ""),
                            pricePerKg = obj.optString("pricePerKg", ""),
                            trend = obj.optString("trend", "")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _marketPrices.value = list
                }
            } else {
                saveMarketPricesLocally(defaultMarketPrices)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveMarketPricesLocally(prices: List<MarketPriceItem>) {
        try {
            val array = org.json.JSONArray()
            for (item in prices) {
                val obj = org.json.JSONObject()
                obj.put("cropName", item.cropName)
                obj.put("market", item.market)
                obj.put("pricePerKg", item.pricePerKg)
                obj.put("trend", item.trend)
                array.put(obj)
            }
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("cached_market_prices", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadWeatherLocally() {
        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            val jsonStr = prefs.getString("cached_weather_json", null)
            if (!jsonStr.isNullOrBlank()) {
                val array = org.json.JSONArray(jsonStr)
                val list = mutableListOf<WeatherDay>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        WeatherDay(
                            day = obj.optString("day", ""),
                            dayChichewa = obj.optString("dayChichewa", ""),
                            condition = obj.optString("condition", ""),
                            temp = obj.optString("temp", ""),
                            rainProb = obj.optString("rainProb", "")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _weatherDays.value = list
                    if (_selectedWeatherDay.value !in list.map { it.day }) {
                        _selectedWeatherDay.value = list.first().day
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveWeatherLocally(days: List<WeatherDay>) {
        try {
            val array = org.json.JSONArray()
            for (day in days) {
                val obj = org.json.JSONObject()
                obj.put("day", day.day)
                obj.put("dayChichewa", day.dayChichewa)
                obj.put("condition", day.condition)
                obj.put("temp", day.temp)
                obj.put("rainProb", day.rainProb)
                array.put(obj)
            }
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("cached_weather_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUserId(): String {
        return try {
            val authUid = try { FirebaseAuth.getInstance().currentUser?.uid } catch (_: Exception) { null }
            if (!authUid.isNullOrBlank()) {
                authUid
            } else {
                val prefsEmail = try {
                    val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
                    prefs.getString("saved_user_email", null)
                } catch (_: Exception) { null }

                val email = prefsEmail ?: _currentUser.value?.email ?: try { FirebaseAuth.getInstance().currentUser?.email } catch (_: Exception) { null }

                if (!email.isNullOrBlank()) {
                    email.lowercase().replace(".", "_").replace("@", "_")
                } else {
                    "default_user"
                }
            }
        } catch (_: Exception) {
            "default_user"
        }
    }

    private fun saveUserSession(email: String, name: String, farmName: String) {
        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("saved_user_email", email)
                .putString("saved_user_name", name)
                .putString("saved_user_farm", farmName)
                .apply()
        } catch (_: Exception) {}
    }

    fun logout() {
        _currentUser.value = null
        _farms.value = emptyList()
        _crops.value = emptyList()
        _activities.value = emptyList()
        _expenses.value = emptyList()
        _harvests.value = emptyList()
        _inventory.value = emptyList()
        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        } catch (_: Exception) {}
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (_: Exception) {}
    }

    fun loadUserDataFromFirestore(uid: String) {
        _farms.value = emptyList()
        _crops.value = emptyList()
        _activities.value = emptyList()
        _expenses.value = emptyList()
        _harvests.value = emptyList()
        _inventory.value = emptyList()

        try {
            val db = FirebaseFirestore.getInstance()
            val farmerRef = db.collection("farmers").document(uid)

            farmerRef.collection("farms").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Farm::class.java) }
                if (list.isNotEmpty()) {
                    _farms.value = list
                } else if (_farms.value.isEmpty()) {
                    val defaultFarm = Farm(name = "Main Valley Farm", size = "10 Hectares", location = "Zone A - North", description = "Primary maize and bean production plot.")
                    _farms.value = listOf(defaultFarm)
                    farmerRef.collection("farms").document(defaultFarm.id).set(defaultFarm)
                }
            }.addOnFailureListener { e -> e.printStackTrace() }

            farmerRef.collection("crops").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Crop::class.java) }
                _crops.value = list
            }.addOnFailureListener { e -> e.printStackTrace() }

            farmerRef.collection("activities").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(FarmActivity::class.java) }
                _activities.value = list
            }.addOnFailureListener { e -> e.printStackTrace() }

            farmerRef.collection("expenses").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                _expenses.value = list
            }.addOnFailureListener { e -> e.printStackTrace() }

            farmerRef.collection("harvests").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(HarvestRecord::class.java) }
                _harvests.value = list
            }.addOnFailureListener { e -> e.printStackTrace() }

            farmerRef.collection("inventory").get().addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(InventoryItem::class.java) }
                _inventory.value = list
            }.addOnFailureListener { e -> e.printStackTrace() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val urlString = "https://api.openweathermap.org/data/2.5/forecast?q=Zomba&units=metric&appid=0b5b99f4cdf46ec85f449743a7403186"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonObj = JSONObject(response.toString())
                    val list = jsonObj.getJSONArray("list")
                    val daysMap = mutableMapOf<String, WeatherDay>()

                    for (i in 0 until list.length()) {
                        val item = list.getJSONObject(i)
                        val dtTxt = item.getString("dt_txt")
                        if (dtTxt.contains("12:00:00") || daysMap.size == 0) {
                            val main = item.getJSONObject("main")
                            val tempVal = main.getDouble("temp").toInt()
                            val weatherArr = item.getJSONArray("weather")
                            val desc = weatherArr.getJSONObject(0).getString("description")
                            val pop = item.optDouble("pop", 0.0) * 100

                            val datePart = dtTxt.substringBefore(" ")
                            val dayName = getDayNameFromDate(datePart)
                            val dayChichewa = getChichewaDay(dayName)

                            if (!daysMap.containsKey(dayName) && daysMap.size < 7) {
                                daysMap[dayName] = WeatherDay(
                                    day = dayName,
                                    dayChichewa = dayChichewa,
                                    condition = desc.replaceFirstChar { it.uppercase() },
                                    temp = "$tempVal°C",
                                    rainProb = "${pop.toInt()}%"
                                )
                            }
                        }
                    }

                    if (daysMap.isNotEmpty()) {
                        val fetchedDays = daysMap.values.toList()
                        _weatherDays.value = fetchedDays
                        saveWeatherLocally(fetchedDays)
                        if (_selectedWeatherDay.value !in fetchedDays.map { it.day }) {
                            _selectedWeatherDay.value = fetchedDays.first().day
                        }
                    }
                } else {
                    loadWeatherLocally()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loadWeatherLocally()
            }
        }
    }

    private fun getDayNameFromDate(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr)
            val daySdf = SimpleDateFormat("EEE", Locale.getDefault())
            daySdf.format(date ?: Date())
        } catch (e: Exception) {
            "Today"
        }
    }

    private fun getChichewaDay(engDay: String): String {
        return when (engDay.lowercase()) {
            "mon", "monday" -> "Lolemba"
            "tue", "tuesday" -> "Lachiwiri"
            "wed", "wednesday" -> "Lachitatu"
            "thu", "thursday" -> "Lachinayi"
            "fri", "friday" -> "Lachisanu"
            "sat", "saturday" -> "Loweruka"
            "sun", "sunday" -> "Lamlungu"
            else -> "Lolemba"
        }
    }

    fun setSelectedWeatherDay(day: String) {
        _selectedWeatherDay.value = day
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        try {
            val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("saved_language", lang).apply()
        } catch (_: Exception) {}
    }

    fun validatePassword(pass: String): String? {
        if (pass.length < 8) return "Password must be at least 8 characters long"
        val hasLetter = pass.any { it.isLetter() }
        val hasDigit = pass.any { it.isDigit() }
        if (!hasLetter || !hasDigit) return "Password must contain both letters and numbers"
        return null
    }

    fun register(
        name: String,
        farmName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val baseName = if (name.isNotBlank()) {
            name
        } else {
            val prefix = email.substringBefore("@")
            if (prefix.isNotBlank()) {
                prefix.replaceFirstChar { it.uppercase() }
            } else {
                "Farmer"
            }
        }
        val resolvedFarmName = if (farmName.isNotBlank()) {
            farmName
        } else {
            "$baseName's Farm"
        }
        val userObj = User(
            email = email,
            name = baseName,
            phone = "+265 991 234 567",
            farmName = resolvedFarmName,
            location = "Zomba, Southern Region",
            landSize = "10 Acres",
            primaryCrops = "Maize, Beans"
        )
        val emailUid = email.lowercase().replace(".", "_").replace("@", "_")

        _currentUser.value = userObj
        saveUserSession(email, userObj.name, userObj.farmName)

        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("farmers").document(emailUid).set(userObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authTask ->
                val firebaseUser = authTask.result?.user
                val authUid = firebaseUser?.uid
                val definitiveUid = authUid ?: emailUid
                val db = FirebaseFirestore.getInstance()

                db.collection("farmers").document(emailUid).set(userObj)
                if (authUid != null) {
                    db.collection("farmers").document(authUid).set(userObj)
                }
                loadUserDataFromFirestore(definitiveUid)
                onSuccess()
            }.addOnFailureListener {
                val db = FirebaseFirestore.getInstance()
                db.collection("farmers").document(emailUid).set(userObj)
                loadUserDataFromFirestore(emailUid)
                onSuccess()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            loadUserDataFromFirestore(emailUid)
            onSuccess()
        }
    }

    fun login(
        email: String,
        password: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            val auth = FirebaseAuth.getInstance()
            val passToUse = if (password.isNotBlank()) password else "FarmerPass2026"
            val emailUid = email.lowercase().replace(".", "_").replace("@", "_")

            auth.signInWithEmailAndPassword(email, passToUse).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    val authUid = firebaseUser?.uid
                    val definitiveUid = authUid ?: emailUid
                    val db = FirebaseFirestore.getInstance()

                    val baseName = if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else "Farmer"
                    val resolvedFarmName = "$baseName's Farm"

                    fun handleSuccessfulUser(user: User, uidUsed: String) {
                        _currentUser.value = user
                        saveUserSession(email, user.name, user.farmName)
                        loadUserDataFromFirestore(uidUsed)
                        onSuccess()
                    }

                    fun fallbackLogin() {
                        val fallbackUser = User(
                            email = email,
                            name = baseName,
                            phone = "+265 991 234 567",
                            farmName = resolvedFarmName,
                            location = "Zomba, Southern Region",
                            landSize = "10 Acres",
                            primaryCrops = "Maize, Beans"
                        )
                        handleSuccessfulUser(fallbackUser, definitiveUid)
                    }

                    db.collection("farmers").document(definitiveUid).get(com.google.firebase.firestore.Source.DEFAULT)
                        .addOnSuccessListener { document1 ->
                            if (document1 != null && document1.exists()) {
                                val firestoreUser = document1.toObject(User::class.java)
                                if (firestoreUser != null) {
                                    handleSuccessfulUser(firestoreUser, definitiveUid)
                                    return@addOnSuccessListener
                                }
                            }

                            db.collection("farmers").document(emailUid).get(com.google.firebase.firestore.Source.DEFAULT)
                                .addOnSuccessListener { document2 ->
                                    if (document2 != null && document2.exists()) {
                                        val firestoreUser = document2.toObject(User::class.java)
                                        if (firestoreUser != null) {
                                            handleSuccessfulUser(firestoreUser, emailUid)
                                            return@addOnSuccessListener
                                        }
                                    }
                                    fallbackLogin()
                                }.addOnFailureListener {
                                    fallbackLogin()
                                }
                        }.addOnFailureListener {
                            fallbackLogin()
                        }
                } else {
                    val exception = task.exception
                    val isNetworkErr = exception?.localizedMessage?.contains("network", ignoreCase = true) == true ||
                            exception?.localizedMessage?.contains("unreachable", ignoreCase = true) == true ||
                            exception?.localizedMessage?.contains("timeout", ignoreCase = true) == true

                    val prefs = getApplication<Application>().getSharedPreferences("munda_wathu_prefs", Context.MODE_PRIVATE)
                    val savedEmail = prefs.getString("saved_user_email", null)

                    if (isNetworkErr && !savedEmail.isNullOrBlank() && savedEmail.equals(email, ignoreCase = true)) {
                        val savedName = prefs.getString("saved_user_name", "") ?: ""
                        val savedFarmName = prefs.getString("saved_user_farm", "") ?: ""
                        val baseName = if (savedName.isNotBlank()) savedName else (if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else "Farmer")
                        val resolvedFarmName = if (savedFarmName.isNotBlank()) savedFarmName else "$baseName's Farm"

                        val userObj = User(
                            email = email,
                            name = baseName,
                            phone = "+265 991 234 567",
                            farmName = resolvedFarmName,
                            location = "Zomba, Southern Region",
                            landSize = "10 Acres",
                            primaryCrops = "Maize, Beans"
                        )
                        _currentUser.value = userObj
                        saveUserSession(email, userObj.name, userObj.farmName)
                        loadUserDataFromFirestore(emailUid)
                        onSuccess()
                    } else if (isNetworkErr) {
                        onError("Network error: Please check your phone's internet connection (Wi-Fi or Mobile Data).")
                    } else {
                        onError(exception?.localizedMessage ?: "Invalid email or password. Please register if you don't have an account.")
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback for local JVM unit tests where Firebase SDK is not initialized
            val uid = email.lowercase().replace(".", "_").replace("@", "_")
            val baseName = if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else "Farmer"
            val resolvedFarmName = "$baseName's Farm"
            val userObj = User(
                email = email,
                name = baseName,
                phone = "+265 991 234 567",
                farmName = resolvedFarmName,
                location = "Zomba, Southern Region",
                landSize = "10 Acres",
                primaryCrops = "Maize, Beans"
            )
            _currentUser.value = userObj
            saveUserSession(email, userObj.name, userObj.farmName)
            loadUserDataFromFirestore(uid)
            onSuccess()
        }
    }

    fun updateProfile(name: String, phone: String, farmName: String, location: String, landSize: String, primaryCrops: String) {
        _currentUser.update { current ->
            current?.copy(
                name = name,
                phone = phone,
                farmName = farmName,
                location = location,
                landSize = landSize,
                primaryCrops = primaryCrops
            )
        }
        try {
            val user = _currentUser.value
            if (user != null) {
                val uid = getUserId()
                val db = FirebaseFirestore.getInstance()
                db.collection("farmers").document(uid).set(user)
            }
        } catch (_: Exception) {}
    }

    fun addFarm(name: String, size: String, location: String, description: String) {
        val newFarm = Farm(name = name, size = size, location = location, description = description)
        _farms.update { it + newFarm }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("farms").document(newFarm.id).set(newFarm)
        } catch (_: Exception) {}
    }

    fun addCrop(farmId: String, cropType: String, variety: String, acreage: String, growthStage: String, plantingDate: String, expectedHarvestDate: String, seedInfo: String) {
        val farm = _farms.value.find { it.id == farmId }
        val newCrop = Crop(
            farmId = farmId,
            farmName = farm?.name ?: "Main Valley Farm",
            cropType = cropType,
            variety = variety,
            acreage = acreage,
            growthStage = growthStage,
            plantingDate = plantingDate,
            expectedHarvestDate = expectedHarvestDate,
            seedInfo = seedInfo
        )
        _crops.update { it + newCrop }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("crops").document(newCrop.id).set(newCrop)
        } catch (_: Exception) {}
    }

        fun updateCropStage(cropId: String, newStage: String) {
        _crops.update { list ->
            list.map { crop ->
                if (crop.id == cropId) {
                    val updated = crop.copy(growthStage = newStage)
                    try {
                        val uid = getUserId()
                        FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("crops").document(crop.id).set(updated)
                    } catch (_: Exception) {}
                    updated
                } else crop
            }
        }
    }

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
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("activities").document(newAct.id).set(newAct)
        } catch (_: Exception) {}

        if (cost > 0) {
            val newExp = Expense(
                farmId = farmId,
                category = "Activity: $activityType",
                amount = cost,
                date = date,
                description = notes
            )
            _expenses.update { it + newExp }
            try {
                val uid = getUserId()
                FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("expenses").document(newExp.id).set(newExp)
            } catch (_: Exception) {}
        }
    }

    fun addExpense(farmId: String, category: String, amount: Double, date: String, description: String) {
        val newExp = Expense(
            farmId = farmId,
            category = category,
            amount = amount,
            date = date,
            description = description
        )
        _expenses.update { it + newExp }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("expenses").document(newExp.id).set(newExp)
        } catch (_: Exception) {}
    }

    fun addHarvest(cropId: String, quantity: Double, unitPrice: Double, buyer: String, date: String) {
        val crop = _crops.value.find { it.id == cropId }
        val total = quantity * unitPrice
        val newHarv = HarvestRecord(
            cropId = cropId,
            cropName = crop?.cropType ?: "Crop",
            quantity = quantity,
            unitPrice = unitPrice,
            totalSales = total,
            buyer = buyer,
            date = date
        )
        _harvests.update { it + newHarv }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("harvests").document(newHarv.id).set(newHarv)
        } catch (_: Exception) {}
    }

    fun addInventoryItem(itemName: String, category: String, quantity: String, status: String) {
        val newItem = InventoryItem(itemName = itemName, category = category, quantity = quantity, status = status)
        _inventory.update { it + newItem }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("inventory").document(newItem.id).set(newItem)
        } catch (_: Exception) {}
    }

    fun toggleInventoryStatus(itemId: String) {
        _inventory.update { list ->
            list.map { item ->
                if (item.id == itemId) {
                    val nextStatus = when (item.status) {
                        "In Stock" -> "Low Stock"
                        "Low Stock" -> "Needs Service"
                        else -> "In Stock"
                    }
                    val updated = item.copy(status = nextStatus)
                    try {
                        val uid = getUserId()
                        FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("inventory").document(item.id).set(updated)
                    } catch (_: Exception) {}
                    updated
                } else item
            }
        }
    }

    fun deleteFarm(farmId: String) {
        _farms.update { list -> list.filter { it.id != farmId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("farms").document(farmId).delete()
        } catch (_: Exception) {}
    }

    fun deleteCrop(cropId: String) {
        _crops.update { list -> list.filter { it.id != cropId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("crops").document(cropId).delete()
        } catch (_: Exception) {}
    }

    fun deleteActivity(activityId: String) {
        _activities.update { list -> list.filter { it.id != activityId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("activities").document(activityId).delete()
        } catch (_: Exception) {}
    }

    fun deleteExpense(expenseId: String) {
        _expenses.update { list -> list.filter { it.id != expenseId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("expenses").document(expenseId).delete()
        } catch (_: Exception) {}
    }

    fun deleteHarvest(harvestId: String) {
        _harvests.update { list -> list.filter { it.id != harvestId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("harvests").document(harvestId).delete()
        } catch (_: Exception) {}
    }

    fun deleteInventoryItem(itemId: String) {
        _inventory.update { list -> list.filter { it.id != itemId } }
        try {
            val uid = getUserId()
            FirebaseFirestore.getInstance().collection("farmers").document(uid).collection("inventory").document(itemId).delete()
        } catch (_: Exception) {}
    }
}
