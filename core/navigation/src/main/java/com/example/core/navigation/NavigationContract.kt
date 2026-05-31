package com.example.core.navigation

interface NavigationContract {
    fun navigateToHabitList()
    fun navigateToCreateHabit()
    fun navigateToHabitDetails(habitId: Long)
    fun navigateToStatistics()
    fun navigateToProfile()
    fun navigateToAbout()
    fun navigateToAboutCompany()
    fun navigateToDetection()
    fun navigateToCrashTest()
    fun navigateToLogin()
    fun navigateBack()
}
