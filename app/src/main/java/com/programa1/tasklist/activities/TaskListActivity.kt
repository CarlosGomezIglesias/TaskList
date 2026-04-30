package com.programa1.tasklist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programa1.tasklist.R
import com.programa1.tasklist.adapters.TaskAdapter
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.data.TaskDAO
import com.programa1.tasklist.databinding.ActivityTaskListBinding

class TaskListActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    lateinit var binding: ActivityTaskListBinding

    lateinit var categoryDAO: CategoryDAO

    lateinit var taskDAO: TaskDAO

    lateinit var adapter: TaskAdapter

    var taskList: List<Task> = emptyList()

    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO=CategoryDAO(this)
        taskDAO= TaskDAO(this)

        val categoryId=intent.getIntExtra(EXTRA_CATEGORY_ID, -1)
        category=categoryDAO.getById(categoryId)

        //crear tarea de prueba
        val task1= Task(-1, "comprar ron", false, category!!)
        val task2= Task(-1, "comprar flores", false, category!!)
        taskDAO.insert(task1)
        taskDAO.insert(task2)
        //fin codigo pruebas

        category?.let {
            taskList=taskDAO.getAllByCategory(it)
        }

        adapter= TaskAdapter(taskList,::showTask, ::editTask, ::deleteTask)
        binding.recyclerView.adapter=adapter
    }

    fun editTask(position: Int){
        val task= taskList[position]
    }

    fun showTask(position: Int){
        val task= taskList[position]

        task.done = !task.done
        taskDAO.update(task)

        taskList = taskDAO.getAllByCategory(category!!)
        adapter.updateData(taskList)
    }

    fun deleteTask(position: Int){
        val task= taskList[position]

        //Patron Builder (evita tener que llamar al objeto cada vez que se hace .setX)
        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar tarea")
            .setMessage("¿Esta seguro que quiere borrar la tarea \"${task.title}\"?")
            .setPositiveButton("Si") { dialog, which ->
                taskDAO.delete(task)
                taskList = taskDAO.getAllByCategory(category!!)
                adapter.updateData(taskList)
            }
            .setNegativeButton("No") { dialog, which ->

            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

}