package com.programa1.tasklist.Receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.programa1.tasklist.R
import com.programa1.tasklist.activities.TaskDetailActivity
import com.programa1.tasklist.data.TaskDAO

class ReminderReceiver : BroadcastReceiver() {
    companion object {
       const val TASK_ID = "TASK_ID"
    }
    override fun onReceive(context: Context, intent: Intent) {


        val id = intent.getIntExtra(TASK_ID, -1)
        val task = TaskDAO(context).getById(id) ?: return

        Log.i("ALARM_TEST", "Se ha recibido una alarma para la tarea ")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Crear canal SIEMPRE (seguro aunque ya exista)


            val channel = NotificationChannel(
                "task_channel",
                context.getString(R.string.notification_title),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, task!!.category.id)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)

        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 2. Notificación
        val notification = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(task.title)
            .setContentText(task.description.ifBlank {
                context.getString(R.string.notification_title)
            })
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(task.description.ifBlank {
                        context.getString(R.string.notification_title)
                    })
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}