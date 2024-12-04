package by.zhogol.fittrack.database

data class User(
    val id: Int,
    val name: String,
    val weight: Int,
    val height: Int,
    val password: String)