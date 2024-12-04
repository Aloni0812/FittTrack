package by.zhogol.fittrack

@Suppress("KotlinJniMissingFunction")
class NativeLib {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }

        external fun validatePasswords(password: String, repeatPassword: String): Boolean
        external fun generateUserId(): Int
    }
}
