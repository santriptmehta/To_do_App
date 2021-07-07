package com.santriptmehta.to_do_app

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateToDoActivity : AppCompatActivity() {
    lateinit var toDoDatabase: ToDoDatabase
    lateinit var editText: EditText

    var isBeingUpdated =  false
    var previousTodo: ToDo? = null

    companion object{
        const val PREVIOUS_TODO = "PreviousTodo"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_to_do)

        if(intent.hasExtra(PREVIOUS_TODO)){
            isBeingUpdated = true
            previousTodo = intent.extras?.get(PREVIOUS_TODO) as ToDo
        }
        toDoDatabase = Room.databaseBuilder(applicationContext,ToDoDatabase::class.java,ToDoDatabase.DB_NAME).build()

        editText = findViewById(R.id.to_do_edittext)
        val saveButton : Button = findViewById(R.id.save_button)

        if(isBeingUpdated){
            editText.setText(previousTodo?.name.toString())
        }

        saveButton.setOnClickListener {
            val enteredText = editText.text.toString()

            if(TextUtils.isEmpty(enteredText)){
                Toast.makeText(this,"Text blank",Toast.LENGTH_LONG).show()
              return@setOnClickListener
            }

            if(isBeingUpdated){
                previousTodo?.let {
                    previousTodo?.name = enteredText
                    updateRow(it)

                }
            }else {

                val todo = ToDo()
                todo.name = enteredText
                insertRow(todo)
            }

        }
    }

    private fun insertRow(todo: ToDo){
        GlobalScope.launch(Dispatchers.IO) {
            //will be inserting data in background
           val id =  toDoDatabase.toDoAppDao().insertTodo(todo)
            println("Insert thread ${Thread.currentThread().name}")

            launch (Dispatchers.Main){
                todo.todoID = id

                startMainActivity()

            }
        }
    }

    private fun updateRow(todo: ToDo ){
        GlobalScope.launch(Dispatchers.IO){
            toDoDatabase.toDoAppDao().updateTodo(todo)

            launch (Dispatchers.Main){
                startMainActivity()
            }


        }
    }

    private fun startMainActivity(){
        val intent = Intent(this@CreateToDoActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {


        val endteredText = editText.text.toString()

        if (TextUtils.isEmpty(endteredText).not()) {
            if (isBeingUpdated) {
                previousTodo?.let {
                    it.name = editText.text.toString()
                    updateRow(it)
                }
            } else {
                var todo = ToDo()
                todo.name = editText.text.toString()
                insertRow(todo)
            }
        }
        super.onBackPressed()
    }
}