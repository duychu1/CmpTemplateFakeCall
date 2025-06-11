package com.ruicomp.cmptemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruicomp.cmptemplate.features.call_history.presentation.CallHistoryScreen
import com.ruicomp.cmptemplate.features.home.presentation.HomeScreen
import com.ruicomp.cmptemplate.features.saved_caller.presentation.SavedCallerScreen
import com.ruicomp.cmptemplate.features.schedule.presentation.ScheduleCallScreen
import com.ruicomp.cmptemplate.features.settings.presentation.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onCallNow = { /*TODO*/ },
                onScheduleCall = { navController.navigate(ScheduleCall) },
                onSavedCaller = { navController.navigate(SavedCaller) },
                onCallHistory = { navController.navigate(CallHistory) },
                onSettingsClick = { navController.navigate(Settings) }
            )
        }
        composable<ScheduleCall> {
            ScheduleCallScreen(onBack = { navController.popBackStack() })
        }
        composable<SavedCaller> {
            SavedCallerScreen(onBack = { navController.popBackStack() })
        }
        composable<CallHistory> {
            CallHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable<Settings> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}