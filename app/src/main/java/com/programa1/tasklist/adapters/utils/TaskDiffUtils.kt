package com.programa1.tasklist.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import com.programa1.tasklist.data.Task

class TaskDiffUtils(val oldList: List<Task>, val newList: List<Task>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return  when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].title != newList[newItemPosition].title -> false
            oldList[oldItemPosition].description != newList[newItemPosition].description -> false
            oldList[oldItemPosition].priority != newList[newItemPosition].priority -> false
            oldList[oldItemPosition].done != newList[newItemPosition].done -> false
            oldList[oldItemPosition].limitDate != newList[newItemPosition].limitDate -> false
            oldList[oldItemPosition].notification != newList[newItemPosition].notification -> false
            oldList[oldItemPosition].category.id != newList[newItemPosition].category.id -> false
            else -> true
        }
    }
}