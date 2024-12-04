package by.zhogol.fittrack.database

data class Workout(
    val id: Int,
    val userId: Int,
    val name: String,
    val duration: Int,
    val date: String,
    val frequency: String)

