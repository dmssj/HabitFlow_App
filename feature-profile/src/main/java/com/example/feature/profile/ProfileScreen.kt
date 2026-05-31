package com.example.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.service.RemoteConfigService
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    remoteConfigService: RemoteConfigService,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val welcomeMessage = remember { remoteConfigService.getString("welcome_message") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collectLatest {
            snackbarHostState.showSnackbar("Профиль успешно сохранен!")
            onBack()
        }
    }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            email = it.email
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(welcomeMessage, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)

            Text("Профиль пользователя", style = MaterialTheme.typography.headlineMedium)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text("Обновлено: ${profile?.updatedAt ?: "Никогда"}", style = MaterialTheme.typography.bodySmall)

            Button(
                onClick = { viewModel.saveProfile(name, email) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить профиль")
            }
            
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }
    }
}
