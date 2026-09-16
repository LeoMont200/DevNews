package com.avanade.devnews.ui.cadastro

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.feature.auth.register.presentation.RegisterViewModel
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.components.DevNewsPasswordField
import com.avanade.devnews.ui.designsystem.components.DevNewsSearchField
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onGoToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = DevNewsDesignTokens.spacing
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
            viewModel.resetRegisterSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.medium)
            .padding(top = spacing.medium)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(170.dp)
                .width(260.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = DevNewsDesignTokens.cardShape
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_devnews),
                contentDescription = "DevNews Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(spacing.small))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "Cadastro",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Crie sua conta para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.small),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                DevNewsSearchField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email"
                )
                DevNewsPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Senha"
                )
                DevNewsPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirmar senha",
                )

                DevNewsPrimaryActionButton(
                    text = if (uiState.isLoading) "Cadastrando..." else "Cadastrar",
                    onClick = {
                        viewModel.register(email, password, confirmPassword)
                    }
                )
                uiState.error?.takeIf { it.isNotBlank() }?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.large))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Já possui uma conta?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onGoToLogin) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    DevNewsTheme {
        RegisterScreen()
    }
}