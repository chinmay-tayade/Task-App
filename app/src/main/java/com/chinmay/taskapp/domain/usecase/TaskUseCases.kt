package com.chinmay.taskapp.domain.usecase

import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

data class TaskUseCases(
    val getTasksUseCase: GetTasksUseCase,
    val addTaskUseCase: AddTaskUseCase,
    val updateTaskUseCase: UpdateTaskUseCase,
    val deleteTaskUseCase: DeleteTaskUseCase
)

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getAllTasks()
}

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.insertTask(task)
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)
}
