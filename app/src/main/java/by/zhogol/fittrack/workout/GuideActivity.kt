package by.zhogol.fittrack.workout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
import by.zhogol.fittrack.workout.StatisticsActivity

class GuideActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var exerciseTitle: TextView
    private lateinit var recyclerView: RecyclerView // RecyclerView
    private lateinit var profileButton: Button
    private lateinit var mainButton: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var workoutAdapter: WorkoutAdapter

    private var userId: Int = -1 // Объявляем userId как свойство класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        // Получаем userId из Intent
        userId = intent.getIntExtra("userId", -1)

        initializeViews()
        setupListeners()

        // Получаем все тренировки для текущего пользователя
        databaseHelper = DatabaseHelper(this)
        val workouts = databaseHelper.getAllWorkoutsForUser(userId) // Получаем все тренировки для пользователя

        // Инициализируем адаптер и передаем обработчик долгого нажатия
        workoutAdapter = WorkoutAdapter(workouts) { workout ->
            showDeleteConfirmationDialog(workout) // Показать диалог подтверждения
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = workoutAdapter
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        exerciseTitle = findViewById(R.id.exerciseTitle)
        recyclerView = findViewById(R.id.recyclerView) // Указываем RecyclerView
        profileButton = findViewById(R.id.profileButton)
        mainButton = findViewById(R.id.mainButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        mainButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
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
        val isDeleted = databaseHelper.deleteWorkout(workout.id)
        if (isDeleted) {
            // Получаем обновленный список тренировок после удаления
            val workouts = databaseHelper.getAllWorkoutsForUser(userId)

            // Обновляем данные в адаптере
            workoutAdapter.updateData(workouts)

            // Показываем сообщение об успешном удалении
            Toast.makeText(this, "Тренировка удалена", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Не удалось удалить тренировку", Toast.LENGTH_SHORT).show()
        }
    }
}
