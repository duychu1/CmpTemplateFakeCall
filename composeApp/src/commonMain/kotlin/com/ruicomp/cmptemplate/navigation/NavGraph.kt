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
import com.ruicomp.cmptemplate.features.language_setting.presentation.LanguageSettingScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onCallNow = { /*TODO*/ },
                onScheduleCall = { navController.navigate(ScheduleCallRoute) },
                onSavedCaller = { navController.navigate(SavedCallerRoute) },
                onCallHistory = { navController.navigate(CallHistoryRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) }
            )
        }
        composable<ScheduleCallRoute> {
            ScheduleCallScreen(onBack = { navController.popBackStack() })
        }
        composable<SavedCallerRoute> {
            SavedCallerScreen(onBack = { navController.popBackStack() })
        }
        composable<CallHistoryRoute> {
            CallHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLanguageClick = { navController.navigate(LanguageSettingRoute) }
            )
        }
        composable<LanguageSettingRoute> {
            LanguageSettingScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}