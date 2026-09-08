package com.avanade.devnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.avanade.devnews.ui.designsystem.showcase.DesignSystemShowcaseScreen
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevNewsTheme {
                var openDesignSystemShowcase by remember { mutableStateOf(false) }

                if (openDesignSystemShowcase) {
                    DesignSystemShowcaseScreen()
                } else {
                    DiscoverScreen(
                        onOpenDesignSystem = { openDesignSystemShowcase = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverScreen(onOpenDesignSystem: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onOpenDesignSystem) {
            Text(text = "Design System")
        }
    }
}