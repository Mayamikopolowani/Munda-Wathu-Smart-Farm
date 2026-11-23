package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropsScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val lang by viewModel.language.collectAsState()
    val isChichewa = lang == "Chichewa"
    val context = LocalContext.current
    val crops by viewModel.crops.collectAsState()
    val farms by viewModel.farms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var cropType by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("CG7") }
    var acreage by remember { mutableStateOf("2.0 Acres") }
    var growthStage by remember { mutableStateOf("Germination") }
    var plantingDate by remember { mutableStateOf("2026-01-20") }
    var expectedHarvestDate by remember { mutableStateOf("2026-05-15") }
    var seedInfo by remember { mutableStateOf("Certified") }

    val stages = listOf("Germination", "Vegetative", "Flowering", "Fruiting", "Harvesting")

    val filteredCrops = if (searchQuery.isBlank()) {
        crops
    } else {
        crops.filter { it.cropType.contains(searchQuery, ignoreCase = true) || it.variety.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isChichewa) "Kusamalira Mbewu (${filteredCrops.size})" else "Crops Management (${filteredCrops.size})", color = Color.White) },
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
                Icon(Icons.Default.Add, contentDescription = "Add Crop")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F8F4))
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search crops (e.g. Maize, Groundnuts)") },
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

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCrops) { crop ->
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
                                    text = crop.cropType,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Badge(containerColor = Color(0xFFE8F5E9)) {
                                    Text(text = crop.growthStage, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Variety: ${crop.variety} | Acreage: ${crop.acreage}", fontSize = 14.sp, color = Color.DarkGray)
                            Text(text = "Farm: ${crop.farmName}", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var showStageDialog by remember { mutableStateOf(false) }
                                var showDeleteConfirm by remember { mutableStateOf(false) }

                                OutlinedButton(
                                    onClick = { showStageDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20))
                                ) {
                                    Text("Update Stage")
                                }

                                IconButton(onClick = { showDeleteConfirm = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Crop", tint = Color(0xFFC62828))
                                }

                                if (showStageDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showStageDialog = false },
                                        title = { Text("Update Growth Stage", color = Color(0xFF1B5E20)) },
                                        text = {
                                            Column {
                                                stages.forEach { st ->
                                                    TextButton(onClick = {
                                                        viewModel.updateCropStage(crop.id, st)
                                                        Toast.makeText(context, "Stage updated to $st", Toast.LENGTH_SHORT).show()
                                                        showStageDialog = false
                                                    }) {
                                                        Text(st, fontSize = 16.sp, color = Color(0xFF2E7D32))
                                                    }
                                                }
                                            }
                                        },
                                        confirmButton = {},
                                        dismissButton = {
                                            TextButton(onClick = { showStageDialog = false }) { Text("Cancel", color = Color.Gray) }
                                        }
                                    )
                                }

                                if (showDeleteConfirm) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteConfirm = false },
                                        title = { Text("Delete Crop?", color = Color(0xFFC62828)) },
                                        text = { Text("Are you sure you want to delete '${crop.cropType}'? This will remove it locally and from Firebase.") },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    viewModel.deleteCrop(crop.id)
                                                    Toast.makeText(context, "Crop deleted", Toast.LENGTH_SHORT).show()
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
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Crop", color = Color(0xFF1B5E20)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cropType,
                            onValueChange = { cropType = it },
                            label = { Text("Crop Name (e.g. Groundnuts)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = variety,
                            onValueChange = { variety = it },
                            label = { Text("Variety (e.g. CG7)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = acreage,
                            onValueChange = { acreage = it },
                            label = { Text("Acreage (e.g. 2.0 Acres)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = growthStage,
                            onValueChange = { growthStage = it },
                            label = { Text("Growth Stage (e.g. Germination)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (cropType.isBlank()) {
                                Toast.makeText(context, "Crop Name cannot be blank", Toast.LENGTH_SHORT).show()
                            } else {
                                val farmId = farms.firstOrNull()?.id ?: ""
                                viewModel.addCrop(
                                    farmId = farmId,
                                    cropType = cropType,
                                    variety = variety,
                                    acreage = acreage,
                                    growthStage = growthStage,
                                    plantingDate = plantingDate,
                                    expectedHarvestDate = expectedHarvestDate,
                                    seedInfo = seedInfo
                                )
                                Toast.makeText(context, "Crop '$cropType' added successfully!", Toast.LENGTH_SHORT).show()
                                cropType = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("Add Crop")
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
