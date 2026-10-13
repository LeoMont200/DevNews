package com.avanade.devnews.feature.news.detail.presentation

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NewsWebViewScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    var webView: WebView? = null

    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                        }
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.loadUrl(url)
                }
            )
        }
    }
}
