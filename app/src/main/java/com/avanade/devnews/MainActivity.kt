package com.avanade.devnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.avanade.devnews.ui.cadastro.RegisterScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.theme.FlavorTheme
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

private enum class MainScreen {
    HOME,
    LOGIN,
    REGISTER,
    SHOWCASE
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentFlavor by remember { mutableStateOf(FlavorTheme.PROD) }
            var currentScreen by remember { mutableStateOf(MainScreen.HOME) }

            BackHandler(enabled = currentScreen != MainScreen.HOME) {
                currentScreen = MainScreen.HOME
            }

            DevNewsTheme(flavor = currentFlavor) {
                when (currentScreen) {
                    MainScreen.HOME -> {
                        HomeScreen(
                            currentFlavor = currentFlavor,
                            onFlavorSelected = { currentFlavor = it },
                            onOpenLogin = { currentScreen = MainScreen.LOGIN },
                            onOpenRegister = { currentScreen = MainScreen.REGISTER }
                        )
                    }
                    MainScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = { currentScreen = MainScreen.SHOWCASE },
                            onGoToRegister = { currentScreen = MainScreen.REGISTER }
                        )
                    }
                    MainScreen.REGISTER -> {
                        RegisterScreen(
                            onGoToLogin = { currentScreen = MainScreen.LOGIN }
                        )
                    }
                    MainScreen.SHOWCASE -> {
                        DesignSystemShowcaseScreen()
                    }
                }
            }
        }
    }

    @Composable
    private fun HomeScreen(
        currentFlavor: FlavorTheme,
        onFlavorSelected: (FlavorTheme) -> Unit,
        onOpenLogin: () -> Unit,
        onOpenRegister: () -> Unit
    ) {
        var showFlavorMenu by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Box {
                    Button(onClick = { showFlavorMenu = !showFlavorMenu }) {
                        Text(text = "Flavor: ${currentFlavor.name}")
                    }
                    DropdownMenu(
                        expanded = showFlavorMenu,
                        onDismissRequest = { showFlavorMenu = false }
                    ) {
                        FlavorTheme.entries.forEach { flavor ->
                            DropdownMenuItem(
                                text = { Text(flavor.name) },
                                onClick = {
                                    onFlavorSelected(flavor)
                                    showFlavorMenu = false
                                }
                            )
                        }
                    }
                }

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
        DevNewsTheme {
            HomeScreen(
                currentFlavor = FlavorTheme.PROD,
                onFlavorSelected = {},
                onOpenLogin = {},
                onOpenRegister = {}
            )
        }
    }
}
