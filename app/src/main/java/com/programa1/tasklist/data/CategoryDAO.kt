package com.programa1.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.programa1.tasklist.utils.DatabaseManager

class CategoryDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun insert(category: Category) {

        open()

        val values = ContentValues()
        values.put(Category.COLUMN_NAME, category.name)

        try {
            val newRowId = db.insert(Category.TABLE_NAME, null, values)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }


    }

    fun update(category: Category) {
        open()
        val values = ContentValues()

        values.put(Category.COLUMN_NAME, category.name)

        try {
            val updateRows =
                db.update(Category.TABLE_NAME, values, "${Category.COLUMN_ID}=${category.id}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun delete(category: Category) {
        open()

        try {
            val deletedRows =
                db.delete(Category.TABLE_NAME, "${Category.COLUMN_ID}=${category.id}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }


    }

    fun getById(id: Int): Category? {
        open()

        var result: Category? = null

        try {
            val cursor = db.query(
                Category.TABLE_NAME,
                null,
                "${Category.COLUMN_ID} = $id",
                null,
                null,
                null,
                null
            )

            if (cursor.moveToNext()) {
                val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME))
                val category = Category(itemId, title)
                result= Category(itemId, title)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return result
    }

    fun getAll(): List<Category> {
        open()

        val resultList: MutableList <Category> = mutableListOf()

        try {
            val cursor = db.query(
                Category.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME))
                val category = Category(itemId, title)
                resultList.add(category)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
     return resultList
    }

}