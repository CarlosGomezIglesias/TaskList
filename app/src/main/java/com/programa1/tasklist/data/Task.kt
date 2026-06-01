package com.programa1.tasklist.data

data class Task (
    val id: Int,
    var title: String,
    var description: String,
    var done: Boolean,
    var limitDate: Long?,
    var priority: Boolean,
    var position: Int,
    val category: Category
){
    companion object {
        const val TABLE_NAME="task"
        const val COLUMN_ID="id"
        const val COLUMN_TITLE="title"
        const val COLUMN_DONE="done"
        const val COLUMN_LIMIT_DATE="limit_date"
        const val COLUMN_PRIORITY="priority"
        const val COLUMN_DESCRIPTION="description"
        const val COLUMN_POSITION="position"
        const val COLUMN_CATEGORY_ID="category_id"

        const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITLE TEXT," +
                    "$COLUMN_DESCRIPTION TEXT," +
                    "$COLUMN_DONE BOOLEAN,"+
                    "$COLUMN_LIMIT_DATE LONG,"+
                    "$COLUMN_PRIORITY BOOLEAN,"+
                    "$COLUMN_POSITION INTEGER,"+
                    "$COLUMN_CATEGORY_ID INTEGER " +
                    "REFERENCES ${Category.TABLE_NAME}(${Category.COLUMN_ID}) ON DELETE CASCADE)"

        const val SQL_DELETE = "DROP TABLE IF EXISTS $TABLE_NAME"

    }
}