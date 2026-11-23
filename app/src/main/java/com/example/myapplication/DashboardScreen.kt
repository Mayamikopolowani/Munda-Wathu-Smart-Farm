package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FarmViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val crops by viewModel.crops.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val harvests by viewModel.harvests.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val farms by viewModel.farms.collectAsState()
    val weatherDays by viewModel.weatherDays.collectAsState()
    val currentWeather = weatherDays.firstOrNull() ?: WeatherDay(
        day = "Today",
        condition = "Partly Cloudy",
        temp = "25°C",
        rainProb = "30%"
    )
    val locationName = user?.location?.substringBefore(",") ?: "Zomba"
    var searchQuery by remember { mutableStateOf("") }

    val lang by viewModel.language.collectAsState()
    val isChichewa = lang == "Chichewa"

    val totalExpenses = expenses.sumOf { it.amount }
    val expensesTitle = "MK ${totalExpenses.toInt()}"
    val inventoryCountTitle = "${inventory.size} ${if (isChichewa) "Zinthu" else "Items"}"
    val activitiesBadge = "${activities.size} ${if (isChichewa) "Ntchito" else "Activities"}"
    val harvestSalesBadge = "${harvests.size} ${if (isChichewa) "Kugulitsa" else "Sales"}"
    val cropsBadge = "${crops.size} ${if (isChichewa) "Mbewu" else "Crops"}"
    var currentTab by remember { mutableStateOf(0) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showQuickActionsDialog by remember { mutableStateOf(false) }

    data class ModuleShortcut(val title: String, val route: String, val icon: String)
    val moduleShortcuts = listOf(
        ModuleShortcut(AppStrings.get("mod_crops", lang), "crops", "🌱"),
        ModuleShortcut(AppStrings.get("mod_inventory", lang), "inventory", "📦"),
        ModuleShortcut(AppStrings.get("mod_expenses", lang), "expenses", "💰"),
        ModuleShortcut(AppStrings.get("mod_harvests", lang), "harvests", "🌾"),
        ModuleShortcut(AppStrings.get("mod_farms", lang), "farms", "🏞️"),
        ModuleShortcut(AppStrings.get("mod_weather", lang), "weather", "⛅"),
        ModuleShortcut(AppStrings.get("mod_reports", lang), "reports", "📈"),
        ModuleShortcut(AppStrings.get("mod_activities", lang), "activities", "🚜"),
        ModuleShortcut(AppStrings.get("mod_market", lang), "market_prices", "📊"),
        ModuleShortcut(AppStrings.get("mod_reminders", lang), "reminders", "🔔")
    )

    val matchedCrops = if (searchQuery.isNotBlank()) crops.filter { it.cropType.contains(searchQuery, true) || it.variety.contains(searchQuery, true) } else emptyList()
    val matchedInventory = if (searchQuery.isNotBlank()) inventory.filter { it.itemName.contains(searchQuery, true) || it.category.contains(searchQuery, true) } else emptyList()
    val matchedExpenses = if (searchQuery.isNotBlank()) expenses.filter { it.category.contains(searchQuery, true) || it.description.contains(searchQuery, true) } else emptyList()
    val matchedHarvests = if (searchQuery.isNotBlank()) harvests.filter { it.cropName.contains(searchQuery, true) || it.buyer.contains(searchQuery, true) } else emptyList()
    val matchedFarms = if (searchQuery.isNotBlank()) farms.filter { it.name.contains(searchQuery, true) || it.location.contains(searchQuery, true) } else emptyList()
    val matchedModules = if (searchQuery.isNotBlank()) moduleShortcuts.filter { it.title.contains(searchQuery, true) } else emptyList()

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFF1B5E20),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.clickable { onNavigate("profile") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "MUNDA WATHU",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFC8E6C9),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = user?.location ?: "Zomba, Southern Region",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC8E6C9)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            IconButton(onClick = { showNotificationsDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                            Badge(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                            ) {
                                Text("3", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0; onNavigate("dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text(AppStrings.get("nav_dashboard", lang)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5E20), selectedTextColor = Color(0xFF1B5E20))
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1; onNavigate("crops") },
                    icon = { Icon(Icons.Default.Eco, contentDescription = "Crops") },
                    label = { Text(AppStrings.get("nav_crops", lang)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5E20), selectedTextColor = Color(0xFF1B5E20))
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2; onNavigate("expenses") },
                    icon = { Icon(Icons.Default.Wallet, contentDescription = "Finance") },
                    label = { Text(AppStrings.get("nav_finance", lang)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5E20), selectedTextColor = Color(0xFF1B5E20))
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3; onNavigate("weather") },
                    icon = { Icon(Icons.Default.WbSunny, contentDescription = "Weather") },
                    label = { Text(AppStrings.get("nav_weather", lang)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5E20), selectedTextColor = Color(0xFF1B5E20))
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { currentTab = 4; onNavigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(AppStrings.get("nav_profile", lang)) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5E20), selectedTextColor = Color(0xFF1B5E20))
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuickActionsDialog = true },
                containerColor = Color(0xFF1B5E20),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(AppStrings.get("quick_actions", lang), fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F8F4))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${AppStrings.get("greeting", lang)} ${user?.name ?: "Farmer"} 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppStrings.get("dashboard_subtitle", lang),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(AppStrings.get("search_hint", lang), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1B5E20)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            if (searchQuery.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Search Results for \"$searchQuery\"",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val hasResults = matchedCrops.isNotEmpty() || matchedInventory.isNotEmpty() || matchedExpenses.isNotEmpty() || matchedHarvests.isNotEmpty() || matchedFarms.isNotEmpty() || matchedModules.isNotEmpty()

                            if (!hasResults) {
                                Text(text = "No matching records found for \"$searchQuery\". Try searching for inventory, crops, expenses, or modules.", fontSize = 13.sp, color = Color.Gray)
                            }

                            if (matchedModules.isNotEmpty()) {
                                Text(text = "Modules & Features:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedModules.forEach { mod ->
                                    TextButton(onClick = { onNavigate(mod.route) }) {
                                        Text("${mod.icon} ${mod.title}", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (matchedCrops.isNotEmpty()) {
                                Text(text = "Crops:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedCrops.forEach { crop ->
                                    TextButton(onClick = { onNavigate("crops") }) {
                                        Text("🌱 ${crop.cropType} (${crop.variety}) - ${crop.growthStage}", color = Color.DarkGray)
                                    }
                                }
                            }

                            if (matchedInventory.isNotEmpty()) {
                                Text(text = "Inventory Stock:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedInventory.forEach { item ->
                                    TextButton(onClick = { onNavigate("inventory") }) {
                                        Text("📦 ${item.itemName} (${item.category}) - ${item.status}", color = Color.DarkGray)
                                    }
                                }
                            }

                            if (matchedExpenses.isNotEmpty()) {
                                Text(text = "Expenses / Finance:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedExpenses.forEach { exp ->
                                    TextButton(onClick = { onNavigate("expenses") }) {
                                        Text("💰 ${exp.category}: MWK ${exp.amount.toInt()} (${exp.description})", color = Color.DarkGray)
                                    }
                                }
                            }

                            if (matchedHarvests.isNotEmpty()) {
                                Text(text = "Harvest & Sales:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedHarvests.forEach { harv ->
                                    TextButton(onClick = { onNavigate("harvests") }) {
                                        Text("🌾 ${harv.cropName}: ${harv.quantity}kg - MWK ${harv.totalSales.toInt()}", color = Color.DarkGray)
                                    }
                                }
                            }

                            if (matchedFarms.isNotEmpty()) {
                                Text(text = "Farms:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                matchedFarms.forEach { farm ->
                                    TextButton(onClick = { onNavigate("farms") }) {
                                        Text("🏞️ ${farm.name} (${farm.size}) - ${farm.location}", color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge(containerColor = Color(0xFFFFF3E0)) {
                                    Text(
                                        text = "LIVE WEATHER",
                                        color = Color(0xFFE65100),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "$locationName • ${currentWeather.temp}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${currentWeather.condition} • Rain Chance ${currentWeather.rainProb}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = null,
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Quick Actions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            QuickActionButton(title = "Add Crop", icon = Icons.Default.Eco) { onNavigate("crops") }
                        }
                        item {
                            QuickActionButton(title = "Record Activity", icon = Icons.Default.Build) { onNavigate("activities") }
                        }
                        item {
                            QuickActionButton(title = "Log Harvest", icon = Icons.Default.Agriculture) { onNavigate("harvests") }
                        }
                        item {
                            QuickActionButton(title = "Add Expense", icon = Icons.Default.Wallet) { onNavigate("expenses") }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = expensesTitle,
                            subtitle = "Season Expenses",
                            desc = "Total recorded expenses",
                            icon = Icons.Default.Wallet,
                            iconBg = Color(0xFFFBE9E7),
                            iconTint = Color(0xFFD84315),
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("expenses") }

                        MetricCard(
                            title = inventoryCountTitle,
                            subtitle = "Inventory Stock",
                            desc = "Total items in stock",
                            icon = Icons.Default.Inventory,
                            iconBg = Color(0xFFEDE7F6),
                            iconTint = Color(0xFF512DA8),
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("inventory") }
                    }
                }

                item {
                    Text(
                        text = "Smart Farm Modules & Proposal Features",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ModuleCard(
                            title = "Crops & Variety Management",
                            badge = cropsBadge,
                            description = "Track growth stages, planting dates & crop health",
                            icon = Icons.Default.Eco,
                            iconBg = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32)
                        ) { onNavigate("crops") }

                        ModuleCard(
                            title = "Farm Activities & Spraying",
                            badge = activitiesBadge,
                            description = "Log land prep, planting, weeding, fertilizer & spraying",
                            icon = Icons.Default.Build,
                            iconBg = Color(0xFFE0F7FA),
                            iconTint = Color(0xFF00838F)
                        ) { onNavigate("activities") }

                        ModuleCard(
                            title = "Harvest Records & Sales",
                            badge = harvestSalesBadge,
                            description = "Record harvested yields, sales prices & buyer contracts",
                            icon = Icons.Default.Agriculture,
                            iconBg = Color(0xFFFFF9C4),
                            iconTint = Color(0xFFF57F17)
                        ) { onNavigate("harvests") }

                        ModuleCard(
                            title = "Local Market Prices",
                            badge = "Live Ticker",
                            description = "View regional market crop prices across Malawi",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            iconBg = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32)
                        ) { onNavigate("market_prices") }

                        ModuleCard(
                            title = "Activity Reminders & Alerts",
                            badge = "3 Pending",
                            description = "Schedule farming operations & spray notifications",
                            icon = Icons.Default.NotificationsActive,
                            iconBg = Color(0xFFFFF3E0),
                            iconTint = Color(0xFFE65100)
                        ) { onNavigate("reminders") }

                        ModuleCard(
                            title = "Financial & Profit/Loss Analysis",
                            badge = "Active",
                            description = "Log expenses, revenue, net profit & expenditure reports",
                            icon = Icons.Default.Wallet,
                            iconBg = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32)
                        ) { onNavigate("reports") }
                    }
                }
            }
        }

        if (showNotificationsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog = false },
                title = { Text("Farm Notifications (3)", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        NotificationRow(
                            title = "Weather Advisory",
                            desc = "Thunderstorms expected on Thursday in Zomba / Lilongwe (90% rain chance)."
                        )
                        NotificationRow(
                            title = "Fertilizer Reminder",
                            desc = "Top-dressing NPK application due for Main Valley Farm."
                        )
                        NotificationRow(
                            title = "Market Price Update",
                            desc = "Soybean prices increased by 12% this week at National Seed Co."
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showNotificationsDialog = false
                            onNavigate("reminders")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("View All Reminders", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNotificationsDialog = false }) {
                        Text("Close", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        if (showQuickActionsDialog) {
            AlertDialog(
                onDismissRequest = { showQuickActionsDialog = false },
                title = { Text("⚡ Quick Actions", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select an action to perform:", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        QuickActionMenuRow(icon = Icons.Default.Eco, title = "Add New Crop", desc = "Register new crop variety & planting") {
                            showQuickActionsDialog = false
                            onNavigate("crops")
                        }
                        QuickActionMenuRow(icon = Icons.Default.Build, title = "Record Farm Activity", desc = "Log land prep, weeding, spraying") {
                            showQuickActionsDialog = false
                            onNavigate("activities")
                        }
                        QuickActionMenuRow(icon = Icons.Default.Agriculture, title = "Log Harvest & Sales", desc = "Record harvested yield & buyers") {
                            showQuickActionsDialog = false
                            onNavigate("harvests")
                        }
                        QuickActionMenuRow(icon = Icons.Default.Wallet, title = "Add Farm Expense", desc = "Record seeds, fertilizer & labor costs") {
                            showQuickActionsDialog = false
                            onNavigate("expenses")
                        }
                        QuickActionMenuRow(icon = Icons.Default.Inventory, title = "Add Inventory Item", desc = "Manage equipment & stock inputs") {
                            showQuickActionsDialog = false
                            onNavigate("inventory")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQuickActionsDialog = false }) {
                        Text("Close", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun NotificationRow(title: String, desc: String) {
    Column {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B5E20))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = desc, fontSize = 12.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

@Composable
fun QuickActionButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(18.sp.value.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, color = Color(0xFF1B5E20), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MetricCard(
    title: String,
    subtitle: String,
    desc: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconBg,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                    }
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            Text(text = subtitle, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    badge: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconBg,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.weight(1f)
                    )
                    Badge(containerColor = Color(0xFFE8F5E9)) {
                        Text(text = badge, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun QuickActionMenuRow(icon: ImageVector, title: String, desc: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF4F8F4)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B5E20))
                Text(text = desc, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

