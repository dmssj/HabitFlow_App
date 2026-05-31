package com.example.feature.main.details

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.DateUtils
import com.example.core.UiState
import com.example.core.navigation.NavigationContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Long,
    viewModel: HabitDetailsViewModel,
    navigator: NavigationContract,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(habitId) {
        viewModel.setHabitId(habitId)
    }

    val uiState by viewModel.uiState.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Детали привычки", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.navigateBack() },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("details_back")
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (!isEditing && uiState is UiState.Success) {
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("edit_habit_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать привычку")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("details_loader")
                    )
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        fontSize = 16.sp
                    )
                }
                is UiState.Success -> {
                    val habit = state.data

                    if (isEditing) {
                        EditHabitContent(
                            initialTitle = habit.title,
                            initialDescription = habit.description,
                            initialTarget = habit.targetPerDay,
                            onSave = { title, desc, target ->
                                viewModel.updateHabit(title, desc, target)
                                isEditing = false
                            },
                            onCancel = { isEditing = false }
                        )
                    } else {
                        ViewHabitContent(
                            habit = habit,
                            onComplete = { viewModel.completeHabit() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ViewHabitContent(
    habit: com.example.domain.model.Habit,
    onComplete: () -> Unit
) {
    val progress = if (habit.targetPerDay > 0) {
        habit.completedToday.toFloat() / habit.targetPerDay.toFloat()
    } else {
        0.0f
    }.coerceAtMost(1.0f)

    val isDone = habit.completedToday >= habit.targetPerDay

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Status Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${habit.completedToday}/${habit.targetPerDay}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "сегодня",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = habit.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = habit.description,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Badge
                if (isDone) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Успешно",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Цель на сегодня достигнута!",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metadata Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Создано", tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Создано", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatEpochDayMonth(habit.createdAt), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Частота", tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Цель", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${habit.targetPerDay} в день", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Log Progress Button
        Button(
            onClick = onComplete,
            enabled = !isDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("details_complete_button")
                .minimumInteractiveComponentSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Записать прогресс")
            Spacer(modifier = Modifier.width(10.dp))
            Text(if (isDone) "Выполнено на сегодня!" else "Записать прогресс (+1)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EditHabitContent(
    initialTitle: String,
    initialDescription: String,
    initialTarget: Int,
    onSave: (String, String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var targetStr by remember { mutableStateOf(initialTarget.toString()) }

    var targetError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = if (it.isBlank()) "Название не может быть пустым" else null
            },
            label = { Text("Название") },
            placeholder = { Text("например: Пить воду") },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_title_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            placeholder = { Text("например: 8 стаканов фильтрованной воды") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = targetStr,
            onValueChange = {
                targetStr = it
                val parsed = it.toIntOrNull()
                targetError = if (parsed == null || parsed <= 0) "Должно быть целым числом больше 0" else null
            },
            label = { Text("Цель на день") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = targetError != null,
            supportingText = targetError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_target_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .minimumInteractiveComponentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Отмена")
            }

            Button(
                onClick = {
                    val parsedTarget = targetStr.toIntOrNull() ?: 1
                    if (title.isNotBlank() && parsedTarget > 0) {
                        onSave(title, description, parsedTarget)
                    }
                },
                enabled = title.isNotBlank() && targetError == null,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("save_edit_button")
                    .minimumInteractiveComponentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Сохранить изменения")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сохранить")
            }
        }
    }
}
