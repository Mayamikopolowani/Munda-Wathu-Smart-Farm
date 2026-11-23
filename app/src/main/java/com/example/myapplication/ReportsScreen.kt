package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun ReportsScreen(viewModel: FarmViewModel, onBack: () -> Unit) {
    val expenses by viewModel.expenses.collectAsState()
    val harvests by viewModel.harvests.collectAsState()
    val crops by viewModel.crops.collectAsState()
    val farms by viewModel.farms.collectAsState()

    val totalExpense = expenses.sumOf { it.amount }
    val totalRevenue = harvests.sumOf { it.totalSales }
    val netProfit = totalRevenue - totalExpense

    val expenseByCategory = expenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports & Analysis", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Executive Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow(label = "Total Registered Farms:", value = "${farms.size}")
                        SummaryRow(label = "Total Active Crops:", value = "${crops.size}")
                        SummaryRow(label = "Total Gross Revenue:", value = "MWK ${totalRevenue.toInt()}")
                        SummaryRow(label = "Total Expenditure:", value = "MWK ${totalExpense.toInt()}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Net Profit / Loss:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                            Text(
                                text = "MWK ${netProfit.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Expense Breakdown by Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            if (expenseByCategory.isEmpty()) {
                item {
                    Text(text = "No expenses recorded yet.", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                items(expenseByCategory.entries.size) { index ->
                    val entry = expenseByCategory.entries.elementAt(index)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = entry.key, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                            Text(text = "MWK ${entry.value.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
