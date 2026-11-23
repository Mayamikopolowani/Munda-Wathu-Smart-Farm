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
fun FarmsScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val lang by viewModel.language.collectAsState()
    val isChichewa = lang == "Chichewa"
    val farms by viewModel.farms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var farmName by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var farmLocation by remember { mutableStateOf("") }
    var farmDesc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isChichewa) "Mindayi Yanga (${farms.size})" else "My Farms (${farms.size})", color = Color.White) },
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
                Icon(Icons.Default.Add, contentDescription = "Add Farm")
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
            items(farms) { farm ->
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
                                text = farm.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = Color(0xFFE8F5E9)) {
                                    Text(text = farm.size, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { showDeleteConfirm = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Farm", tint = Color(0xFFC62828))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Location: ${farm.location}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = farm.description, fontSize = 13.sp, color = Color.Gray)
                    }
                }

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("Delete Farm?", color = Color(0xFFC62828)) },
                        text = { Text("Are you sure you want to delete '${farm.name}'? This will remove it locally and from Firebase.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteFarm(farm.id)
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
                title = { Text("Add New Farm", color = Color(0xFF1B5E20)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = farmName,
                            onValueChange = { farmName = it },
                            label = { Text("Farm Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = farmSize,
                            onValueChange = { farmSize = it },
                            label = { Text("Size (e.g. 5 Acres)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = farmLocation,
                            onValueChange = { farmLocation = it },
                            label = { Text("Location / Zone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = farmDesc,
                            onValueChange = { farmDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (farmName.isNotBlank()) {
                                viewModel.addFarm(farmName, farmSize.ifBlank { "1 Acre" }, farmLocation.ifBlank { "General" }, farmDesc)
                                farmName = ""
                                farmSize = ""
                                farmLocation = ""
                                farmDesc = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("Add")
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
