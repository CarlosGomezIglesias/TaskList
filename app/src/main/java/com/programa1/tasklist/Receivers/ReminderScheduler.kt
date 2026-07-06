package com.programa1.tasklist.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.programa1.tasklist.Receivers.ReminderReceiver
import com.programa1.tasklist.data.Task

object ReminderScheduler {

    fun schedule(context: Context, task: Task) {

        if (!task.notification || task.notificationDate == null) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            task.notificationDate!!,
            pendingIntent
        )

        Log.d(
            "ALARM_TEST",
            "Alarma programada para ${java.util.Date(task.notificationDate!!)}"
        )
    }

    fun cancel(context: Context, task: Task) {

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        Log.d("ALARM_TEST", "Alarma cancelada")
    }
}