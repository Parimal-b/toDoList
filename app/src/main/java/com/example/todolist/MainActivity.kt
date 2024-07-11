package com.example.todolist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.db.Task
import com.example.todolist.db.TaskDatabase
import com.example.todolist.db.TaskRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = TaskDatabase.getInstance(application).taskDAO
        val repository = TaskRepository(dao)
        val factory = TaskViewModelFactory(repository)

        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        binding.myViewModel = taskViewModel

        binding.lifecycleOwner = this

        initRecyclerView()

        taskViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initRecyclerView(){
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        displayTaskLists()
    }

    private fun displayTaskLists(){
        taskViewModel.tasks.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            binding.tasksRecyclerView.adapter = MyRecyclerViewAdapter(it, {selectedItem: Task -> listItemClicked(selectedItem)})
        })
    }

    private fun listItemClicked(task: Task){
        taskViewModel.initUpdateAndDelete(task)
    }
}