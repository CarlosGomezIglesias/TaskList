package com.programa1.tasklist.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.Task

class DatabaseManager (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys=ON")
        db.execSQL(Category.SQL_CREATE)
        db.execSQL(Task.SQL_CREATE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDestroy(db)
        onCreate(db)

    }
    fun onDestroy (db: SQLiteDatabase) {
        db.execSQL(Task.SQL_DELETE)
        db.execSQL(Category.SQL_DELETE)
    }
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TaskList.db"
    }
}