package com.godark14.myhabit.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.unit.dp
import com.godark14.myhabit.MyHabitApplication
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class HabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as MyHabitApplication).repository
        val habits = repository.getAllHabits().first()
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()
        val completedIds = repository.getCompletedHabitIds(todayEpochDay)

        val visibleHabits = habits.filter { habit ->
            val days = habit.repeatDays.split(",").mapNotNull { it.toIntOrNull() }.toSet()
            days.isEmpty() || days.contains(today.dayOfWeek.value - 1)
        }.take(5)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(
                        ColorProvider(
                            day = Color(0xFFF2E9E1),
                            night = Color(0xFF2B1E14)
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Today's habits",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
                if (visibleHabits.isEmpty()) {
                    Text(text = "Nothing scheduled today")
                } else {
                    visibleHabits.forEach { habit ->
                        val isCompleted = habit.id in completedIds
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(
                                    actionRunCallback<ToggleHabitAction>(
                                        actionParametersOf(habitIdKey to habit.id)
                                    )
                                )
                        ) {
                            Text(text = if (isCompleted) "✅ " else "⬜ ")
                            Text(text = habit.name)
                        }
                    }
                }
            }
        }
    }
}