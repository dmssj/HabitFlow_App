package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.HabitDao
import com.example.data.entity.HabitEntity
import com.example.data.entity.HabitProgressEntity

@Database(entities = [HabitEntity::class, HabitProgressEntity::class], version = 1, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
