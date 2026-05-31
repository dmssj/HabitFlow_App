package com.example.feature.auth

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.domain.model.AuthResult

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    val yandexLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onYandexResult(result.resultCode, result.data)
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }

    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HabitFlow Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                val intent = viewModel.getYandexLoginIntent(context) as? Intent
                if (intent != null) {
                    yandexLauncher.launch(intent)
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Login with Yandex")
        }
        
        if (authState is AuthResult.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text((authState as AuthResult.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}
