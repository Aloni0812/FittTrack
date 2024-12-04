package by.zhogol.fittrack.workout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.zhogol.fittrack.R
import by.zhogol.fittrack.adapters.WorkoutAdapter
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.database.Workout
import by.zhogol.fittrack.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var backButton: Button
    private lateinit var profileButton: Button
    private lateinit var mainButton: Button

    private var userId: Int = -1 // Переменная для хранения userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Используем правильный макет

        // Получаем userId из Intent
        userId = intent.getIntExtra("userId", -1)

        dbHelper = DatabaseHelper(this)

        initializeViews()
        setupListeners()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.timeInMillis
            loadWorkoutsByDate(selectedDate)
        }
    }

    private fun initializeViews() {
        // Инициализация представлений
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        workoutAdapter = WorkoutAdapter(emptyList()) { workout ->
            showDeleteConfirmationDialog(workout) // Обработчик долгого нажатия
        }
        recyclerView.adapter = workoutAdapter

        // Убедитесь, что ID кнопок совпадают с теми, что в XML
        backButton = findViewById(R.id.backButton)
        profileButton = findViewById(R.id.profileButton)  // исправлено на правильный ID
        mainButton = findViewById(R.id.mainButton)  // исправлено на правильный ID
    }

    private fun setupListeners() {
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }

        mainButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }
    }

    private fun loadWorkoutsByDate(selectedDate: Long) {
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDate))
        // Передаем userId для фильтрации тренировок по пользователю
        val filteredWorkouts = dbHelper.getWorkoutsByDate(dateString, userId)
        workoutAdapter.updateData(filteredWorkouts)
    }

    private fun showDeleteConfirmationDialog(workout: Workout) {
        val dialog = AlertDialog.Builder(this)
            .setMessage("Вы хотите удалить тренировку \"${workout.name}\"?")
            .setPositiveButton("Да") { _, _ ->
                deleteWorkout(workout)
            }
            .setNegativeButton("Нет", null)
            .create()

        dialog.show()
    }

    private fun deleteWorkout(workout: Workout) {
        // Удаляем тренировку из базы данных
        val isDeleted = dbHelper.deleteWorkout(workout.id)
        if (isDeleted) {
            // Обновляем список тренировок для этого пользователя
            val allWorkouts = dbHelper.getAllWorkoutsForUser(userId) // Загружаем все тренировки для пользователя
            workoutAdapter.updateData(allWorkouts)  // Обновляем данные в адаптере
            Toast.makeText(this, "Тренировка удалена", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Не удалось удалить тренировку", Toast.LENGTH_SHORT).show()
        }
    }
}
