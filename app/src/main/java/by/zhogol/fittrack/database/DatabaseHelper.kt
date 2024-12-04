package by.zhogol.fittrack.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.random.Random

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "fittrack.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, name TEXT, weight INTEGER, height INTEGER, password TEXT)")
            db.execSQL("CREATE TABLE workouts (_id INTEGER PRIMARY KEY, userId INTEGER, name TEXT, duration INTEGER, date TEXT, frequency TEXT)")
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error or show appropriate message
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS users")
            db.execSQL("DROP TABLE IF EXISTS workouts")
            onCreate(db)
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error or show appropriate message
        }
    }

    fun checkUserCredentials(id: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE _id = ? AND password = ?", arrayOf(id, password))
        val isAuthenticated = cursor.use { it.count > 0 }
        return isAuthenticated
    }

    private fun generateUniqueId(): Int {
        return Random.nextInt(10000000, 100000000)
    }

    fun addUser(name: String, password: String, id: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("_id", id)
            put("name", name)
            put("weight", 0)
            put("height", 0)
            put("password", password)
        }
        return db.insert("users", null, values)
    }

    @SuppressLint("Range")
    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            "users",
            arrayOf("_id", "name", "weight", "height", "password"),
            "_id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var user: User? = null
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex("_id"))
                val name = it.getString(it.getColumnIndex("name"))
                val weight = it.getInt(it.getColumnIndex("weight"))
                val height = it.getInt(it.getColumnIndex("height"))
                val password = it.getString(it.getColumnIndex("password"))
                user = User(id, name, weight, height, password)
            }
        }
        return user
    }

    fun getAllWorkoutsForUser(userId: Int): List<Workout> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM workouts WHERE userId = ?", arrayOf(userId.toString()))
        val workouts = mutableListOf<Workout>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val workout = Workout(
                        id = it.getInt(it.getColumnIndexOrThrow("_id")),
                        userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        duration = it.getInt(it.getColumnIndexOrThrow("duration")),
                        date = it.getString(it.getColumnIndexOrThrow("date")),
                        frequency = it.getString(it.getColumnIndexOrThrow("frequency"))
                    )
                    workouts.add(workout)
                } while (it.moveToNext())
            }
        }
        return workouts
    }

    fun addWorkout(userId: Int, name: String, duration: Int, date: String, frequency: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("_id", generateUniqueId())
            put("userId", userId)
            put("name", name)
            put("duration", duration)
            put("date", date)
            put("frequency", frequency)
        }
        return db.insert("workouts", null, values)
    }

    fun getWorkoutsByDate(date: String, userId: Int): List<Workout> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM workouts WHERE date = ? AND userId = ?", arrayOf(date, userId.toString()))
        val workouts = mutableListOf<Workout>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val workout = Workout(
                        id = it.getInt(it.getColumnIndexOrThrow("_id")),
                        userId = it.getInt(it.getColumnIndexOrThrow("userId")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        duration = it.getInt(it.getColumnIndexOrThrow("duration")),
                        date = it.getString(it.getColumnIndexOrThrow("date")),
                        frequency = it.getString(it.getColumnIndexOrThrow("frequency"))
                    )
                    workouts.add(workout)
                } while (it.moveToNext())
            }
        }
        return workouts
    }

    fun updateUser(userId: Int, name: String, weight: Int, height: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("weight", weight)
            put("height", height)
        }

        val rowsAffected = db.update(
            "users",
            values,
            "_id = ?",
            arrayOf(userId.toString())
        )

        return rowsAffected > 0
    }
    fun deleteWorkout(workoutId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("workouts", "_id = ?", arrayOf(workoutId.toString()))
        db.close()
        return rowsDeleted > 0
    }

    fun deleteUser(userId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("users", "_id = ?", arrayOf(userId.toString()))
        db.close()
        return rowsDeleted > 0
    }

}
