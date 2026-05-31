package com.example.data.repository

import com.example.domain.model.HabitStatistics
import com.example.domain.repository.StatisticsRepository
import com.example.data.dao.HabitDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val dao: HabitDao
) : StatisticsRepository {

    override fun getHabitStatistics(): Flow<HabitStatistics> =
        combine(dao.getAllHabits(), dao.getAllProgress()) { habits, progressLogs ->
            val totalHabits = habits.size
            val completedHabitsCount = habits.count { it.completedToday >= it.targetPerDay }
            
            val averageCompletionRate = if (totalHabits > 0) {
                val totalProgressPercent = habits.sumOf { habit ->
                    val progressRatio = if (habit.targetPerDay > 0) {
                        habit.completedToday.toFloat() / habit.targetPerDay.toFloat()
                    } else {
                        0.0f
                    }
                    (progressRatio.coerceAtMost(1.0f) * 100).toDouble()
                }
                (totalProgressPercent / totalHabits).toFloat()
            } else {
                0.0f
            }

            val streakDays = calculateStreak(progressLogs.map { it.completedAt })

            val completionsByDay = mutableMapOf<Int, Int>().apply {
                (Calendar.SUNDAY..Calendar.SATURDAY).forEach { put(it, 0) }
            }
            val cal = Calendar.getInstance()
            progressLogs.forEach { log ->
                cal.timeInMillis = log.completedAt
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                completionsByDay[dayOfWeek] = (completionsByDay[dayOfWeek] ?: 0) + 1
            }

            val orderedCompletions = mutableMapOf<Int, Int>()
            val systemDaysToMonFirst = mapOf(
                Calendar.MONDAY to 1,
                Calendar.TUESDAY to 2,
                Calendar.WEDNESDAY to 3,
                Calendar.THURSDAY to 4,
                Calendar.FRIDAY to 5,
                Calendar.SATURDAY to 6,
                Calendar.SUNDAY to 7
            )
            for (i in 1..7) { orderedCompletions[i] = 0 }
            completionsByDay.forEach { (calendarDay, count) ->
                val orderedDay = systemDaysToMonFirst[calendarDay] ?: 1
                orderedCompletions[orderedDay] = (orderedCompletions[orderedDay] ?: 0) + count
            }

            HabitStatistics(
                totalHabits = totalHabits,
                completedHabitsCount = completedHabitsCount,
                averageCompletionRate = averageCompletionRate,
                streakDays = streakDays,
                completionsByDayOfWeek = orderedCompletions
            )
        }

    private fun calculateStreak(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0
        
        val cal = Calendar.getInstance()
        val days = timestamps.map { ts ->
            cal.timeInMillis = ts
            cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
        }.distinct().sortedDescending()

        if (days.isEmpty()) return 0

        var streak = 0
        val tempCal = Calendar.getInstance()
        
        val todayYear = tempCal.get(Calendar.YEAR)
        val todayDay = tempCal.get(Calendar.DAY_OF_YEAR)
        val todayKey = todayYear * 1000 + todayDay
        
        tempCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayYear = tempCal.get(Calendar.YEAR)
        val yesterdayDay = tempCal.get(Calendar.DAY_OF_YEAR)
        val yesterdayKey = yesterdayYear * 1000 + yesterdayDay

        val latestDay = days.first()
        if (latestDay != todayKey && latestDay != yesterdayKey) {
            return 0
        }

        var currentDayKey = todayKey
        
        if (!days.contains(todayKey) && days.contains(yesterdayKey)) {
            currentDayKey = yesterdayKey
        }

        while (true) {
            if (days.contains(currentDayKey)) {
                streak++
                val dYear = currentDayKey / 1000
                val dDay = currentDayKey % 1000
                val dateCal = Calendar.getInstance()
                dateCal.set(Calendar.YEAR, dYear)
                dateCal.set(Calendar.DAY_OF_YEAR, dDay)
                dateCal.add(Calendar.DAY_OF_YEAR, -1)
                currentDayKey = dateCal.get(Calendar.YEAR) * 1000 + dateCal.get(Calendar.DAY_OF_YEAR)
            } else {
                break
            }
        }
        return streak
    }
}
