package by.zhogol.fittrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.Workout

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onWorkoutLongClick: (Workout) -> Unit // Обработчик долгого нажатия
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    // Внутренний класс для ViewHolder
    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutName: TextView = view.findViewById(R.id.trainingName)
        val workoutDuration: TextView = view.findViewById(R.id.trainingDescription)
        val workoutDate: TextView = view.findViewById(R.id.trainingDate) // Добавили дату
        val workoutFrequency: TextView = view.findViewById(R.id.trainingFrequency) // Добавили частоту
    }

    // Создание нового элемента для RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training, parent, false)
        return WorkoutViewHolder(view)
    }

    // Привязка данных из workout к соответствующим представлениям в ViewHolder
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.name
        holder.workoutDuration.text = "Продолжительность: ${workout.duration} мин"
        holder.workoutDate.text = "Дата: ${workout.date}"
        holder.workoutFrequency.text = "Частота: ${workout.frequency}"

        // Обработка долгого нажатия на элемент
        holder.itemView.setOnLongClickListener {
            onWorkoutLongClick(workout) // Вызов обработчика
            true // Возвращаем true, чтобы предотвратить другие действия при долгом нажатии
        }
    }

    // Возвращаем количество элементов в списке
    override fun getItemCount(): Int = workouts.size

    // Обновление данных в адаптере
    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}

