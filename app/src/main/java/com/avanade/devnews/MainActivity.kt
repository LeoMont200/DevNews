package com.avanade.devnews

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.login.LoginScreen
import com.avanade.devnews.ui.login.RecoveryScreen
import dagger.hilt.android.AndroidEntryPoint

private enum class MainScreen {
    HOME,
    LOGIN,
    RECOVERY,
    SHOWCASE
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(MainScreen.HOME) }

            BackHandler(enabled = currentScreen != MainScreen.HOME) {
                currentScreen = MainScreen.HOME
            }

            DevNewsTheme {
                when (currentScreen) {
                    MainScreen.HOME -> {
                        HomeScreen(onOpenLogin = { currentScreen = MainScreen.LOGIN })
                    }
                    MainScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = { currentScreen = MainScreen.SHOWCASE },
                            onForgotPasswordClick = { currentScreen = MainScreen.RECOVERY }
                        )
                    }
                    MainScreen.RECOVERY -> {
                        RecoveryScreen(onBackToLoginClick = { currentScreen = MainScreen.LOGIN })
                    }
                    MainScreen.SHOWCASE -> {
                        DesignSystemShowcaseScreen()
                    }
                }
            }
        }
    }

    @Composable
    private fun HomeScreen(onOpenLogin: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = onOpenLogin) {
                Text(text = "Login")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun HomeScreenPreview() {
        DevNewsTheme {
            HomeScreen(onOpenLogin = {})
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permissionState = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            return
        }

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
