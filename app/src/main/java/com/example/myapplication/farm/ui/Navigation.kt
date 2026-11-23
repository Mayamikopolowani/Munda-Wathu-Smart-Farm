package com.example.myapplication.farm.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.farm.data.FarmViewModel

@Composable
fun MundaWathuApp() {
    val navController = rememberNavController()
    val viewModel: FarmViewModel = viewModel()
    val user by viewModel.currentUser.collectAsState()

    val startDestination = if (user != null) "dashboard" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(viewModel = viewModel) {
                navController.navigate("dashboard") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        }
        composable("dashboard") {
            DashboardScreen(viewModel = viewModel) { route ->
                navController.navigate(route)
            }
        }
        composable("farms") {
            FarmsScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("crops") {
            CropsScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("activities") {
            ActivitiesScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("expenses") {
            ExpensesScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("harvests") {
            HarvestsScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("reports") {
            ReportsScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate("auth") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}
