package com.example.whattodo

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Calendar
import kotlin.random.Random

class  WhatToDoNotificationService (
    private val context: Context,
    private val title: String,

) {
    private val notificationManager =
        context.getSystemService(NotificationManager::class.java)

    fun showBasicNotification() {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,
            0, intent,
            PendingIntent.FLAG_IMMUTABLE )

        val notification = NotificationCompat.Builder(context, "WhatToDo_Notification")
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "This is the reminder notification for your task titled \"${title}\"Do not forget to complete it!."
            ))
            .setSmallIcon(R.drawable.wtd_notification)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }


    fun scheduleNotification(content: String, reminderTime: String?, notificationId: Int) {


        reminderTime?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                action = "NOTIFICATION_ACTION"
                putExtra("title", content)
            }

            val timeTokens = reminderTime.split(":")
            if (timeTokens.size == 2) {
                val hour = timeTokens[0].toIntOrNull() ?: 0
                val minute = timeTokens[1].toIntOrNull() ?: 0

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 5)
                }


                // Check if the time set is in the future
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1) // Set the alarm for the next day
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                Log.d("PendingIntentID", "PendingIntent ID: $notificationId")

                // Set the alarm using the calendar time
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }


        }

    }

    fun cancelNotification(id: Int) {
        try {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java)
            intent.action = "NOTIFICATION_ACTION" // Ensure action matches the scheduled intent
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("CancelNotification", "Notification cancelled successfully with ID: $id")
        } catch (e: Exception) {
            Log.e("CancelNotification", "Error cancelling notification: ${e.message}")
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "WhatToDo_Notification"
            val channelName = "WhatToDo Channel Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for WhatToDo task lists."
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


}