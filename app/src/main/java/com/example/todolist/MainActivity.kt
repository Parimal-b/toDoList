package com.example.todolist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.databinding.DialogLayoutBinding
import com.example.todolist.db.Priority
import com.example.todolist.db.Task
import com.example.todolist.db.TaskDatabase
import com.example.todolist.db.TaskRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showNewTaskDialog(Task(0, "", "", false, Priority.MEDIUM, ""))
        }

    }

    private fun initRecyclerView(){
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        displayTaskLists()
    }

    private fun displayTaskLists(){
        taskViewModel.tasks.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            binding.tasksRecyclerView.adapter = MyRecyclerViewAdapter(it, {selectedItem: Task -> listItemClicked(selectedItem)}, {selectedItem: Task -> checkBoxItemClicked(selectedItem)}, {selectedItem: Task -> deleteTask(selectedItem)},
                {selectedItem: Task -> updatePriority(selectedItem)})
        })
    }

    private fun listItemClicked(task: Task){
        taskViewModel.initUpdateAndDelete(task)
        showDialog(task)
    }

    private fun checkBoxItemClicked(task: Task){
        taskViewModel.update(task)
    }

    private fun deleteTask(task: Task){
        taskViewModel.delete(task)
    }

    private fun updatePriority(task: Task){
        taskViewModel.update(task)
    }

    private fun showNewTaskDialog(task: Task) {
        val dialogBinding: DialogLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_layout,
            null,
            false
        )
        dialogBinding.myViewModel = taskViewModel
        dialogBinding.lifecycleOwner = this

        taskViewModel.initResetAllEditTexts(task)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("Add Task")

        dialogBuilder.create().show()
    }

    private fun showDialog(task: Task) {
        val dialogBinding: DialogLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_layout,
            null,
            false
        )
        dialogBinding.myViewModel = taskViewModel
        dialogBinding.lifecycleOwner = this

        taskViewModel.initUpdateAndDelete(task)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("Add Task")

        dialogBuilder.create().show()
    }

}