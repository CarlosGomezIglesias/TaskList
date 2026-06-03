package com.programa1.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getLongOrNull
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
        values.put(Task.COLUMN_DESCRIPTION, task.description)
        values.put(Task.COLUMN_DONE, task.done)
        values.put(Task.COLUMN_PRIORITY, task.priority)
        values.put(Task.COLUMN_LIMIT_DATE, task.limitDate)
        values.put(Task.COLUMN_POSITION, task.position)
        values.put(Task.COLUMN_NOTIFICATION, task.notification)
        values.put(Task.COLUMN_CATEGORY_ID, task.category.id)
        return values
    }

    fun cursorToEntity(cursor: Cursor): Task{
            val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_DESCRIPTION))
            val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_DONE)) !=0
            val priority = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_PRIORITY)) !=0
            val limitDate = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(Task.COLUMN_LIMIT_DATE))
            val position = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_POSITION))
            val notification = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NOTIFICATION)) !=0
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_CATEGORY_ID))
            val category = CategoryDAO(context).getById(categoryId)!!
            return Task(itemId, title,description, done, limitDate,priority,position, notification,category)
    }

    fun insert(task: Task) {

        open()

        val values = getContentValues(task)

        try {
            val newRowId = db.insert(Task.TABLE_NAME, null, values)
            task.id = newRowId.toInt()
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

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun delete(task: Task) {
        open()

        try {
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_ID}=${task.id}", null)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }


    }

    fun deleteAll() {
        open()

        try {
            // Issue SQL statement.
            val deletedRows = db.delete(Task.TABLE_NAME, null, null)
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

    fun getAllBy(where: String?) : List<Task> {
        open()

        val resultList: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Task.COLUMN_POSITION               // The sort order
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

    fun getAllByCategory(category: Category): List<Task> {
        return getAllBy("${Task.COLUMN_CATEGORY_ID} = ${category.id}")
    }

    fun getAll(): List<Task> {
        return getAllBy(null)
    }

    fun countByCategory(category: Category): Int {
        return countBy("${Task.COLUMN_CATEGORY_ID} = ${category.id}")
    }

    fun countByCategoryAndDone(category: Category, done: Boolean): Int {
        return countBy("${Task.COLUMN_CATEGORY_ID} = ${category.id} AND ${Task.COLUMN_DONE} = $done")
    }

    fun countBy(where: String): Int {
        open()

        var count = 0

        try {
            val cursor = db.query(
                Task.TABLE_NAME,
                arrayOf("COUNT(*)"),
                where,
                null,
                null,
                null,
                null
            )

            if (cursor.moveToNext()) {
                count = cursor.getInt(0)

            }
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return count
    }
}