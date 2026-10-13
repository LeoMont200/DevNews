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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.feature.auth.session.presentation.SessionViewModel
import com.avanade.devnews.feature.favorites.presentation.FavoritesScreen
import com.avanade.devnews.feature.news.detail.presentation.NewsDetailScreen
import com.avanade.devnews.feature.news.detail.presentation.NewsWebViewScreen
import com.avanade.devnews.feature.news.list.presentation.NewsListScreen
import com.avanade.devnews.feature.notifications.presentation.NotificationHistoryScreen
import com.avanade.devnews.feature.profile.presentation.ProfileScreen
import com.avanade.devnews.ui.cadastro.RegisterScreen
import com.avanade.devnews.ui.designsystem.components.DevNewsBottomNavItem
import com.avanade.devnews.ui.designsystem.components.DevNewsBottomNavigationBar
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
    data object Favorites : MainScreen
    data object NotificationHistory : MainScreen
    data object Profile : MainScreen
    data class NewsDetail(val article: NewsArticle, val origin: NewsOrigin) : MainScreen
    data class NewsWebView(
        val url: String,
        val article: NewsArticle,
        val origin: NewsOrigin
    ) : MainScreen
}

private enum class NewsOrigin {
    NewsList,
    Favorites
}

private val mainScreenSaver: Saver<MainScreen, Any> = mapSaver(
    save = { screen ->
        when (screen) {
            MainScreen.Home -> mapOf("type" to "home")
            MainScreen.Login -> mapOf("type" to "login")
            MainScreen.Register -> mapOf("type" to "register")
            MainScreen.Recovery -> mapOf("type" to "recovery")
            MainScreen.NewsList -> mapOf("type" to "news_list")
            MainScreen.Favorites -> mapOf("type" to "favorites")
            MainScreen.NotificationHistory -> mapOf("type" to "notification_history")
            MainScreen.Profile -> mapOf("type" to "profile")
            is MainScreen.NewsDetail -> mapOf(
                "type" to "news_detail",
                "origin" to screen.origin.name,
                "sourceName" to screen.article.sourceName,
                "author" to screen.article.author,
                "title" to screen.article.title,
                "description" to screen.article.description,
                "content" to screen.article.content,
                "articleUrl" to screen.article.articleUrl,
                "imageUrl" to (screen.article.imageUrl ?: ""),
                "publishedAt" to screen.article.publishedAt
            )
            is MainScreen.NewsWebView -> mapOf(
                "type" to "news_webview",
                "url" to screen.url,
                "origin" to screen.origin.name,
                "sourceName" to screen.article.sourceName,
                "author" to screen.article.author,
                "title" to screen.article.title,
                "description" to screen.article.description,
                "content" to screen.article.content,
                "articleUrl" to screen.article.articleUrl,
                "imageUrl" to (screen.article.imageUrl ?: ""),
                "publishedAt" to screen.article.publishedAt
            )
        }
    },
    restore = { restored ->
        fun restoredArticle(): NewsArticle {
            return NewsArticle(
                sourceName = restored["sourceName"] as String,
                author = restored["author"] as String,
                title = restored["title"] as String,
                description = restored["description"] as String,
                content = restored["content"] as String,
                articleUrl = restored["articleUrl"] as String,
                imageUrl = (restored["imageUrl"] as String).ifBlank { null },
                publishedAt = restored["publishedAt"] as String
            )
        }

        when (restored["type"] as String) {
            "home" -> MainScreen.Home
            "login" -> MainScreen.Login
            "register" -> MainScreen.Register
            "recovery" -> MainScreen.Recovery
            "news_list" -> MainScreen.NewsList
            "favorites" -> MainScreen.Favorites
            "notification_history" -> MainScreen.NotificationHistory
            "profile" -> MainScreen.Profile
            "news_detail" -> MainScreen.NewsDetail(
                article = restoredArticle(),
                origin = NewsOrigin.valueOf(restored["origin"] as String)
            )
            "news_webview" -> MainScreen.NewsWebView(
                url = restored["url"] as String,
                article = restoredArticle(),
                origin = NewsOrigin.valueOf(restored["origin"] as String)
            )
            else -> MainScreen.Home
        }
    }
)

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
            var currentScreen by rememberSaveable(stateSaver = mainScreenSaver) {
                mutableStateOf<MainScreen>(MainScreen.Home)
            }
            var hasResolvedStartDestination by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(
                sessionUiState.isCheckingSession,
                sessionUiState.isSessionActive,
                hasResolvedStartDestination
            ) {
                if (!hasResolvedStartDestination && !sessionUiState.isCheckingSession) {
                    currentScreen = if (sessionUiState.isSessionActive) {
                        MainScreen.NewsList
                    } else {
                        MainScreen.Home
                    }
                    hasResolvedStartDestination = true
                }
            }

            BackHandler(enabled = currentScreen != MainScreen.Home) {
                currentScreen = when (val screen = currentScreen) {
                    MainScreen.Home -> MainScreen.Home
                    MainScreen.Login -> MainScreen.Home
                    MainScreen.Register -> MainScreen.Home
                    MainScreen.Recovery -> MainScreen.Login
                    MainScreen.NewsList -> MainScreen.Home
                    MainScreen.Favorites -> MainScreen.NewsList
                    MainScreen.NotificationHistory -> MainScreen.NewsList
                    MainScreen.Profile -> MainScreen.NewsList
                    is MainScreen.NewsDetail -> {
                        when (screen.origin) {
                            NewsOrigin.NewsList -> MainScreen.NewsList
                            NewsOrigin.Favorites -> MainScreen.Favorites
                        }
                    }
                    is MainScreen.NewsWebView -> {
                        MainScreen.NewsDetail(
                            article = screen.article,
                            origin = screen.origin
                        )
                    }
                }
            }

            DevNewsTheme(flavor = FlavorTheme.PROD) {
                val rootNewsScreens = setOf(
                    MainScreen.NewsList,
                    MainScreen.Favorites,
                    MainScreen.NotificationHistory,
                    MainScreen.Profile
                )
                val showBottomNavigation =
                    currentScreen in rootNewsScreens ||
                        currentScreen is MainScreen.NewsDetail ||
                        currentScreen is MainScreen.NewsWebView
                val selectedBottomTabIndex = when (currentScreen) {
                    MainScreen.NewsList -> 0
                    MainScreen.Favorites -> 1
                    MainScreen.NotificationHistory -> 2
                    MainScreen.Profile -> 3
                    is MainScreen.NewsDetail -> 0
                    is MainScreen.NewsWebView -> 0
                    else -> -1
                }

                val screenContent: @Composable (Modifier) -> Unit = { modifier ->
                    when (val screen = currentScreen) {
                        MainScreen.Home -> {
                            HomeScreen(
                                onOpenLogin = { currentScreen = MainScreen.Login },
                                onOpenRegister = { currentScreen = MainScreen.Register }
                            )
                        }

                        MainScreen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    currentScreen = MainScreen.NewsList
                                },
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
                                modifier = modifier,
                                onArticleClick = { article ->
                                    currentScreen = MainScreen.NewsDetail(
                                        article = article,
                                        origin = NewsOrigin.NewsList
                                    )
                                },
                                onNotificationHistoryClick = {
                                    currentScreen = MainScreen.NotificationHistory
                                }
                            )
                        }

                        MainScreen.Favorites -> {
                            FavoritesScreen(
                                modifier = modifier,
                                onArticleClick = { article ->
                                    currentScreen = MainScreen.NewsDetail(
                                        article = article,
                                        origin = NewsOrigin.Favorites
                                    )
                                }
                            )
                        }

                        MainScreen.NotificationHistory -> {
                            NotificationHistoryScreen(
                                modifier = modifier,
                                onBackClick = { currentScreen = MainScreen.NewsList }
                            )
                        }

                        MainScreen.Profile -> {
                            ProfileScreen(
                                modifier = modifier,
                                onLogoutClick = {
                                    sessionViewModel.logout()
                                    currentScreen = MainScreen.Home
                                    hasResolvedStartDestination = false
                                }
                            )
                        }

                        is MainScreen.NewsDetail -> {
                            NewsDetailScreen(
                                article = screen.article,
                                modifier = modifier,
                                onBack = {
                                    currentScreen = when (screen.origin) {
                                        NewsOrigin.NewsList -> MainScreen.NewsList
                                        NewsOrigin.Favorites -> MainScreen.Favorites
                                    }
                                },
                                onOpenWebView = { url ->
                                    currentScreen = MainScreen.NewsWebView(
                                        url = url,
                                        article = screen.article,
                                        origin = screen.origin
                                    )
                                }
                            )
                        }

                        is MainScreen.NewsWebView -> {
                            NewsWebViewScreen(
                                url = screen.url,
                                modifier = modifier
                            )
                        }
                    }
                }

                if (showBottomNavigation) {
                    Scaffold(
                        bottomBar = {
                            DevNewsBottomNavigationBar(
                                items = listOf(
                                    DevNewsBottomNavItem(
                                        label = getString(R.string.bottom_nav_home),
                                        icon = Icons.Outlined.Home,
                                        isSelected = selectedBottomTabIndex == 0
                                    ),
                                    DevNewsBottomNavItem(
                                        label = getString(R.string.bottom_nav_favorites),
                                        icon = Icons.Outlined.FavoriteBorder,
                                        isSelected = selectedBottomTabIndex == 1
                                    ),
                                    DevNewsBottomNavItem(
                                        label = getString(R.string.bottom_nav_notifications),
                                        icon = Icons.Outlined.Notifications,
                                        isSelected = selectedBottomTabIndex == 2
                                    ),
                                    DevNewsBottomNavItem(
                                        label = getString(R.string.bottom_nav_profile),
                                        icon = Icons.Outlined.PersonOutline,
                                        isSelected = selectedBottomTabIndex == 3
                                    )
                                ),
                                onItemClick = { index ->
                                    currentScreen = when (index) {
                                        0 -> MainScreen.NewsList
                                        1 -> MainScreen.Favorites
                                        2 -> MainScreen.NotificationHistory
                                        3 -> MainScreen.Profile
                                        else -> currentScreen
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        screenContent(Modifier.padding(innerPadding))
                    }
                } else {
                    screenContent(Modifier)
                }
            }
        }
    }

    @Composable
    private fun HomeScreen(
        onOpenLogin: () -> Unit,
        onOpenRegister: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome_image),
                contentDescription = "Welcome image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Bem-vindo ao DevNews",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Fique por dentro das principais noticias de tecnologia.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Cadastro")
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
