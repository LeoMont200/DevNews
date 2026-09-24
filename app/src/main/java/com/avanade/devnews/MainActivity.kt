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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.avanade.devnews.feature.auth.session.presentation.SessionViewModel
import com.avanade.devnews.ui.cadastro.RegisterScreen
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.theme.FlavorTheme
import com.avanade.devnews.ui.login.LoginScreen
import com.avanade.devnews.ui.login.RecoveryScreen
import dagger.hilt.android.AndroidEntryPoint

private enum class MainScreen {
    HOME,
    LOGIN,
    REGISTER,
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
            val sessionViewModel: SessionViewModel = hiltViewModel()
            val sessionUiState by sessionViewModel.uiState.collectAsState()
            var currentScreen by rememberSaveable { mutableStateOf(MainScreen.HOME) }
            var hasResolvedStartDestination by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(
                sessionUiState.isCheckingSession,
                sessionUiState.isSessionActive,
                hasResolvedStartDestination
            ) {
                if (!hasResolvedStartDestination && !sessionUiState.isCheckingSession) {
                    currentScreen = if (sessionUiState.isSessionActive) {
                        MainScreen.SHOWCASE
                    } else {
                        MainScreen.HOME
                    }
                    hasResolvedStartDestination = true
                }
            }

            BackHandler(
                enabled = currentScreen == MainScreen.LOGIN ||
                    currentScreen == MainScreen.REGISTER ||
                    currentScreen == MainScreen.RECOVERY
            ) {
                currentScreen = MainScreen.HOME
            }

            DevNewsTheme(flavor = FlavorTheme.PROD) {
                when (currentScreen) {
                    MainScreen.HOME -> {
                        HomeScreen(
                            onOpenLogin = { currentScreen = MainScreen.LOGIN },
                            onOpenRegister = { currentScreen = MainScreen.REGISTER }
                        )
                    }
                    MainScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = {
                                currentScreen = MainScreen.SHOWCASE
                            },
                            onGoToRegister = { currentScreen = MainScreen.REGISTER },
                            onForgotPasswordClick = { currentScreen = MainScreen.RECOVERY }
                        )
                    }
                    MainScreen.REGISTER -> {
                        RegisterScreen(
                            onRegisterSuccess = { currentScreen = MainScreen.LOGIN },
                            onGoToLogin = { currentScreen = MainScreen.LOGIN }
                        )
                    }
                    MainScreen.RECOVERY -> {
                        RecoveryScreen(onBackToLoginClick = { currentScreen = MainScreen.LOGIN })
                    }
                    MainScreen.SHOWCASE -> {
                        DesignSystemShowcaseScreen(
                            onLogoutClick = {
                                sessionViewModel.logout()
                                currentScreen = MainScreen.HOME
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HomeScreen(
        onOpenLogin: () -> Unit,
        onOpenRegister: () -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Button(onClick = onOpenLogin) {
                    Text(text = "Login")
                }

                Button(onClick = onOpenRegister) {
                    Text(text = "Register")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun HomeScreenPreview() {
        DevNewsTheme(flavor = FlavorTheme.PROD) {
            HomeScreen(
                onOpenLogin = {},
                onOpenRegister = {}
            )
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
