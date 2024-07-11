package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.db.Task
import com.example.todolist.db.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TaskRepository): ViewModel() {

    val tasks = repository.tasks

    val inputTaskTitle = MutableLiveData<String>()
    val inputTaskDescription = MutableLiveData<String>()

    val saveOrUpdateButton = MutableLiveData<String>()
    val clearAllOrDeleteButton = MutableLiveData<String>()

    private var isUpdateOrDelete = false
    private lateinit var taskToUpdateOrDelete : Task

    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButton.value = "Save"
        clearAllOrDeleteButton.value = "Clear All"
    }

    fun saveOrUpdate(){

        if (inputTaskTitle.value == null){
            statusMessage.value = Event("Please Enter the Title for your Task")
        }else if (inputTaskDescription.value == null){
            statusMessage.value = Event("Please Enter the Description for your Task")
        }
        if (isUpdateOrDelete){
            taskToUpdateOrDelete.title = inputTaskTitle.value!!
            taskToUpdateOrDelete.description = inputTaskDescription.value!!
            update(taskToUpdateOrDelete)
        }else{
            val title = inputTaskTitle.value!!
            val description = inputTaskDescription.value!!

            insert(Task(0, title, description))

            inputTaskTitle.value = ""
            inputTaskDescription.value = ""
        }

    }

    fun clearAllOrDelete(){
        if (isUpdateOrDelete){
            delete(taskToUpdateOrDelete)
        }else{
            deleteAll()
        }
    }

    fun insert(task: Task){
        viewModelScope.launch {
            val newRowId = repository.insert(task)
            withContext(Dispatchers.Main){
                if (newRowId > -1){
                    statusMessage.value = Event("Task Added Successfully")
                }else{
                    statusMessage.value = Event("Oops!! There is some error in the operation")
                }

            }
        }
    }

    fun update(task: Task){
        viewModelScope.launch {
            repository.update(task)
            withContext(Dispatchers.Main){
                statusMessage.value = Event("Task updated Successfully")
            }
        }
    }

    fun delete(task: Task){
        viewModelScope.launch {
            repository.delete(task)
            withContext(Dispatchers.Main){
                statusMessage.value = Event("Task deleted Successfully")
            }
            withContext(Dispatchers.Main){
                inputTaskTitle.value = ""
                inputTaskDescription.value = ""
                isUpdateOrDelete = false
                taskToUpdateOrDelete = task
                saveOrUpdateButton.value = "Save"
                clearAllOrDeleteButton.value = "Clear All"
            }
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            repository.deleteAll()
            withContext(Dispatchers.Main){
                statusMessage.value = Event("All tasks deleted Successfully")
            }
        }
    }

    fun initUpdateAndDelete(task: Task){
        inputTaskTitle.value = task.title
        inputTaskDescription.value = task.description
        isUpdateOrDelete = true
        taskToUpdateOrDelete = task
        saveOrUpdateButton.value = "Update"
        clearAllOrDeleteButton.value = "Delete"
    }
}