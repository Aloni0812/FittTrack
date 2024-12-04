package by.zhogol.fittrack.auth

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.profile.ProfileActivity

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var labelTextView: TextView
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var logInButton: Button
    private lateinit var signUpButton: Button
    private val SWIPE_THRESHOLD = 100 // Порог для определения свайпа
    private var startX: Float = 0f
    private var endX:  Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        dbHelper = DatabaseHelper(this) // Создаем экземпляр DatabaseHelper

        initializeViews()
        setupListeners()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val registerIntent = Intent(this,  AuthorizationActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val profileIntent = Intent(this, RegistrationActivity::class.java)
        startActivity(profileIntent)
        finish()
    }


    private fun initializeViews() {
        labelTextView = findViewById(R.id.label)
        loginEditText = findViewById(R.id.nameEnter)
        passwordEditText = findViewById(R.id.passwordEnter)
        logInButton = findViewById(R.id.logInButton)
        signUpButton = findViewById(R.id.signupButton)
    }

    private fun setupListeners() {
        logInButton.setOnClickListener {
            if (validateCredentials()) {
                val username = loginEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                // Проверка учетных данных в базе данных
                val isAuthenticated = dbHelper.checkUserCredentials(username, password)
                if (isAuthenticated) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("userId", username.toInt()) // Передаем userId
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Неверные логин или пароль")
                }
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
        window.decorView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    endX = event.x // Можно оставить для отладки
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x // Обновляем endX при отпускании
                    when {
                        endX > startX + SWIPE_THRESHOLD -> swipeRight()  // Свайп вправо
                        startX > endX + SWIPE_THRESHOLD -> swipeLeft()
                    }
                    true

                }
                else -> false
            }
        }
    }
    private fun validateCredentials(): Boolean {
        val username = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        return when {
            username.isEmpty() -> {
                showErrorMessage("Пожалуйста, введите логин")
                false
            }
            !isValidUsername(username) -> {
                showErrorMessage("Логин должен состоять из 8 цифр")
                false
            }
            password.isEmpty() -> {
                showErrorMessage("Пожалуйста, введите пароль")
                false
            }
            !isValidPassword(password) -> {
                showErrorMessage("Пароль должен содержать не менее 8 символов")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length == 8 && username.all { it.isDigit() }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideErrorMessage() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
    }

    private fun performAuthentication(username: String, password: String) {
        // Здесь должна быть реальная логика аутентификации
        // Например, запрос к серверу или проверка в локальном хранилище
        // Для простоты примера мы просто выводим сообщение о успехе
        Toast.makeText(this, "Аутентификация успешна", Toast.LENGTH_SHORT).show()

        // После успешной аутентификации можно перейти на следующую активность
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}

