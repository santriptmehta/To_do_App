package com.santriptmehta.to_do_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.santriptmehta.to_do_app.CreateToDoActivity.Companion.PREVIOUS_TODO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ToDoInterface{

    lateinit var recyclerView: RecyclerView
    lateinit var toDoAdapter: ToDoAdapter
    lateinit var toDoDatabase: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDoDatabase = Room.databaseBuilder(applicationContext,ToDoDatabase::class.java,ToDoDatabase.DB_NAME).build()


        val list: MutableList<ToDo> = mutableListOf()

        val fab: FloatingActionButton = findViewById(R.id.add_button)
         recyclerView = findViewById(R.id.recyclerView)
        toDoAdapter = ToDoAdapter( list, this)

        recyclerView.adapter = toDoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val intent = Intent(this, CreateToDoActivity::class.java)
            startActivity(intent)
        }

        fetchToDoList()
    }

    private fun fetchToDoList(){
        GlobalScope.launch ( Dispatchers.IO ){
            val todoList = toDoDatabase.toDoAppDao().fetchList()

            launch ( Dispatchers.Main ){
               toDoAdapter.setList(todoList)
            }

        }
    }

    override fun updateTodoText(todo: ToDo) {

    val intent = Intent(this,CreateToDoActivity::class.java)
        intent.putExtra(PREVIOUS_TODO,todo)
        startActivity(intent)
    }

    override fun onDeleteToDo(todo: ToDo, position : Int) {
        GlobalScope.launch (Dispatchers.IO){
            toDoDatabase.toDoAppDao().deleteTodo(todo)

            launch(Dispatchers.Main){
                toDoAdapter.toDoList.remove(todo)
                toDoAdapter.notifyItemRemoved(position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_serch)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchTodos(it)
                }
                return true
            }

        })
        return true
    }

    private fun searchTodos(newText: String){
        GlobalScope.launch(Dispatchers.IO) {
            val list = toDoDatabase.toDoAppDao().fetchList()
            launch(Dispatchers.Main) {
                val filteredList = filter(list, newText)


                toDoAdapter.setList(filteredList)
                recyclerView.scrollToPosition(0 )
            }
        }
    }
    private fun filter(list: List<ToDo>, newText: String): MutableList<ToDo>{

    val lowerCaseText = newText.toLowerCase()
        val filteredList: MutableList<ToDo> = mutableListOf()

        for(item in list){
            val text: String? = item.name?.toLowerCase()
            if (text?.contains(lowerCaseText) == true){
                filteredList.add(item)
            }
        }
        return filteredList
    }

}