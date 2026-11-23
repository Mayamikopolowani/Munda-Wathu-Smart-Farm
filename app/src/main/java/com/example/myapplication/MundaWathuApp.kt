package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MundaWathuApp() {
    val navController = rememberNavController()
    val viewModel: FarmViewModel = viewModel()

    val startDestination = "welcome"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoggedIn = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegistered = {
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
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
        composable("inventory") {
            InventoryScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("weather") {
            WeatherScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("market_prices") {
            MarketPricesScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("reminders") {
            RemindersScreen {
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
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}
