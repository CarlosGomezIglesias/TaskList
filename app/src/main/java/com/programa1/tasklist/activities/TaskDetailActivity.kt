package com.programa1.tasklist.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.programa1.tasklist.R
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.data.TaskDAO
import com.programa1.tasklist.databinding.ActivityTaskDetailBinding

class TaskDetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    lateinit var binding: ActivityTaskDetailBinding
    lateinit var task: Task
    var category: Category? = null

    lateinit var categoryDAO: CategoryDAO

    lateinit var taskDAO: TaskDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_task_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        categoryDAO=CategoryDAO(this)
        taskDAO= TaskDAO(this)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)

        category=categoryDAO.getById(categoryId)
        if(taskId!=-1){
            task= taskDAO.getById(taskId)!!
            supportActionBar?.title="Editar tarea"
        }else{
            task= Task(-1,"", false, category!!)
            supportActionBar?.title="Crear tarea"
        }

        binding.titleTextField.editText!!.setText(task.title)

        binding.saveButton.setOnClickListener {
            task.title=binding.titleTextField.editText!!.text.toString()
            taskDAO.save(task)
            Snackbar.make(binding.root, "Tarea Guardada", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }
}