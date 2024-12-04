package by.zhogol.fittrack.profile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.workout.GuideActivity
import by.zhogol.fittrack.workout.StatisticsActivity
import by.zhogol.fittrack.workout.WorkoutActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var profileButton: Button
    private lateinit var mainButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var guideListButton: Button // Кнопка "Список тренировок"
    private lateinit var userNameTextView: TextView
    private lateinit var userHeightTextView: TextView
    private lateinit var userWeightTextView: TextView
    private lateinit var userIdTextView: TextView // Новый TextView для ID
    private lateinit var backImageView: ImageView
    private lateinit var addWorkout : Button

    private var userId: Int = -1 // Объявляем userId как свойство класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Получаем userId из Intent
        //userId = intent.getIntExtra("userId", -1)

        //initializeViews()
        //setupListeners()

        // Установка данных профиля
        //loadUserProfile()
    }

    private fun initializeViews() {

        profileButton = findViewById(R.id.profileButton)
        mainButton = findViewById(R.id.mainButton)
        editProfileButton = findViewById(R.id.btn_edit_profile)
        guideListButton = findViewById(R.id.btn_guide_list) // Инициализируем кнопку "Список тренировок"
        userNameTextView = findViewById(R.id.tv_user_name)
        userHeightTextView = findViewById(R.id.tv_current_height)
        userWeightTextView = findViewById(R.id.tv_current_weight)
        userIdTextView = findViewById(R.id.id) // Инициализируем TextView для ID
        addWorkout = findViewById(R.id.btn_add_workout)
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

        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }

        guideListButton.setOnClickListener {
            val intent = Intent(this, GuideActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }
        addWorkout.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        if (userId != -1) {
            val dbHelper = DatabaseHelper(this)
            val user = dbHelper.getUserById(userId)

            if (user != null) {
                userNameTextView.text = user.name
                userHeightTextView.text = "Рост: ${user.height} см"
                userWeightTextView.text = "Вес: ${user.weight} кг"
                userIdTextView.text = "ID: ${user.id}" // Устанавливаем ID
            } else {
                showToast("Пользователь не найден")
            }
        } else {
            showToast("Ошибка загрузки профиля")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
