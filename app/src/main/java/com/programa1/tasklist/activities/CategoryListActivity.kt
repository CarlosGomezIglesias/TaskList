package com.programa1.tasklist.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programa1.tasklist.R
import com.programa1.tasklist.adapters.CategoryAdapter
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.databinding.ActivityCategoryListBinding
import com.programa1.tasklist.databinding.DialogCreateCategoryBinding

class CategoryListActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryListBinding
    lateinit var adapter: CategoryAdapter
    var categoryList: List<Category> = emptyList()
    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)

        /*for (i in 1..10) {
            val category = Category(-1, "Category $i")
            categoryDAO.insert(category)
        }*/

        //categoryList = categoryDAO.getAll() // Hacemos la select en el onResume

        /*var taskDAO = TaskDAO(this)
        categoryList.forEach { c ->
            var taskList = taskDAO.getAllByCategory(c)
            for (i in taskList.indices) {
                var t = taskList[i]
                t.position = i
                taskDAO.update(t)
            }
        }*/

        adapter = CategoryAdapter(categoryList, ::showCategory, ::editCategory, ::deleteCategory)

        binding.recyclerView.adapter = adapter

        binding.addCategoryFAB.setOnClickListener {
            //Navegar a una alerta de crear
            showCategoryDialog(Category(-1, ""))
        }

    }

    override fun onResume() {
        super.onResume()
        categoryList = categoryDAO.getAll()
        adapter.updateData(categoryList)
    }

    fun showCategoryDialog(category: Category) {

        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        val isEditing = category.id != -1

        var title = getString(R.string.alertDialog_title_createCategory)

        var icon = R.drawable.ic_add_category

        //Al pulsar en crear o editar categoria distingue cual es la opcion elegida
        if (isEditing) {
            title = getString(R.string.alertDialog_title_editCategory)
            icon = R.drawable.ic_edit
        }
        dialogBinding.textField.editText!!.setText(category.name)
        dialogBinding.textField.editText!!.addTextChangedListener { //Si esta el nombre en blanco avisa
            if (dialogBinding.textField.editText!!.text.trim().isEmpty()) {
                dialogBinding.textField.error = getString(R.string.error_texField)
            } else {
                dialogBinding.textField.error = null
            }
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(icon)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.alertDialog_positiveButton_create_edit), null)
            .setNegativeButton(getString(R.string.alertDialog_negativeButton), null)
            .setCancelable(false)
            .create()
        dialog.show()

        //Hacer que se desactive el boton de guardar hasta que haya texto valido
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Estado inicial
        val editText = dialogBinding.textField.editText!!
        positiveButton.isEnabled = editText.text.toString().trim().isNotEmpty()

        // Escuchar cambios en el texto
        editText.addTextChangedListener {
            val text = it.toString().trim()
            if (text.isEmpty()) {
                dialogBinding.textField.error = getString(R.string.error_texField)
                positiveButton.isEnabled = false
            } else {
                dialogBinding.textField.error = null
                positiveButton.isEnabled = true
            }
            positiveButton.setOnClickListener {
                val name = editText.text.toString().trim()
                category.name = name
                categoryDAO.save(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
                dialog.dismiss()
            }


            //Personaliza el boton positivo
            /*dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.textField.editText!!.text.toString().trim()
                if (name.isNotEmpty()) {
                    category.name = name
                    categoryDAO.save(category)
                    categoryList = categoryDAO.getAll()
                    adapter.updateData(categoryList)
                    dialog.dismiss()
                }*/

        }
    }

    fun showCategory(position: Int) {

        val category = categoryList[position]
        val intent= Intent(this, TaskListActivity::class.java)
        intent.putExtra(TaskListActivity.EXTRA_CATEGORY_ID, category.id)
        startActivity(intent)

    }

    fun editCategory(position: Int) {
        val category = categoryList[position]
        showCategoryDialog(category)
    }

    fun deleteCategory(position: Int) {
        val category = categoryList[position]

        //Patron Builder (evita tener que llamar al objeto cada vez que se hace .setX)
        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle(getString(R.string.alertDialog_title_deleteCategory))
            .setMessage(getString(R.string.alertDialog_message_deleteCategory, category.name))
            .setPositiveButton(getString(R.string.alertDialog_positiveButton_delete)) { dialog, which ->
                categoryDAO.delete(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
            }
            .setNegativeButton(getString(R.string.alertDialog_negativeButton)) { dialog, which ->

            }
            .setCancelable(false)
            .create()
        dialog.show()


    }

}