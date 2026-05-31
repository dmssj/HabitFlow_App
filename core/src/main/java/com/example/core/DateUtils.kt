package com.example.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatEpochDayMonth(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM", Locale("ru"))
        return sdf.format(Date(timestamp))
    }
}
