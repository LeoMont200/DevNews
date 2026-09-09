package com.avanade.devnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.theme.FlavorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentFlavor by remember { mutableStateOf(FlavorTheme.PROD) }
            var openDesignSystemShowcase by remember { mutableStateOf(false) }

            DevNewsTheme(flavor = currentFlavor) {
                if (openDesignSystemShowcase) {
                    DesignSystemShowcaseScreen()
                } else {
                    DiscoverScreen(
                        currentFlavor = currentFlavor,
                        onFlavorSelected = { currentFlavor = it },
                        onOpenDesignSystem = { openDesignSystemShowcase = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverScreen(
    currentFlavor: FlavorTheme,
    onFlavorSelected: (FlavorTheme) -> Unit,
    onOpenDesignSystem: () -> Unit
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

            Button(onClick = onOpenDesignSystem) {
                Text(text = "Design System")
            }
        }
    }
}