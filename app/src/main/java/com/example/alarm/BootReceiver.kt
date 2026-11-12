package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.RiseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val app = context.applicationContext as? RiseApplication ?: return
            val pendingResult = goAsync()
            app.applicationScope.launch(Dispatchers.IO) {
                try {
                    val user = app.repository.getProfileSync()
                    val scheduler = AlarmScheduler(context)
                    if (user.alarmEnabled) {
                        scheduler.scheduleMorningAlarm(user.alarmHour, user.alarmMinute)
                    }
                    if (user.eveningReminderEnabled) {
                        scheduler.scheduleEveningCheckIn(user.eveningHour, user.eveningMinute)
                    }
                    if (user.sundayReminderEnabled) {
                        scheduler.scheduleSundayCheckIn(user.sundayReminderHour, user.sundayReminderMinute)
                    }
                    scheduler.scheduleNightPreGeneration(23, 0)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
