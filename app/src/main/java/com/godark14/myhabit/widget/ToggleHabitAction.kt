package com.godark14.myhabit.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.godark14.myhabit.MyHabitApplication

val habitIdKey = ActionParameters.Key<Long>("habitId")

class ToggleHabitAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val habitId = parameters[habitIdKey] ?: return
        val repository = (context.applicationContext as MyHabitApplication).repository
        val habit = repository.getHabitById(habitId) ?: return
        repository.toggleHabitCompletion(habit)
        HabitWidget().update(context, glanceId)
    }
}