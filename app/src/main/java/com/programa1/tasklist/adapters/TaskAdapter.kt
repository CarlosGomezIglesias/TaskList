package com.programa1.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.databinding.ItemTaskBinding


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
                onClick(position)
            }
            holder.binding.doneCheckBox.setOnCheckedChangeListener { button, bool ->
                //solo accede al codigo si es el usuario el que a dado click
                if (holder.binding.doneCheckBox.isPressed){
                    onClick(position)
                }

            }
            holder.binding.editButton.setOnClickListener {
                onEdit(position)

            }
            holder.binding.deleteButton.setOnClickListener {
                onDelete(position)

            }


        }

        override fun getItemCount(): Int = items.size

        fun updateData(dataSet: List<Task>){
            items = dataSet
            notifyDataSetChanged()
        }
    }

    class TaskViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        fun render(task: Task) {
            binding.doneCheckBox.isChecked=task.done
            binding.titleTextView.text = task.title
        }

    }
