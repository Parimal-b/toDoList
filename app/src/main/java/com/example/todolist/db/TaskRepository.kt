package com.example.todolist.db

class TaskRepository(private val dao: TaskDao) {

    val tasks = dao.getAllTasks()

    suspend fun insert(task: Task): Long{
        return dao.insertTask(task)
    }

    suspend fun update(task: Task){
        dao.updateTask(task)
    }

    suspend fun delete(task: Task){
        dao.deleteTask(task)
    }

    suspend fun deleteAll(){
        dao.deleteAll()
    }
}