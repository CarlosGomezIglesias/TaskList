package com.programa1.tasklist.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.programa1.tasklist.data.TaskDAO
import com.programa1.tasklist.utils.ReminderScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BOOT_TEST", "===========================")
        Log.d("BOOT_TEST", "Receiver ejecutado")
        Log.d("BOOT_TEST", "Action = ${intent.action}")
        Log.d("BOOT_TEST", "===========================")

        if (
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }


        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d("ALARM_TEST", "Teléfono reiniciado. Reprogramando alarmas...")

        val taskDAO = TaskDAO(context)

        val now = System.currentTimeMillis()

        taskDAO.getAll().forEach { task ->

            if (task.notification &&
                task.notificationDate != null &&
                task.notificationDate!! > now) {

                ReminderScheduler.schedule(context, task)

                Log.d(
                    "ALARM_TEST",
                    "Reprogramada tarea ${task.id}"
                )
            }
        }
    }
}