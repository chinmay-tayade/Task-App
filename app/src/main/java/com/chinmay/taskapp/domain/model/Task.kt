package com.chinmay.taskapp.domain.model


import java.util.Date

data class Task(
    val id: Long = 0,
    val title: String,
    val dueDate: Date,
    val status: TaskStatus
)

enum class TaskStatus {
    PENDING, COMPLETED
}
