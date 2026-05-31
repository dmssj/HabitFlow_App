package com.example.feature.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.feature.about.R

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        
        // Передача данных в Compose
        val appName = "HabitFlow"
        val version = "1.0.0"
        val developer = "Захаров Дмитрий"

        composeView.setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Название: $appName", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Версия: $version", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Разработчик: $developer", style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(onClick = {
                            val intent = android.content.Intent(this@AboutActivity, Class.forName("com.example.MainActivity")).apply {
                                putExtra("navigate_to", "about_company")
                                flags = android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                            startActivity(intent)
                        }) {
                            Text("О компании (Карта)")
                        }
                    }
                }
            }
        }
    }
}
