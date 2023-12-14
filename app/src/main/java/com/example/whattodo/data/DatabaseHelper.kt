import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.whattodo.model.Habit
import com.example.whattodo.model.Task

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskDatabase"
        private const val TABLE_TASKS = "tasks"
        private const val TABLE_HABIT = "habit"

        // Columns for tasks table
        private const val COLUMN_ID_TASKS = "id"
        private const val COLUMN_TITLE_TASKS = "title"
        private const val COLUMN_IS_DONE = "isDone"
        private const val COLUMN_REMINDER_TASKS = "reminder"
        private const val COLUMN_REMINDER_TIME_TASKS = "reminderTime"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_NOTIFICATION_ID = "notificationId"

        // Columns for habit table
        private const val COLUMN_ID_HABIT = "id"
        private const val COLUMN_TITLE_HABIT = "title"
        private const val COLUMN_REMINDER_HABIT = "reminder"
        private const val COLUMN_REMINDER_TIME_HABIT = "reminderTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TASKS_TABLE_QUERY = ("CREATE TABLE $TABLE_TASKS " +
                "($COLUMN_ID_TASKS INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE_TASKS TEXT, " +
                "$COLUMN_IS_DONE INTEGER, " +
                "$COLUMN_REMINDER_TASKS INTEGER, " +
                "$COLUMN_REMINDER_TIME_TASKS TEXT, " +
                "$COLUMN_PHOTO INTEGER, " +
                "$COLUMN_NOTIFICATION_ID INTEGER)")

        val CREATE_HABIT_TABLE_QUERY = ("CREATE TABLE $TABLE_HABIT " +
                "($COLUMN_ID_HABIT INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE_HABIT TEXT, " +
                "$COLUMN_REMINDER_HABIT INTEGER, " +
                "$COLUMN_REMINDER_TIME_HABIT TEXT)")

        db?.execSQL(CREATE_TASKS_TABLE_QUERY)
        db?.execSQL(CREATE_HABIT_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HABIT")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE_TASKS, task.title)
        values.put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
        values.put(COLUMN_REMINDER_TASKS, if (task.reminder) 1 else 0)
        values.put(COLUMN_REMINDER_TIME_TASKS, task.reminderTime)
        values.put(COLUMN_PHOTO, if (task.photo) 1 else 0)
        values.put(COLUMN_NOTIFICATION_ID, task.notificationId)

        val id = db.insert(TABLE_TASKS, null, values)
        db.close()
        return id
    }



    fun getAllTasks(): ArrayList<Task> {
        val tasks = ArrayList<Task>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_TASKS", null)

        cursor?.use {
            val idIndex = cursor.getColumnIndex(COLUMN_ID_TASKS)
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE_TASKS)
            val isDoneIndex = cursor.getColumnIndex(COLUMN_IS_DONE)
            val reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER_TASKS)
            val reminderTimeIndex = cursor.getColumnIndex(COLUMN_REMINDER_TIME_TASKS)
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

    fun getAllHabits(): ArrayList<Habit> {
        val habits = ArrayList<Habit>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_HABIT", null)

        cursor?.use {
            val idIndex = cursor.getColumnIndex(COLUMN_ID_HABIT)
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE_HABIT)
            val reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER_HABIT)
            val reminderTimeIndex = cursor.getColumnIndex(COLUMN_REMINDER_TIME_HABIT)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(idIndex)
                    val title = cursor.getString(titleIndex)
                    val reminder = cursor.getInt(reminderIndex) == 1
                    val reminderTime = cursor.getString(reminderTimeIndex)

                    val habit = Habit(id, title, reminder, reminderTime)
                    habits.add(habit)
                } while (cursor.moveToNext())
            }
        }

        cursor?.close()
        db.close()
        return habits
    }

    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID_TASKS = ?"
        val whereArgs = arrayOf(taskId.toString())

        val deletedRows = db.delete(TABLE_TASKS, whereClause, whereArgs)
        db.close()
        return deletedRows
    }


    fun updateReminder(taskId: Int, task: Task): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_REMINDER_TASKS, if (task.reminder) 1 else 0)
        contentValues.put(COLUMN_REMINDER_TIME_TASKS, task.reminderTime)
        val whereClause = "$COLUMN_ID_TASKS = ?"
        val whereArgs = arrayOf(taskId.toString())

        val updatedRows = db.update(TABLE_TASKS, contentValues, whereClause, whereArgs)

        db.close()

        return updatedRows > 0
    }

    fun changeIsDone(taskId: Int, newIsDoneStatus: Boolean): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_IS_DONE, if (newIsDoneStatus) 1 else 0)

        val whereClause = "$COLUMN_ID_TASKS = ?"
        val whereArgs = arrayOf(taskId.toString())

        val updatedRows = db.update(TABLE_TASKS, contentValues, whereClause, whereArgs)

        db.close()

        return updatedRows > 0
    }

    fun deleteHabit(habitId: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID_HABIT = ?"
        val whereArgs = arrayOf(habitId.toString())

        val deletedRows = db.delete(TABLE_HABIT, whereClause, whereArgs)
        db.close()
        return deletedRows
    }

    fun addHabit(habit: Habit): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE_HABIT, habit.title)
        values.put(COLUMN_REMINDER_HABIT, if (habit.reminder) 1 else 0)
        values.put(COLUMN_REMINDER_TIME_HABIT, habit.reminderTime)

        val id = db.insert(TABLE_HABIT, null, values)
        db.close()
        return id
    }



}
