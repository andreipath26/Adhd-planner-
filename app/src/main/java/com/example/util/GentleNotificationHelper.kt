package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object GentleNotificationHelper {

    private const val CHANNEL_ID = "focusflow_gentle_nudges"
    private const val CHANNEL_NAME = "Gentle Focus & Flow Nudges"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gentle, non-intrusive focus cues and micro-step completion encouragement."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 80, 100)
                setShowBadge(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun sendFocusNudge(context: Context, title: String, message: String) {
        initNotificationChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        try {
            manager?.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: SecurityException) {
            // Permission might be denied in runtime, handled safely
        }
    }

    fun triggerGentleHaptic(context: Context, isSuccess: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                if (isSuccess) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 60, 50, 80),
                            intArrayOf(0, 120, 0, 200),
                            -1
                        )
                    )
                } else {
                    vibrator?.vibrate(VibrationEffect.createOneShot(40, 80))
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (isSuccess) {
                    vibrator?.vibrate(longArrayOf(0, 60, 50, 80), -1)
                } else {
                    vibrator?.vibrate(40)
                }
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    val gentleAffirmations = listOf(
        "Take a slow breath. You don't need to finish everything at once, just this 5-min step.",
        "Momentum is built one tiny win at a time. Be proud of starting!",
        "Stuck? Lower the barrier. What's the smallest action you can do in 60 seconds?",
        "Your brain is doing hard work today. Stay hydrated and be gentle with yourself.",
        "Consistency isn't doing it perfectly every day; it's returning without guilt.",
        "One micro-step down! Feel that dopamine spark."
    )

    fun getRandomAffirmation(): String {
        return gentleAffirmations.random()
    }
}
