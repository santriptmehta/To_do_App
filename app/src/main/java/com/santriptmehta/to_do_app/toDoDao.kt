package com.santriptmehta.to_do_app

import androidx.room.*

//Dao -> data access object
// CUrd - create read update delete

@Dao
interface TooDoDao {

    @Insert
    suspend fun insertTodo(todo : ToDo ): Long

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME)
    suspend fun fetchList(): MutableList<ToDo>

    @Update
    suspend fun updateTodo(todo: ToDo)

    @Delete
    suspend fun deleteTodo(todo: ToDo)
}