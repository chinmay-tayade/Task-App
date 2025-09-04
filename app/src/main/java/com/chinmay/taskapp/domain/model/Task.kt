package com.chinmay.taskapp.domain.model

import com.chinmay.taskapp.data.local.TaskEntity


data class Task(
    val id: Long = 0,
    val title: String,
    val dueDate: Long,
    val status: TaskStatus
)



data class UpdatedPair(
    var old : Task,
    var updated : Task
)

enum class TaskStatus {
    PENDING, COMPLETED
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = 0,
        title = this.title,
        dueDate = this.dueDate,
        status = this.status
    )
}
