package com.programa1.tasklist.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.data.TaskDAO

class DatabaseManager (val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        //necesitamos activar las foreign keys en cada conexion con la base de datos
        db.execSQL("PRAGMA foreign_keys=ON;")
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Category.SQL_CREATE)
        db.execSQL(Task.SQL_CREATE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*onDestroy(db)
        onCreate(db)*/
        var version = oldVersion
        if (version == 1) {
            db.execSQL("ALTER TABLE ${Task.TABLE_NAME} ADD COLUMN ${Task.COLUMN_POSITION} INTEGER DEFAULT 0")
            val categoryDAO = CategoryDAO(context)
            val taskDAO = TaskDAO(context)
            categoryDAO.getAll().forEach { c ->
                val taskList = taskDAO.getAllByCategory(c)
                for (i in taskList.indices) {
                    var t = taskList[i]
                    t.position = i
                    taskDAO.update(t)
                }
            }
            version = 2
        }
        if (version == 2) {

        }
    }
    fun onDestroy (db: SQLiteDatabase) {
        db.execSQL(Task.SQL_DELETE)
        db.execSQL(Category.SQL_DELETE)
    }
    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "TaskList.db"
    }
}