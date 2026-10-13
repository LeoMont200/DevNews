package com.avanade.devnews.feature.notifications.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.avanade.devnews.R
import com.avanade.devnews.core.notifications.NotificationHistoryEntry
import com.avanade.devnews.core.notifications.NotificationHistoryStore
import com.avanade.devnews.core.notifications.PushNotificationManager
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHistoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val spacing = DevNewsDesignTokens.spacing
    val notifications by NotificationHistoryStore.observe(context).collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notification_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.notification_history_back_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            item {
                TextButton(
                    onClick = {
                        PushNotificationManager.showNotification(
                            context = context,
                            title = context.getString(R.string.push_notification_test_title),
                            body = context.getString(R.string.push_notification_test_body)
                        )
                    }
                ) {
                    Text(text = stringResource(R.string.push_notification_test_button))
                }
            }

            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(spacing.large),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.notification_history_empty_state),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = notifications,
                    key = { entry -> entry.timestamp }
                ) { entry ->
                    NotificationHistoryCard(entry = entry)
                }
            }
        }
    }
}

@Composable
private fun NotificationHistoryCard(
    entry: NotificationHistoryEntry,
    modifier: Modifier = Modifier
) {
    val spacing = DevNewsDesignTokens.spacing

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = spacing.xSmall
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.xSmall)
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatNotificationTimestamp(entry.timestamp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun formatNotificationTimestamp(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        .format(Date(timestamp))
}
