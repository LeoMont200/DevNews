package com.avanade.devnews

import android.app.Application
import android.util.Log
import com.avanade.devnews.core.notifications.PushNotificationManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DevNewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PushNotificationManager.createNotificationChannel(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Unable to fetch FCM token", task.exception)
                return@addOnCompleteListener
            }

            Log.d(TAG, "FCM token: ${task.result}")
        }
    }

    private companion object {
        const val TAG = "DevNewsApplication"
    }
}
