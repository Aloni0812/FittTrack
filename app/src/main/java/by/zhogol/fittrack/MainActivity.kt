package by.zhogol.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import by.zhogol.fittrack.ui.theme.FitTrackTheme


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import by.zhogol.fittrack.auth.AuthorizationActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_medicine)
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}