package com.ruicomp.cmptemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruicomp.cmptemplate.features.call_history.presentation.screens.CallHistoryScreen
import com.ruicomp.cmptemplate.features.home.presentation.screens.HomeScreen
import com.ruicomp.cmptemplate.features.saved_caller.presentation.screens.SavedCallerScreen
import com.ruicomp.cmptemplate.features.schedule.presentation.screens.ScheduleCallScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCallNow = { /*TODO*/ },
                onScheduleCall = { navController.navigate("schedule_call") },
                onSavedCaller = { navController.navigate("saved_caller") },
                onCallHistory = { navController.navigate("call_history") }
            )
        }
        composable("schedule_call") {
            ScheduleCallScreen(onBack = { navController.popBackStack() })
        }
        composable("saved_caller") {
            SavedCallerScreen(onBack = { navController.popBackStack() })
        }
        composable("call_history") {
            CallHistoryScreen(onBack = { navController.popBackStack() })
        }
    }
} 