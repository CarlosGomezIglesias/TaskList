package com.programa1.tasklist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programa1.tasklist.adapters.utils.TaskDiffUtils
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.databinding.ItemTaskBinding
import java.util.Calendar


class TaskAdapter(
    var items: List<Task>,
    val onClick: (Int) -> Unit,
    val onEdit: (Int) -> Unit,
    val onDelete: (Int) -> Unit,
) : RecyclerView.Adapter<TaskViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
            return TaskViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = items[position]
            holder.render(task)
            holder.itemView.setOnClickListener {
                onClick(holder.absoluteAdapterPosition)
            }
            holder.binding.doneCheckBox.setOnCheckedChangeListener { button, bool ->
                //solo accede al codigo si es el usuario el que a dado click
                if (holder.binding.doneCheckBox.isPressed){
                    onClick(holder.absoluteAdapterPosition)
                }

            }
            holder.binding.editButton.setOnClickListener {
                onEdit(holder.absoluteAdapterPosition)

            }
            holder.binding.deleteButton.setOnClickListener {
                onDelete(holder.absoluteAdapterPosition)

            }



        }

        override fun getItemCount(): Int = items.size

        fun updateData(dataSet: List<Task>){
            val diffUtils = TaskDiffUtils(items, dataSet)
            val diffResult = DiffUtil.calculateDiff(diffUtils)
            items = dataSet
            diffResult.dispatchUpdatesTo(this)
        }
    }

    class TaskViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        fun render(task: Task) {
            binding.doneCheckBox.isChecked=task.done
            binding.titleTextView.text = task.title
            binding.descriptionTextView.text = task.description
            if(task.priority){
                binding.priorityImage.visibility = View.VISIBLE
            }else{
                binding.priorityImage.visibility = View.GONE
            }

            if(task.limitDate != null){

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = task.limitDate!!

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)

                val fechaTexto = "$day/$month/$year"

                binding.limitDateTextView.text = fechaTexto

            }else{
                binding.limitDateTextView.text = "Sin fecha"
            }
        }

    }
