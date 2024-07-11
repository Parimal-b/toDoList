package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_data_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    var id: Int,

    @ColumnInfo(name = "task_name")
    var title: String,

    @ColumnInfo(name = "task_description")
    var description: String
)
