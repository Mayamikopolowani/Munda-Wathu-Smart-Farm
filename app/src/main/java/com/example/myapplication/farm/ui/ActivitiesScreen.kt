package com.example.myapplication.farm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.farm.data.FarmActivity
import com.example.myapplication.farm.data.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val activities by viewModel.activities.collectAsState()
    val farms by viewModel.farms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedFarmId by remember { mutableStateOf(farms.firstOrNull()?.id ?: "") }
    var activityType by remember { mutableStateOf("Land Preparation") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    val activityTypes = listOf("Land Preparation", "Planting", "Weeding", "Fertilizer Application", "Pest Control", "Harvesting")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Tracking (${activities.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Task, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(activities) { act ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
                                color = MaterialTheme.colorScheme.primary
                            )
                            Badge { Text("MWK ${act.cost.toInt()}") }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Date: ${act.date}", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = act.notes, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Record Farm Activity") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Activity Type:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        activityTypes.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = activityType == type,
                                    onClick = { activityType = type }
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
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("Cost (MWK)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Description") },
                            modifier = Modifier.fillMaxWidth()
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
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
