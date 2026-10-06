package com.avanade.devnews.core.notifications

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

data class NotificationHistoryEntry(
    val title: String,
    val body: String,
    val timestamp: Long
)

object NotificationHistoryStore {
    private const val PREFS_NAME = "devnews_notification_history"
    private const val KEY_HISTORY = "history"
    private const val MAX_ENTRIES = 50

    private val historyState = MutableStateFlow<List<NotificationHistoryEntry>>(emptyList())
    private var isLoaded = false

    fun observe(context: Context): StateFlow<List<NotificationHistoryEntry>> {
        ensureLoaded(context)
        return historyState
    }

    fun recordNotification(
        context: Context,
        title: String,
        body: String
    ) {
        ensureLoaded(context)

        val updatedHistory = listOf(
            NotificationHistoryEntry(
                title = title,
                body = body,
                timestamp = System.currentTimeMillis()
            )
        ) + historyState.value

        val limitedHistory = updatedHistory.take(MAX_ENTRIES)
        historyState.value = limitedHistory
        persist(context, limitedHistory)
    }

    private fun ensureLoaded(context: Context) {
        if (isLoaded) {
            return
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawHistory = prefs.getString(KEY_HISTORY, null)
        historyState.value = rawHistory?.let(::parseHistory).orEmpty()
        isLoaded = true
    }

    private fun persist(
        context: Context,
        history: List<NotificationHistoryEntry>
    ) {
        val jsonArray = JSONArray()
        history.forEach { entry ->
            jsonArray.put(
                JSONObject()
                    .put("title", entry.title)
                    .put("body", entry.body)
                    .put("timestamp", entry.timestamp)
            )
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_HISTORY, jsonArray.toString())
            .apply()
    }

    private fun parseHistory(rawHistory: String): List<NotificationHistoryEntry> {
        val jsonArray = JSONArray(rawHistory)
        return buildList {
            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(index) ?: continue
                add(
                    NotificationHistoryEntry(
                        title = item.optString("title"),
                        body = item.optString("body"),
                        timestamp = item.optLong("timestamp")
                    )
                )
            }
        }
    }
}
