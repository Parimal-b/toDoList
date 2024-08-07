package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.db.Priority
import com.example.todolist.db.Task
import com.example.todolist.db.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TaskRepository): ViewModel() {

    val tasks = repository.tasks

    val inputTaskTitle = MutableLiveData<String>()
    val inputTaskDescription = MutableLiveData<String>()
    val isChecked = MutableLiveData<Boolean>()
    val inputTaskCategory = MutableLiveData<String>()


    val saveOrUpdateButton = MutableLiveData<String>()
    val clearAllOrDeleteButton = MutableLiveData<String>()

    private var isUpdateOrDelete = false
    private lateinit var taskToUpdateOrDelete: Task

    private val statusMessage = MutableLiveData<Event<String>>()

    private val _dismissDialog = MutableLiveData<Boolean>()
    val dismissDialog: LiveData<Boolean>get() = _dismissDialog


    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButton.value = "Save"
        clearAllOrDeleteButton.value = "Clear All"
    }

    fun saveOrUpdate() {
        if (inputTaskTitle.value == null) {
            statusMessage.value = Event("Please Enter the Title for your Task")
        } else if (inputTaskDescription.value == null) {
            statusMessage.value = Event("Please Enter the Description for your Task")
        } else {
            if (isUpdateOrDelete) {
                taskToUpdateOrDelete.title = inputTaskTitle.value!!
                taskToUpdateOrDelete.description = inputTaskDescription.value!!
                taskToUpdateOrDelete.category = inputTaskCategory.value!!
                update(taskToUpdateOrDelete)
            } else {
                val title = inputTaskTitle.value!!
                val description = inputTaskDescription.value!!
                val category = inputTaskCategory.value!!

                insert(Task(0, title, description, false, priority = Priority.MEDIUM, category))

                inputTaskTitle.value = ""
                inputTaskDescription.value = ""
                inputTaskCategory.value = ""
            }
            _dismissDialog.value = true
        }
    }


    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(taskToUpdateOrDelete)
        } else {
            deleteAll()
        }
    }

    fun insert(task: Task) {
        viewModelScope.launch {
            val newRowId = repository.insert(task)
            withContext(Dispatchers.Main) {
                if (newRowId > -1) {
                    statusMessage.value = Event("Task Added Successfully")
                } else {
                    statusMessage.value = Event("Oops!! There is some error in the operation")
                }
            }
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            repository.update(task)
            withContext(Dispatchers.Main) {

            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            withContext(Dispatchers.Main) {
                statusMessage.value = Event("Task deleted Successfully")
                inputTaskTitle.value = ""
                inputTaskDescription.value = ""
                inputTaskCategory.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButton.value = "Save"
                clearAllOrDeleteButton.value = "Clear All"
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            withContext(Dispatchers.Main) {
                statusMessage.value = Event("All tasks deleted Successfully")
            }
        }
    }

    fun initUpdateAndDelete(task: Task) {
        inputTaskTitle.value = task.title
        inputTaskDescription.value = task.description
        inputTaskCategory.value = task.category
        isUpdateOrDelete = true
        _dismissDialog.value = false
        taskToUpdateOrDelete = task
        saveOrUpdateButton.value = "Update"
        clearAllOrDeleteButton.value = "Delete"
    }

    fun initResetAllEditTexts(task: Task){
        inputTaskTitle.value = ""
        inputTaskDescription.value = ""
        inputTaskCategory.value = ""
        _dismissDialog.value = false
        isUpdateOrDelete = false
        taskToUpdateOrDelete = task
        saveOrUpdateButton.value = "Save"
        clearAllOrDeleteButton.value = "Delete"
    }

    fun onCheckedChanged(isChecked: Boolean) {
        this.isChecked.value = isChecked
        if (isUpdateOrDelete) {
            taskToUpdateOrDelete.isCompleted = isChecked
            viewModelScope.launch {
                repository.update(taskToUpdateOrDelete)
            }
        }
    }

}
