package com.chinmay.taskapp.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chinmay.taskapp.domain.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val dueDate: Long,
    val status: TaskStatus
)
