package com.godark14.myhabit.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.godark14.myhabit.MyHabitApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = (appContext as MyHabitApplication).repository
                val habits = repository.getAllHabits().first()
                habits.filter { it.remindersEnabled }.forEach { habit ->
                    ReminderScheduler.scheduleHabitReminder(appContext, habit)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}