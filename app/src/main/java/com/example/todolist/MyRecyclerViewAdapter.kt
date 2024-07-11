package com.example.todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ListItemBinding
import com.example.todolist.db.Task

class MyRecyclerViewAdapter(private val tasks: List<Task>, private val clickListener: (Task)-> Unit) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return  MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(tasks[position], clickListener)
    }

}

class MyViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(task: Task, clickListener: (Task)-> Unit){
        binding.titleTextView.text = task.title
        binding.edescriptionTextView.text = task.description
        binding.listItemLayout.setOnClickListener{
            clickListener(task)
        }
    }
}