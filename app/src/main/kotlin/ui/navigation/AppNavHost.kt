package com.example.rewire.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rewire.manager.HabitManager
import com.example.rewire.ui.screens.HabitHomeScreen
import com.example.rewire.ui.screens.LabelManagementScreen

@Composable
fun AppNavHost(
    habitManager: HabitManager,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HabitHomeScreen(
                habitManager = habitManager,
                navController = navController
            )
        }
        
        composable("label_management") {
            LabelManagementScreen(
                navController = navController,
                habitManager = habitManager
            )
        }
        
        // Future screens can be added here:
        // composable("settings") { SettingsScreen(navController) }
        // composable("statistics") { StatisticsScreen(navController) }
        // composable("addiction_tracking") { AddictionTrackingScreen(navController) }
    }
}
