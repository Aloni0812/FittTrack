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
import by.zhogol.fittrack.NativeLib
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.profile.ProfileActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var labelTextView: TextView
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordRepeatEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var dbHelper: DatabaseHelper

    private val SWIPE_THRESHOLD = 100
    private var startX: Float = 0f
    private var endX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initializeViews()
        dbHelper = DatabaseHelper(this)

        setupListeners()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val registerIntent = Intent(this, AuthorizationActivity::class.java)
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
        nameEditText = findViewById(R.id.nameEnter)
        passwordEditText = findViewById(R.id.passwordEnter)
        passwordRepeatEditText = findViewById(R.id.passwordRepeat)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signupButton)
        labelTextView = findViewById(R.id.registration)
    }

    private fun setupListeners() {
        signUpButton.setOnClickListener {
            if (validateRegistrationData()) {
                val name = nameEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                val userId = NativeLib.generateUserId() // Генерация userId через нативную функцию

                val result = dbHelper.addUser(name, password, userId)

                if (result != -1L) {
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("userId", userId.toInt()) // Передаем userId
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Ошибка при регистрации, попробуйте снова.")
                }
            }
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, AuthorizationActivity::class.java)
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
                    endX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    when {
                        endX > startX + SWIPE_THRESHOLD -> swipeRight()
                        startX > endX + SWIPE_THRESHOLD -> swipeLeft()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun validateRegistrationData(): Boolean {
        val name = nameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val passwordRepeat = passwordRepeatEditText.text.toString().trim()

        return when {
            name.isEmpty() -> {
                showErrorMessage("Пожалуйста, введите имя")
                false
            }
            password.isEmpty() -> {
                showErrorMessage("Пожалуйста, введите пароль")
                false
            }
            !NativeLib.validatePasswords(password, passwordRepeat) -> { // Проверка совпадения паролей через нативный метод
                showErrorMessage("Пароли не совпадают")
                false
            }
            else -> true
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
