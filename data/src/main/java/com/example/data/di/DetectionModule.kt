package com.example.data.di

import com.example.data.service.TFLiteObjectDetectionService
import com.example.domain.service.ObjectDetectionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DetectionModule {

    @Binds
    @Singleton
    abstract fun bindObjectDetectionService(
        impl: TFLiteObjectDetectionService
    ): ObjectDetectionService
}
