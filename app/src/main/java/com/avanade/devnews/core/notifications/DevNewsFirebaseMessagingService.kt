package com.avanade.devnews.core.notifications

import android.util.Log
import com.avanade.devnews.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DevNewsFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.push_notification_default_title)
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: return

        PushNotificationManager.showNotification(
            context = applicationContext,
            title = title,
            body = body
        )

        Log.d(TAG, "Push received. Data payload: ${remoteMessage.data}")
    }

    private companion object {
        const val TAG = "DevNewsFCMService"
    }
}
