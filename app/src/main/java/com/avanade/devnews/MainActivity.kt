package com.avanade.devnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import com.avanade.devnews.feature.auth.login.presentation.LoginViewModel
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.login.LoginScreen
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint

private enum class MainScreen {
    HOME,
    DESIGN_SYSTEM,
    LOGIN
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(MainScreen.HOME) }

            BackHandler(enabled = currentScreen != MainScreen.HOME) {
                currentScreen = MainScreen.HOME
            }

            DevNewsTheme {
                when (currentScreen) {
                    MainScreen.HOME -> {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Greeting(
                                name = "Android",
                                onTestAnalyticsClick = {
                                    Log.d("FirebaseAnalyticsTest", "Botao clicado, enviando evento clique_botao_teste")
                                    firebaseAnalytics.logEvent("clique_botao_teste", null)
                                },
                                onOpenDesignSystem = { currentScreen = MainScreen.DESIGN_SYSTEM },
                                onOpenLogin = { currentScreen = MainScreen.LOGIN },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    MainScreen.DESIGN_SYSTEM -> DesignSystemShowcaseScreen()
                    MainScreen.LOGIN -> LoginScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    onTestAnalyticsClick: () -> Unit,
    onOpenDesignSystem: () -> Unit,
    onOpenLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello $name!")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onTestAnalyticsClick) {
                Text(text = "Testar Analytics")
            }
            Button(onClick = onOpenDesignSystem) {
                Text(text = "Design System")
            }
            Button(onClick = onOpenLogin) {
                Text(text = "Tela login")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DevNewsTheme {
        Greeting(
            name = "Android",
            onTestAnalyticsClick = {},
            onOpenDesignSystem = {},
            onOpenLogin = {}
        )
    }
}
