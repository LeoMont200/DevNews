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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.feature.news.detail.presentation.NewsDetailScreen
import com.avanade.devnews.feature.news.list.presentation.NewsListScreen
import com.avanade.devnews.ui.cadastro.RegisterScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.theme.FlavorTheme
import com.avanade.devnews.ui.login.LoginScreen
import com.avanade.devnews.ui.login.RecoveryScreen
import dagger.hilt.android.AndroidEntryPoint

private sealed interface MainScreen {
    data object Home : MainScreen
    data object Login : MainScreen
    data object Register : MainScreen
    data object Recovery : MainScreen
    data object NewsList : MainScreen
    data class NewsDetail(val article: NewsArticle) : MainScreen
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
            var currentScreen by remember { mutableStateOf<MainScreen>(MainScreen.Home) }

            BackHandler(enabled = currentScreen != MainScreen.Home) {
                currentScreen = when (currentScreen) {
                    MainScreen.Home -> MainScreen.Home
                    MainScreen.Login -> MainScreen.Home
                    MainScreen.Register -> MainScreen.Home
                    MainScreen.Recovery -> MainScreen.Login
                    MainScreen.NewsList -> MainScreen.Home
                    is MainScreen.NewsDetail -> MainScreen.NewsList
                }
            }

            DevNewsTheme(flavor = FlavorTheme.PROD) {
                when (currentScreen) {
                    MainScreen.Home -> {
                        HomeScreen(
                            onOpenLogin = { currentScreen = MainScreen.Login },
                            onOpenRegister = { currentScreen = MainScreen.Register }
                        )
                    }
                    MainScreen.Login -> {
                        LoginScreen(
                            onLoginSuccess = { currentScreen = MainScreen.NewsList },
                            onGoToRegister = { currentScreen = MainScreen.Register },
                            onForgotPasswordClick = { currentScreen = MainScreen.Recovery }
                        )
                    }
                    MainScreen.Register -> {
                        RegisterScreen(
                            onRegisterSuccess = { currentScreen = MainScreen.Login },
                            onGoToLogin = { currentScreen = MainScreen.Login }
                        )
                    }
                    MainScreen.Recovery -> {
                        RecoveryScreen(onBackToLoginClick = { currentScreen = MainScreen.Login })
                    }
                    MainScreen.NewsList -> {
                        NewsListScreen(
                            onArticleClick = { article ->
                                currentScreen = MainScreen.NewsDetail(article)
                            }
                        )
                    }
                    is MainScreen.NewsDetail -> {
                        val article = (currentScreen as MainScreen.NewsDetail).article
                        NewsDetailScreen(
                            article = article,
                            onBack = { currentScreen = MainScreen.NewsList }
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
