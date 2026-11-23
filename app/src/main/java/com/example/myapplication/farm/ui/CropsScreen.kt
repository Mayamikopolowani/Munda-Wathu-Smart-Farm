package com.example.myapplication.farm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.farm.data.Crop
import com.example.myapplication.farm.data.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropsScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val crops by viewModel.crops.collectAsState()
    val farms by viewModel.farms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedFarmId by remember { mutableStateOf(farms.firstOrNull()?.id ?: "") }
    var cropType by remember { mutableStateOf("") }
    var plantingDate by remember { mutableStateOf("") }
    var expectedHarvestDate by remember { mutableStateOf("") }
    var seedInfo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crops Management (${crops.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Eco, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Crop")
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
            items(crops) { crop ->
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
                                text = crop.cropType,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Badge { Text(crop.farmName) }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Planting Date: ${crop.plantingDate}", fontSize = 14.sp)
                        Text(text = "Expected Harvest: ${crop.expectedHarvestDate}", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Seed: ${crop.seedInfo}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Crop") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select Farm:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        farms.forEach { farm ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedFarmId == farm.id,
                                    onClick = { selectedFarmId = farm.id }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = farm.name, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cropType,
                            onValueChange = { cropType = it },
                            label = { Text("Crop Type (e.g. Maize, Beans)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = plantingDate,
                            onValueChange = { plantingDate = it },
                            label = { Text("Planting Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = expectedHarvestDate,
                            onValueChange = { expectedHarvestDate = it },
                            label = { Text("Expected Harvest (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = seedInfo,
                            onValueChange = { seedInfo = it },
                            label = { Text("Seed Variety / Info") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (cropType.isNotBlank() && selectedFarmId.isNotBlank()) {
                                viewModel.addCrop(
                                    selectedFarmId,
                                    cropType,
                                    plantingDate.ifBlank { "2026-02-01" },
                                    expectedHarvestDate.ifBlank { "2026-06-01" },
                                    seedInfo.ifBlank { "Standard Seed" }
                                )
                                cropType = ""
                                plantingDate = ""
                                expectedHarvestDate = ""
                                seedInfo = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Add")
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
