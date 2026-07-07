package com.programa1.tasklist.data

data class Category(
    val id : Int,
    var position: Int,
    var name: String
){
    var pendingTasks = 0
    var numberOfTasksDone = 0
    var numberOfTasksTotal = 0

    companion object {
        const val TABLE_NAME="categories"
        const val COLUMN_ID="id"
        const val COLUMN_POSITION="position"
        const val COLUMN_NAME="name"

        const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_POSITION INTEGER,"+
                    "$COLUMN_NAME TEXT)"

        const val SQL_DELETE = "DROP TABLE IF EXISTS $TABLE_NAME"

    }
}


