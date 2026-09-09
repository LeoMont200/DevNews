package com.avanade.devnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

private enum class MainScreen {
    HOME,
    DESIGN_SYSTEM,
    LOGIN
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                                onOpenLogin = { currentScreen = MainScreen.LOGIN },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    MainScreen.DESIGN_SYSTEM -> DesignSystemShowcaseScreen()
                    MainScreen.LOGIN -> LoginScreen(
                        onLoginSuccess = { currentScreen = MainScreen.DESIGN_SYSTEM }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    onOpenLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello $name!")
        Button(onClick = onOpenLogin) {
            Text(text = "Tela login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DevNewsTheme {
        Greeting(
            name = "Android",
            onOpenLogin = {}
        )
    }
}
