package com.example.whattodo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action == "NOTIFICATION_ACTION") {
                val title = intent.getStringExtra("title") ?: "Default Title"
                val notificationService = WhatToDoNotificationService(context, title)
                notificationService.showBasicNotification()
            }
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Error: ${e.message}")
            e.printStackTrace()
        }
    }
}