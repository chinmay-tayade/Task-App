package com.chinmay.taskapp.data.repository

import com.chinmay.taskapp.data.local.TaskDao
import com.chinmay.taskapp.data.local.TaskEntity
import com.chinmay.taskapp.domain.model.Task
import com.chinmay.taskapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date


class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks().map { taskEntities ->
            taskEntities.map { Task(it.id, it.title, Date(it.dueDate), it.status) }
        }
    }

    override suspend fun insertTask(task: Task) {
        dao.insertTask(TaskEntity(task.id, task.title, task.dueDate.time, task.status))
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(TaskEntity(task.id, task.title, task.dueDate.time, task.status))
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(TaskEntity(task.id, task.title, task.dueDate.time, task.status))
    }
}
