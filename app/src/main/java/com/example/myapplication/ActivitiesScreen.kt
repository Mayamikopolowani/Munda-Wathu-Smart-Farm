package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val activities by viewModel.activities.collectAsState()
    val farms by viewModel.farms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var activityType by remember { mutableStateOf("Land Preparation") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    val activityTypes = listOf("Land Preparation", "Planting", "Weeding", "Fertilizer Application", "Pest Control", "Harvesting")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Tracking (${activities.size})", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF1B5E20),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F8F4))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(activities) { act ->
                var showDeleteConfirm by remember { mutableStateOf(false) }
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
                            Text(
                                text = act.activityType,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = Color(0xFFE8F5E9)) {
                                    Text(text = "MWK ${act.cost.toInt()}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { showDeleteConfirm = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Activity", tint = Color(0xFFC62828))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Date: ${act.date}", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = act.notes, fontSize = 13.sp, color = Color.Gray)
                    }
                }

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("Delete Activity?", color = Color(0xFFC62828)) },
                        text = { Text("Are you sure you want to delete '${act.activityType}'? This will remove it locally and from Firebase.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteActivity(act.id)
                                    showDeleteConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancel", color = Color(0xFF2E7D32))
                            }
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Record Farm Activity", color = Color(0xFF1B5E20)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Activity Type:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                        activityTypes.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = activityType == type,
                                    onClick = { activityType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1B5E20))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = type, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("Cost (MWK)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val costVal = cost.toDoubleOrNull() ?: 0.0
                            val farmId = farms.firstOrNull()?.id ?: ""
                            if (farmId.isNotBlank()) {
                                viewModel.addActivity(
                                    farmId = farmId,
                                    cropId = null,
                                    activityType = activityType,
                                    date = date.ifBlank { "2026-02-15" },
                                    notes = notes.ifBlank { "Routine activity" },
                                    cost = costVal
                                )
                                date = ""
                                notes = ""
                                cost = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = Color(0xFF2E7D32))
                    }
                }
            )
        }
    }
}
