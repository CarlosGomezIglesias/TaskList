package com.programa1.tasklist.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryDAO=CategoryDAO(this)
        taskDAO= TaskDAO(this)

        val categoryId=intent.getIntExtra(EXTRA_CATEGORY_ID, -1)
        category=categoryDAO.getById(categoryId)

        /*crear tarea de prueba
        val task1= Task(-1, "comprar ron", false, category!!)
        val task2= Task(-1, "comprar flores", false, category!!)
        taskDAO.insert(task1)
        taskDAO.insert(task2)
        fin codigo pruebas*/

        supportActionBar?.title=category?.name

        adapter= TaskAdapter(taskList,::showTask, ::editTask, ::deleteTask)
        binding.recyclerView.adapter=adapter

        configureGestures()


    }

    fun editTask(position: Int){
        val task= taskList[position]

        adapter.notifyItemChanged(position)

        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, task.category.id)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        startActivity(intent)
    }

    fun showTask(position: Int){
        val task= taskList[position]

        task.done = !task.done
        taskDAO.update(task)

        adapter.notifyItemChanged(position) //Obliga a actualizar el check antes de actualizar los datos

        reloadData()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.)
        super.onOptionsItemSelected(item)
    }
    fun reloadData(){
        category?.let {
            taskList=taskDAO.getAllByCategory(it)
        }
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
                reloadData()
            }
            .setNegativeButton("No") { dialog, which ->
                adapter.notifyItemChanged(position)
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    fun configureGestures(){
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                    return false
                }

                override fun onSwiped(
                   viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if (direction== ItemTouchHelper.LEFT){
                        deleteTask(viewHolder.absoluteAdapterPosition)
                    }else {
                        editTask(viewHolder.absoluteAdapterPosition)
                    }
                    adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                }

            }
        )
        gestures.attachToRecyclerView(binding.recyclerView)
    }
}