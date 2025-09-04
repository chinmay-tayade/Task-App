package com.chinmay.taskapp.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER(:title) || '%' LIMIT 1")
    suspend fun getTaskByTitle(title: String): TaskEntity?


    @Query("""
        SELECT id FROM tasks
        WHERE LOWER(title) LIKE '%' || LOWER(:title) || '%'
        ORDER BY LENGTH(title) ASC 
        LIMIT 1
    """)
    suspend fun getTaskIdBySimilarTitle(title: String): Long?

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id:Long) : TaskEntity?

}
