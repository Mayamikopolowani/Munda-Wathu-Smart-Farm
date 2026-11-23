package com.example.myapplication.farm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.farm.data.Crop
import com.example.myapplication.farm.data.FarmViewModel
import com.example.myapplication.farm.data.HarvestRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestsScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val harvests by viewModel.harvests.collectAsState()
    val crops by viewModel.crops.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedCropId by remember { mutableStateOf(crops.firstOrNull()?.id ?: "") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Harvest & Sales (${harvests.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Agriculture, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Harvest")
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
            items(harvests) { harvest ->
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
                                text = harvest.cropName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Badge { Text("MWK ${harvest.totalSales.toInt()}") }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Quantity: ${harvest.quantity} kg", fontSize = 14.sp)
                        Text(text = "Unit Price: MWK ${harvest.unitPrice} / kg", fontSize = 14.sp)
                        Text(text = "Date: ${harvest.date}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Record Harvest & Sales") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select Crop:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        crops.forEach { crop ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedCropId == crop.id,
                                    onClick = { selectedCropId = crop.id }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "${crop.cropType} (${crop.farmName})", fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity (kg / bags)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = unitPrice,
                            onValueChange = { unitPrice = it },
                            label = { Text("Unit Price (MWK)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qtyVal = quantity.toDoubleOrNull() ?: 0.0
                            val priceVal = unitPrice.toDoubleOrNull() ?: 0.0
                            if (qtyVal > 0 && selectedCropId.isNotBlank()) {
                                viewModel.addHarvest(
                                    cropId = selectedCropId,
                                    quantity = qtyVal,
                                    unitPrice = priceVal,
                                    date = date.ifBlank { "2026-03-20" }
                                )
                                quantity = ""
                                unitPrice = ""
                                date = ""
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
