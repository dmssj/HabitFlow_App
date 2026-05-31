# ProGuard rules for HabitFlow

# Hilt / Dagger
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep class com.google.dagger.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# AppMetrica
-keep class io.appmetrica.analytics.** { *; }

# Yandex Maps
-keep class com.yandex.mapkit.** { *; }
-keep class com.yandex.runtime.** { *; }

# Room
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class com.example.data.database.** { *; }
-keep class com.example.data.entity.** { *; }

# Domain models (if they are used in serialization/reflection)
-keep class com.example.domain.model.** { *; }
