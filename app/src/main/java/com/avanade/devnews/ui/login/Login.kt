package com.avanade.devnews.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.feature.auth.login.presentation.LoginViewModel
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.components.DevNewsSearchField
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onGoToRegister: () -> Unit = {}
) {
    val spacing = DevNewsDesignTokens.spacing
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.large)
            .padding(top = spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(220.dp)
                .width(320.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = DevNewsDesignTokens.cardShape
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                painter = painterResource(id = R.drawable.logo_devnews),
                contentDescription = "DevNews Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            )
            Text(
                text = "Bem-vindo ao DevNews!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(spacing.medium))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.small),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                DevNewsSearchField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username"
                )
                DevNewsSearchField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password"
                )
                DevNewsPrimaryActionButton(
                    text = if (uiState.isLoading) "Entrando..." else "Login",
                    onClick = {
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("clique_botao_login", null)
                        viewModel.login(username, password)
                    }
                )
                uiState.errorMessage?.takeIf { it.isNotBlank() }?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    TextButton(onClick = {}) {
                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ainda não tem conta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onGoToRegister) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    DevNewsTheme {
        LoginScreen()
    }
}
