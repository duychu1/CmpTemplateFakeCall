package com.ruicomp.cmptemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.ruicomp.cmptemplate.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
//            val isDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = false

            // Key logic: We want dark icons when the theme is NOT dark
            val useDarkIcons = !isDarkTheme

            // Use DisposableEffect to set the status bar icons
            // It will be cleaned up when the composable leaves the composition
            DisposableEffect(useDarkIcons) {
                val window = (this@MainActivity).window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = useDarkIcons
                onDispose {} // No cleanup needed
            }
            MaterialTheme {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}