package com.avanade.devnews.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.feature.auth.forgotpassword.presentation.RecoveryViewModel
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.components.DevNewsSearchField
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens

private val BackArrowIcon: ImageVector =
    ImageVector.Builder(
        name = "BackArrow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(20f, 11f)
            lineTo(7.83f, 11f)
            lineTo(13.42f, 5.41f)
            lineTo(12f, 4f)
            lineTo(4f, 12f)
            lineTo(12f, 20f)
            lineTo(13.41f, 18.59f)
            lineTo(7.83f, 13f)
            lineTo(20f, 13f)
            close()
        }
    }.build()

@Composable
fun RecoveryScreen(
    modifier: Modifier = Modifier,
    viewModel: RecoveryViewModel = hiltViewModel(),
    onBackToLoginClick: () -> Unit = {}
) {
    val spacing = DevNewsDesignTokens.spacing
    val uiState by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.large)
            .padding(top = spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBackToLoginClick,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Voltar para login",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

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
                painter = painterResource(id = R.drawable.logo_devnews),
                contentDescription = "DevNews Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            )
            Text(
                text = "Recupere sua senha",
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
                text = "Esqueci minha senha",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Digite seu e-mail para receber o link de redefinição.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            DevNewsSearchField(
                value = email,
                onValueChange = {
                    email = it
                    if (uiState.errorMessage != null || uiState.successMessage != null) {
                        viewModel.clearFeedback()
                    }
                },
                placeholder = "E-mail"
            )
            DevNewsPrimaryActionButton(
                text = if (uiState.isLoading) "Enviando..." else "Enviar",
                onClick = { viewModel.recoverPassword(email) }
            )
            uiState.successMessage?.let { message ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = DevNewsDesignTokens.cardShape
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium)
                    )
                }
            }
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun RecoveryScreenPreview() {
    DevNewsTheme {
        RecoveryScreen(
            onBackToLoginClick = {}
        )
    }
}
