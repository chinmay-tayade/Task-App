package com.chinmay.taskapp.di

import android.content.Context
import com.chinmay.taskapp.data.local.TaskDao
import com.chinmay.taskapp.data.local.TaskDatabase
import com.chinmay.taskapp.data.remote.GeminiApiService
import com.chinmay.taskapp.data.remote.ApiClient
import com.chinmay.taskapp.data.repository.GeminiRepository
import com.chinmay.taskapp.data.repository.TaskRepositoryImpl
import com.chinmay.taskapp.domain.repository.TaskRepository
import com.chinmay.taskapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(repository: TaskRepository): TaskUseCases {
        return TaskUseCases(
            getTasksUseCase = GetTasksUseCase(repository),
            addTaskUseCase = AddTaskUseCase(repository),
            updateTaskUseCase = UpdateTaskUseCase(repository),
            deleteTaskUseCase = DeleteTaskUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService {
        return ApiClient.createService(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(apiService: GeminiApiService): GeminiRepository {
        return GeminiRepository(apiService)
    }
}
