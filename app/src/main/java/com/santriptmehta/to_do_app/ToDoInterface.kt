package com.santriptmehta.to_do_app

interface ToDoInterface{
    fun updateTodoText(todo: ToDo)
    fun onDeleteToDo(todo: ToDo , position : Int )
}

