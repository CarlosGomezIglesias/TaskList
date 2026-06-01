package com.programa1.tasklist.activities

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Collections

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

    lateinit var hideMenuItem: Menu

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

        binding.addTask.setOnClickListener {
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, category?.id ?: -1)
            startActivity(intent)
        }

        configureGestures()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_task_list_menu, menu)
        hideMenuItem = menu.findItem(R.id.menu_hide)

        setFavoriteIcon()
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun editTask(position: Int){
        val task= taskList[position]

        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, task.category.id)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        startActivity(intent)

        adapter.notifyItemChanged(position)

    }
    fun showTask(position: Int){
        val task= taskList[position]

        task.done = !task.done
        taskDAO.update(task)

        //Obliga a actualizar el check antes de actualizar los datos
        adapter.notifyItemChanged(position)

        reloadData()
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
    //funcion que añade los gestos de barrer a un lado y otro
    fun configureGestures(){
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.bindingAdapterPosition
                    val toPosition = target.bindingAdapterPosition
                    swapCategoryPositions(fromPosition,toPosition )
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
                }
                //Esto es para personalizar la fila cuando deslizo a derecha o izquierda
                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    val whiteColor = getColor(R.color.white)

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        // Swipe left action
                        .addSwipeLeftLabel("BORRAR")
                        .setSwipeLeftLabelColor(whiteColor)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        //.setSwipeLeftActionIconTint(whiteColor) Sobreescribe el color del icono si tiene
                        .addSwipeLeftBackgroundColor(getColor(R.color.red))

                        // Swipe right action
                        .addSwipeRightLabel("EDITAR")
                        .setSwipeRightLabelColor(whiteColor)
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        //.setSwipeRightActionIconTint(whiteColor) Sobreescribe el color del icono si tiene
                        .addSwipeRightBackgroundColor(getColor(R.color.green))

                        // Build
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }


            }
        )
        gestures.attachToRecyclerView(binding.recyclerView)
    }
    private fun swapCategoryPositions(position1: Int, position2: Int){
        val task1 = taskList[position1]
        val task2 = taskList[position2]
        task1.position = position2
        task2.position = position1
        taskDAO.update(task1)
        taskDAO.update(task2)
        Collections.swap(taskList, position1, position2)


    }
}