package by.zhogol.fittrack.workout

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.DatabaseHelper
import by.zhogol.fittrack.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class WorkoutActivity : AppCompatActivity() {

    private lateinit var profileButton: Button
    private lateinit var mainButton: Button
    private lateinit var backButton: Button
    private lateinit var saveButton: Button
    private lateinit var workoutTitle: TextView
    private lateinit var trainingFrequencyText: TextView
    private lateinit var frequencySpinner: Spinner
    private lateinit var workoutNameEditText: EditText
    private lateinit var workoutDurationEditText: EditText
    private lateinit var saveImageView: ImageView
    private lateinit var backImageView: ImageView
    private lateinit var workoutDateEditText: EditText

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1 // This will store the userId passed via intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize views and database helper
        initializeViews()
        dbHelper = DatabaseHelper(this)

        // Retrieve userId from the Intent
        userId = intent.getIntExtra("userId", -1)

        // Set up spinner for workout frequency
        setupFrequencySpinner()

        // Setup button listeners
        setupListeners()

        // Setup Date Input
        setupDateInput()
    }

    private fun initializeViews() {
        profileButton = findViewById(R.id.profileButton)
        mainButton = findViewById(R.id.mainButton)
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        workoutTitle = findViewById(R.id.createWorkoutTitle)
        trainingFrequencyText = findViewById(R.id.tv_training_frequency)
        frequencySpinner = findViewById(R.id.spinner_frequency)
        workoutNameEditText = findViewById(R.id.nameWorkoutEnter)
        workoutDurationEditText = findViewById(R.id.et_workout_duration)
        saveImageView = findViewById(R.id.imageView)
        backImageView = findViewById(R.id.backImage)
        workoutDateEditText = findViewById(R.id.enterDate)
    }

    private fun setupFrequencySpinner() {
        val frequencies = arrayOf("Ежедневно", "Еженедельно", "Ежемесячно")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
        saveButton.setOnClickListener {
            saveWorkout()
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("userId", userId) // Передаем userId
            startActivity(intent)
        }
        saveImageView.setOnClickListener {
            saveWorkout()
        }
        backImageView.setOnClickListener {
            finish()
        }
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

        workoutDateEditText.setOnClickListener {
            showDatePickerDialog()
        } // Показываем диалог при нажатии на поле
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Форматируем дату в нужном формате дд.мм.гггг
            val formattedDate = String.format("%02d.%02d.%04d", selectedDay, selectedMonth + 1, selectedYear)
            workoutDateEditText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupDateInput() {
        workoutDateEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                isFormatting = true

                // Удаляем все нецифровые символы
                val cleaned = s.toString().replace(Regex("[^\\d]"), "")

                // Форматируем в дд.мм.гггг
                val formatted = when {
                    cleaned.length <= 2 -> cleaned
                    cleaned.length <= 4 -> "${cleaned.substring(0, 2)}.${cleaned.substring(2)}"
                    cleaned.length <= 8 -> "${cleaned.substring(0, 2)}.${cleaned.substring(2, 4)}.${cleaned.substring(4)}"
                    else -> "${cleaned.substring(0, 2)}.${cleaned.substring(2, 4)}.${cleaned.substring(4, 8)}"
                }

                // Устанавливаем форматированный текст
                workoutDateEditText.setText(formatted)
                workoutDateEditText.setSelection(formatted.length) // Устанавливаем курсор в конец текста

                isFormatting = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateDate(date: String): Boolean {
        // Проверка формата дд.мм.гггг
        val regex = "^([0-2][0-9]|(3)[0-1])\\.(0[1-9]|1[0-2])\\.(\\d{4})$".toRegex()
        if (!regex.matches(date)) {
            Toast.makeText(this, "Неверный формат даты. Используйте дд.мм.гггг", Toast.LENGTH_SHORT).show()
            return false
        }

        // Разделение на день, месяц и год
        val parts = date.split(".")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        // Проверка на существование даты
        if (year > 2100 || year < 1900) {
            Toast.makeText(this, "Год должен быть между 1900 и 2100", Toast.LENGTH_SHORT).show()
            return false
        }

        if (month > 12 || month < 1) {
            Toast.makeText(this, "Месяц должен быть от 01 до 12", Toast.LENGTH_SHORT).show()
            return false
        }

        // Проверка количества дней в месяце
        val daysInMonth = getDaysInMonth(month, year)
        if (day > daysInMonth) {
            Toast.makeText(this, "В этом месяце только $daysInMonth дней", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            else -> 0
        }
    }

    private fun saveWorkout() {
        val workoutName = workoutNameEditText.text.toString()
        val workoutDuration = workoutDurationEditText.text.toString()
        val workoutDate = workoutDateEditText.text.toString()
        val frequency = frequencySpinner.selectedItem.toString()

        // Проверка даты
        if (workoutName.isEmpty() || workoutDuration.isEmpty() || workoutDate.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
        } else if (validateDate(workoutDate)) {
            saveWorkoutToDatabase(workoutName, workoutDuration, workoutDate, frequency)
        }
    }

    private fun saveWorkoutToDatabase(workoutName: String, workoutDuration: String, workoutDate: String, frequency: String) {
        val durationInt = workoutDuration.toIntOrNull() ?: 0
        val dateFormatted = formatDate(workoutDate)

        // Save workout to the database
        val workoutId = dbHelper.addWorkout(userId, workoutName, durationInt, dateFormatted, frequency)

        if (workoutId > 0) {
            showToast("Тренировка сохранена")
            clearFields()
        } else {
            showToast("Ошибка при сохранении тренировки")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearFields() {
        workoutNameEditText.text.clear()
        workoutDurationEditText.text.clear()
        workoutDateEditText.text.clear()
    }
    private fun formatDate(dateString: String): String {
        // Use the input as the validated date and return in the required format
        val parts = dateString.split(".")
        return "${parts[2]}-${parts[1]}-${parts[0]}" // Convert to yyyy-MM-dd
    }

}
