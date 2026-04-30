package com.programa1.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.programa1.tasklist.utils.DatabaseManager

class TaskDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun save(task: Task){
        if (task.id!=-1){
            update(task)
        }else {
            insert(task)
        }
    }

    fun getContentValues(task: Task): ContentValues{

        val values = ContentValues()
        values.put(Task.COLUMN_TITLE, task.title)
        values.put(Task.COLUMN_DONE, task.done)
        values.put(Task.COLUMN_CATEGORY_ID, task.category.id)
        return values
    }

    fun cursorToEntity(cursor: Cursor): Task{
            val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_TITLE))
            val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_DONE)) !=0
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_CATEGORY_ID))
            val category = CategoryDAO(context).getById(categoryId)!!
            return Task(itemId, title, done, category)
    }

    fun insert(task: Task) {

        open()

        val values = getContentValues(task)

        try {
            val newRowId = db.insert(Task.TABLE_NAME, null, values)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }


    }

    fun update(task: Task) {
        open()
        val values = getContentValues(task)

        try {
            val updateRows = db.update(Task.TABLE_NAME, values,"${Task.COLUMN_ID}=${task.id}", null)
                db.update(Task.TABLE_NAME, values, "${Task.COLUMN_ID}=${task.id}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun delete(task: Task) {
        open()

        try {
            val deletedRows =
                db.delete(Task.TABLE_NAME, "${Task.COLUMN_ID}=${task.id}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }


    }

    fun getById(id: Int): Task? {
        open()

        var result: Task? = null

        try {
            val cursor = db.query(
                Task.TABLE_NAME,
                null,
                "${Task.COLUMN_ID} = $id",
                null,
                null,
                null,
                null
            )

            if (cursor.moveToNext()) {
                result = cursorToEntity(cursor)

            }
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return result
    }

    fun getAllByCategory(category: Category): List<Task> {
        open()

        val resultList: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,
                null,
                "${Task.COLUMN_CATEGORY_ID}=${category.id}",
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                resultList.add(task)

            }
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return resultList
    }

    fun getAll(): List<Task> {
        open()

        val resultList: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                resultList.add(task)

            }
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return resultList
    }

}