import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.whattodo.model.Task

class TaskDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskDatabase"
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_IS_DONE = "isDone"
        private const val COLUMN_REMINDER = "reminder"
        private const val COLUMN_REMINDER_TIME = "reminderTime"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_NOTIFICATION_ID = "notificationId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_IS_DONE INTEGER, " +
                "$COLUMN_REMINDER INTEGER, " +
                "$COLUMN_REMINDER_TIME TEXT, " +
                "$COLUMN_PHOTO INTEGER, " +
                "$COLUMN_NOTIFICATION_ID INTEGER)")
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, task.title)
        values.put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
        values.put(COLUMN_REMINDER, if (task.reminder) 1 else 0)
        values.put(COLUMN_REMINDER_TIME, task.reminderTime)
        values.put(COLUMN_PHOTO, if (task.photo) 1 else 0)
        values.put(COLUMN_NOTIFICATION_ID, task.notificationId)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllTasks(): ArrayList<Task> {
        val tasks = ArrayList<Task>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        cursor?.use {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
            val isDoneIndex = cursor.getColumnIndex(COLUMN_IS_DONE)
            val reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER)
            val reminderTimeIndex = cursor.getColumnIndex(COLUMN_REMINDER_TIME)
            val photoIndex = cursor.getColumnIndex(COLUMN_PHOTO)
            val notificationIdIndex = cursor.getColumnIndex(COLUMN_NOTIFICATION_ID)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(idIndex)
                    val title = cursor.getString(titleIndex)
                    val isDone = cursor.getInt(isDoneIndex) == 1
                    val reminder = cursor.getInt(reminderIndex) == 1
                    val reminderTime = cursor.getString(reminderTimeIndex)
                    val photo = cursor.getInt(photoIndex) == 1
                    val notificationId = cursor.getInt(notificationIdIndex)

                    val task = Task(id, title, isDone, reminder, reminderTime, photo, notificationId)
                    tasks.add(task)
                } while (cursor.moveToNext())
            }
        }

        cursor?.close()
        db.close()
        return tasks
    }

    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(taskId.toString())

        val deletedRows = db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
        return deletedRows
    }

    @SuppressLint("Range")
    fun getTask(taskId: Int): Task? {
        val db = this.readableDatabase
        var task: Task? = null
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(taskId.toString())

        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex(COLUMN_ID))
                val title = it.getString(it.getColumnIndex(COLUMN_TITLE))
                val isDone = it.getInt(it.getColumnIndex(COLUMN_IS_DONE)) == 1
                val reminder = it.getInt(it.getColumnIndex(COLUMN_REMINDER)) == 1
                val reminderTime = it.getString(it.getColumnIndex(COLUMN_REMINDER_TIME))
                val photo = it.getInt(it.getColumnIndex(COLUMN_PHOTO)) == 1
                val notificationId = it.getInt(it.getColumnIndex(COLUMN_NOTIFICATION_ID))

                task = Task(id, title, isDone, reminder, reminderTime, photo, notificationId)
            }
        }

        cursor.close()
        db.close()
        return task
    }
    // Implement other methods like updateTask(), deleteTask(), etc. based on your requirements
    // Remember to handle exceptions and close the database properly
}
