package com.avanade.devnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.avanade.devnews.ui.theme.DevNewsTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        enableEdgeToEdge()
        setContent {
            DevNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        onTestAnalyticsClick = {
                            Log.d("FirebaseAnalyticsTest", "Botao clicado, enviando evento clique_botao_teste")
                            firebaseAnalytics.logEvent("clique_botao_teste", null)
                        },
                        modifier = Modifier.padding(innerPadding)
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
fun Greeting(
    name: String,
    onTestAnalyticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello $name!")
        Button(onClick = onTestAnalyticsClick) {
            Text(text = "Testar Analytics")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DevNewsTheme {
        Greeting(name = "Android", onTestAnalyticsClick = {})
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