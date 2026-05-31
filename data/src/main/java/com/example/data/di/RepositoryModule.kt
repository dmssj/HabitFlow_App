package com.example.data.di

import com.example.domain.repository.HabitRepository
import com.example.domain.repository.StatisticsRepository
import com.example.data.repository.HabitRepositoryImpl
import com.example.data.repository.StatisticsRepositoryImpl
import com.example.data.repository.TokenRepositoryImpl
import com.example.domain.repository.TokenRepository
import com.example.data.repository.FirestoreUserProfileRepository
import com.example.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        impl: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        impl: StatisticsRepositoryImpl
    ): StatisticsRepository
    
    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: FirestoreUserProfileRepository
    ): UserProfileRepository
}
