package by.zhogol.fittrack.profile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import by.zhogol.fittrack.R
import by.zhogol.fittrack.auth.AuthorizationActivity
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.workout.StatisticsActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var titleTextView: TextView
    private lateinit var userNameEditText: EditText
    private lateinit var currentWeightEditText: EditText
    private lateinit var currentHeightEditText: EditText
    private lateinit var btnProfile: Button // Кнопка "Профиль"
    private lateinit var btnStatistics: Button // Кнопка "Статистика"
    private lateinit var deleteProfileButton: Button // Кнопка "Удалить профиль"
    private lateinit var exitButton: Button // Кнопка "Выйти из профиля"

    private var userId: Int = -1 // Переменная для хранения userId
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Получаем userId из Intent
        userId = intent.getIntExtra("userId", -1)

        dbHelper = DatabaseHelper(this)

        initializeViews()
        setupListeners()

        // Загрузка данных пользователя и установка их в поля
        loadUserData()
    }

    private fun initializeViews() {
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        titleTextView = findViewById(R.id.editProfileTitle)
        userNameEditText = findViewById(R.id.et_user_name)
        currentWeightEditText = findViewById(R.id.et_current_weight)
        currentHeightEditText = findViewById(R.id.et_current_height)
        btnProfile = findViewById(R.id.profileButton)
        btnStatistics = findViewById(R.id.mainButton)
        deleteProfileButton = findViewById(R.id.button) // Инициализация кнопки "Удалить профиль"
        exitButton = findViewById(R.id.exit) // Инициализация кнопки "Выйти из профиля"
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            val updatedName = userNameEditText.text.toString()
            val updatedWeight = currentWeightEditText.text.toString().toIntOrNull() ?: 0
            val updatedHeight = currentHeightEditText.text.toString().toIntOrNull() ?: 0

            val success = dbHelper.updateUser(userId, updatedName, updatedWeight, updatedHeight)

            if (success) {
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
                navigateToProfile()
            } else {
                Toast.makeText(this, "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            navigateToProfile()
        }

        btnProfile.setOnClickListener {
            navigateToProfile()
        }

        btnStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        deleteProfileButton.setOnClickListener {
            val success = dbHelper.deleteUser(userId)
            if (success) {
                Toast.makeText(this, "Профиль удалён", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            } else {
                Toast.makeText(this, "Ошибка при удалении профиля", Toast.LENGTH_SHORT).show()
            }
        }

        exitButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun loadUserData() {
        val user = dbHelper.getUserById(userId)
        if (user != null) {
            userNameEditText.setText(user.name)
            currentWeightEditText.setText(user.weight.toString())
            currentHeightEditText.setText(user.height.toString())
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
