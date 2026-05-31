package com.example.feature.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.UiState
import com.example.core.navigation.NavigationContract

data class HabitTemplate(
    val title: String,
    val description: String,
    val target: Int
)

val templates = listOf(
    HabitTemplate("Пить воду", "Соблюдать гидратацию, 8 стаканов", 8),
    HabitTemplate("Читать книгу", "Прочитать 15 страниц книги", 1),
    HabitTemplate("Тренироваться", "Разминка или полноценная тренировка", 1),
    HabitTemplate("Учить английский", "Выучить 10 новых слов", 10),
    HabitTemplate("Медитировать", "Провести 10 минут в осознанности", 1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    viewModel: CreateHabitViewModel,
    navigator: NavigationContract,
    modifier: Modifier = Modifier
) {
    val creationState by viewModel.creationState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("1") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var targetError by remember { mutableStateOf<String?>(null) }
    
    var showTemplates by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showTemplates = true
    }

    LaunchedEffect(creationState) {
        if (creationState is UiState.Success) {
            viewModel.resetState()
            navigator.navigateBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Создание привычки", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.navigateBack() },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("create_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Suggestion templates with AnimatedVisibility
            androidx.compose.animation.AnimatedVisibility(
                visible = showTemplates,
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Шаблоны",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Быстрые шаблоны",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(templates) { template ->
                            InputChip(
                                selected = false,
                                onClick = {
                                    title = template.title
                                    description = template.description
                                    targetStr = template.target.toString()
                                    titleError = null
                                    targetError = null
                                },
                                label = { Text(template.title) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }
                }
            }

            // Input Fields
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = if (it.isBlank()) "Название не может быть пустым" else null
                },
                label = { Text("Название") },
                placeholder = { Text("Какое дело вы хотите добавить?") },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habit_title_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                placeholder = { Text("например: 500 мл каждый час или утренняя разминка") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habit_description_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = targetStr,
                onValueChange = {
                    targetStr = it
                    val parsed = it.toIntOrNull()
                    targetError = if (parsed == null || parsed <= 0) "Цель на день должна быть больше 0" else null
                },
                label = { Text("Цель на день (раз)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = targetError != null,
                supportingText = targetError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habit_target_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (creationState is UiState.Error) {
                Text(
                    text = (creationState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Action Button
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: 1
                    if (title.isNotBlank() && target > 0) {
                        viewModel.createHabit(title, description, target)
                    }
                },
                enabled = title.isNotBlank() && targetError == null && creationState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_habit_button")
                    .minimumInteractiveComponentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (creationState is UiState.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Создать привычку", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
