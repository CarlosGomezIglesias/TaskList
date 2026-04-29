package com.programa1.tasklist.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programa1.tasklist.R
import com.programa1.tasklist.adapters.CategoryAdapter
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.databinding.ActivityMainBinding
import com.programa1.tasklist.databinding.DialogCreateCategoryBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: CategoryAdapter

    var categoryList: List<Category> = emptyList()

    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

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

        categoryList = categoryDAO.getAll()

        adapter = CategoryAdapter(categoryList, ::showCategory, ::editCategory, ::deleteCategory)

        binding.recyclerView.adapter = adapter

        binding.addCategoryFAB.setOnClickListener {
            //Navegar al activity de crear, o mostrar una alerta de crear
            showCategoryDialog(Category(-1, ""))
        }

    }

    fun showCategoryDialog(category: Category) {
        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        val isEditing = category.id != -1

        var title = "Crear categoria"

        if (isEditing) {
            title = "Editar categoria"

        }
        dialogBinding.textField.editText!!.setText(category.name)

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_add_category)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("No", null)
            .setCancelable(false)
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = dialogBinding.textField.editText!!.text.toString().trim()
            if (name.isEmpty()) {
                dialogBinding.textField.error = "El nombre no puede estar vacio"
            } else {
                dialogBinding.textField.error = null

                category.name = name
                categoryDAO.save(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)

                dialog.dismiss()

            }

        }
    }

    fun showCategory(position: Int) {

        val category = categoryList[position]
        Toast.makeText(this, category.name, Toast.LENGTH_SHORT).show()

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
            .setTitle("Borrar categoria")
            .setMessage("¿Esta seguro que quiere borrar la categoria \"${category.name}\"?")
            .setPositiveButton("Si") { dialog, which ->
                categoryDAO.delete(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
            }
            .setNegativeButton("No") { dialog, which ->

            }
            .setCancelable(false)
            .create()
        dialog.show()


    }

}