package com.example.todolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ListItemBinding
import com.example.todolist.db.Priority
import com.example.todolist.db.Task

class MyRecyclerViewAdapter(
    private val tasks: List<Task>,
    private val clickListener: (Task) -> Unit,
    private val textBoxClickListener: (Task) -> Unit,
    private val deleteTask: (Task) -> Unit,
    private val updatePriority: (Task) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(tasks[position], clickListener, textBoxClickListener, deleteTask, updatePriority)
    }
}

class MyViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private var currentPriorityPosition = -1 // Initialize with an invalid position

    fun bind(
        task: Task,
        clickListener: (Task) -> Unit,
        textBoxClickListener: (Task) -> Unit,
        deleteTask: (Task) -> Unit,
        updatePriority: (Task) -> Unit
    ) {
        // Set initial state
        updateTaskView(task)

        binding.listItemLayout.setOnClickListener {
            clickListener(task)
        }

        binding.taskCheckbox.setOnClickListener {
            task.isCompleted = !task.isCompleted
            updateTaskView(task)
            textBoxClickListener(task)
        }

        binding.deleteButton.setOnClickListener {
            deleteTask(task)
        }

        // Set up onItemSelected listener only if necessary
        if (currentPriorityPosition != getPriorityPosition(task.priority)) {
            currentPriorityPosition = getPriorityPosition(task.priority)
            binding.prioritySpinner.setSelection(currentPriorityPosition)
        }

        binding.prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPriority = when (position) {
                    0 -> Priority.HIGH
                    1 -> Priority.MEDIUM
                    else -> Priority.LOW
                }
                if (task.priority != selectedPriority) {
                    task.priority = selectedPriority
                    updatePriority(task)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.executePendingBindings()
    }

    private fun updateTaskView(task: Task) {
        binding.titleTextView.text = task.title
        binding.edescriptionTextView.text = task.description
        binding.taskCheckbox.isChecked = task.isCompleted
        binding.taskCategory.text = task.category
        binding.prioritySpinner.setSelection(getPriorityPosition(task.priority))

        if (task.isCompleted) {
            setTextViewStrikeThrough(binding.titleTextView, true)
            setTextViewStrikeThrough(binding.edescriptionTextView, true)
        } else {
            setTextViewStrikeThrough(binding.titleTextView, false)
            setTextViewStrikeThrough(binding.edescriptionTextView, false)
        }
    }

    private fun getPriorityPosition(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

    private fun setTextViewStrikeThrough(textView: TextView, strikeThrough: Boolean) {
        if (strikeThrough) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.white))
        }
    }
}