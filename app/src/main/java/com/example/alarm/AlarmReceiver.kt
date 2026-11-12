package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.audio.TtsManager
import com.example.MainActivity
import com.example.R
import com.example.RiseApplication

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ALARM_ID = "rise_morning_alarm_channel"
        const val CHANNEL_EVENING_ID = "rise_evening_checkin_channel"
        const val CHANNEL_SUNDAY_ID = "rise_sunday_checkin_channel"
        const val NOTIFICATION_ALARM_ID = 9001
        const val NOTIFICATION_EVENING_ID = 9002
        const val NOTIFICATION_SUNDAY_ID = 9003
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TYPE) ?: AlarmScheduler.TYPE_MORNING_ALARM

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Rise:AlarmWakeLock"
        )
        wakeLock?.acquire(30000L)

        createNotificationChannels(context)

        when (alarmType) {
            AlarmScheduler.TYPE_MORNING_ALARM -> handleMorningAlarm(context)
            AlarmScheduler.TYPE_SUNDAY_CHECKIN -> handleSundayCheckIn(context)
            AlarmScheduler.TYPE_NIGHT_PREGEN -> handleNightPreGeneration(context)
            else -> handleEveningCheckIn(context)
        }
    }

    private fun handleMorningAlarm(context: Context) {
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            1005,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ALARM_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌅 Rise & Shine!")
            .setContentText("Morning alarm is ringing loud! Tap to wake up and receive your daily briefing.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 600, 200, 600, 200))
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ALARM_ID, notification)

        try {
            context.startActivity(alarmIntent)
        } catch (ignored: Exception) {}

        // Re-arm for tomorrow
        val scheduler = AlarmScheduler(context)
        scheduler.scheduleMorningAlarm(7, 0)
    }

    private fun handleEveningCheckIn(context: Context) {
        val checkInIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_EVENING_CHECKIN", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1006,
            checkInIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EVENING_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌙 Evening Check-in & Planning")
            .setContentText("Review today's score and set tomorrow's goals to protect your streak.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_EVENING_ID, notification)

        AlarmScheduler(context).scheduleEveningCheckIn(20, 30)
    }

    private fun handleSundayCheckIn(context: Context) {
        val checkInIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_SUNDAY_CHECKIN", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1007,
            checkInIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SUNDAY_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🧭 Sunday Night Strategic Review (9:00 PM)")
            .setContentText("Review last week's charts, set next week's focus, and claim +200 XP bounty!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_SUNDAY_ID, notification)

        AlarmScheduler(context).scheduleSundayCheckIn(21, 0)
    }

    private fun handleNightPreGeneration(context: Context) {
        val app = context.applicationContext as? RiseApplication ?: return
        app.applicationScope.launch(Dispatchers.IO) {
            try {
                val profile = app.database.userProfileDao().getUserProfile() ?: return@launch
                val apiKey = app.securePreferences.getGeminiApiKey() ?: ""
                if (!profile.useRealisticVoice || apiKey.isBlank()) {
                    return@launch
                }

                val tomorrowStr = app.repository.getTomorrowString()
                // Only generate if not already generated earlier (e.g. at evening check-in)
                if (profile.lastMorningAudioGeneratedForDate == tomorrowStr) {
                    return@launch
                }

                val tomorrowTasks = app.repository.getTasksForDateSync(tomorrowStr)
                val quote = app.repository.peekOrGetQuoteForBriefing()

                val script = TtsManager.buildMorningScript(
                    userName = profile.userName,
                    quoteText = quote.text,
                    quoteAuthor = quote.author,
                    todayQuestion = profile.morningWinQuestion,
                    tasks = tomorrowTasks.map { it.title }
                )

                val morningFile = app.geminiTtsService.getMorningAudioFile()
                val result = app.geminiTtsService.generateSpeech(
                    apiKey = apiKey,
                    text = script,
                    voiceName = profile.geminiVoiceName,
                    voiceStyle = profile.geminiVoiceStyle,
                    outputFile = morningFile
                )

                if (result.isSuccess) {
                    app.database.userProfileDao().updateUserProfile(
                        profile.copy(lastMorningAudioGeneratedForDate = tomorrowStr)
                    )
                    val todayStr = app.repository.getTodayString()
                    app.securePreferences.incrementGeminiDailyUsage(todayStr)
                }
            } catch (ignored: Exception) {
            } finally {
                AlarmScheduler(context).scheduleNightPreGeneration(23, 0)
            }
        }
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM_ID,
                context.getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.alarm_channel_description)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 600, 200, 600, 200)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val eveningChannel = NotificationChannel(
                CHANNEL_EVENING_ID,
                context.getString(R.string.evening_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.evening_channel_description)
            }

            val sundayChannel = NotificationChannel(
                CHANNEL_SUNDAY_ID,
                "Sunday Night Check-in",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weekly planning and strategic review reminder"
            }

            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(eveningChannel)
            notificationManager.createNotificationChannel(sundayChannel)
        }
    }
}
