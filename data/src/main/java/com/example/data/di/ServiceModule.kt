package com.example.data.di

import com.example.data.repository.YandexAuthService
import com.example.data.service.AppMetricaAnalyticsService
import com.example.data.service.FirebasePushService
import com.example.data.service.FirebaseRemoteConfigService
import com.example.data.service.FirebaseCrashReporter
import com.example.data.service.AppMetricaCrashReporter
import com.example.data.service.CompositeCrashReporter
import com.example.domain.service.AnalyticsService
import com.example.domain.service.AuthService
import com.example.domain.service.PushService
import com.example.domain.service.RemoteConfigService
import com.example.domain.service.CrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(
        impl: AppMetricaAnalyticsService
    ): AnalyticsService

    @Binds
    @Singleton
    abstract fun bindAuthService(
        impl: YandexAuthService
    ): AuthService

    @Binds
    @Singleton
    abstract fun bindPushService(
        impl: FirebasePushService
    ): PushService

    @Binds
    @Singleton
    abstract fun bindRemoteConfigService(
        impl: FirebaseRemoteConfigService
    ): RemoteConfigService

    @Binds
    @Singleton
    abstract fun bindCrashReporter(
        impl: CompositeCrashReporter
    ): CrashReporter
}
