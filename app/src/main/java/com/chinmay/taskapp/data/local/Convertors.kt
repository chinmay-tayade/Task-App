package com.chinmay.taskapp.data.local


import androidx.room.TypeConverter
import com.chinmay.taskapp.domain.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun statusToString(status: TaskStatus): String = status.name
}
