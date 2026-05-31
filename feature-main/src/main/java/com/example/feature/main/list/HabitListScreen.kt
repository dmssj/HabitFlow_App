package com.example.feature.main.list

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.UiState
import com.example.core.navigation.NavigationContract
import com.example.domain.model.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel,
    navigator: NavigationContract,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        try {
            val sdf = java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale("ru"))
            sdf.format(java.util.Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("ru")) else it.toString() }
        } catch (e: Exception) {
            "Среда, 20 мая"
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = "HabitFlow",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp, // Slightly reduced to ensure it fits
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentDateStr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navigator.navigateToStatistics() },
                        modifier = Modifier.testTag("statistics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Статистика",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Распознавание ИИ") },
                            onClick = {
                                showMenu = false
                                navigator.navigateToDetection()
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Профиль") },
                            onClick = {
                                showMenu = false
                                navigator.navigateToProfile()
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("О приложении") },
                            onClick = {
                                showMenu = false
                                navigator.navigateToAbout()
                            },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Crash Test") },
                            onClick = {
                                showMenu = false
                                navigator.navigateToCrashTest()
                            },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigateToCreateHabit() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = "Создать привычку") },
                text = { Text("Добавить") },
                modifier = Modifier
                    .testTag("add_habit_fab")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = uiState, label = "ScreenState") { state ->
                when (state) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.testTag("loading_indicator")
                            )
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Ошибка",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Что-то пошло не так",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { viewModel.retry() }) {
                                Text("Попробовать снова")
                            }
                        }
                    }
                    is UiState.Success -> {
                        val habits = state.data
                        if (habits.isEmpty()) {
                            EmptyState(onAddClick = { navigator.navigateToCreateHabit() })
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 80.dp // extra spacing for FAB
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    DailyProgressBanner(habits = habits)
                                }
                                items(
                                    items = habits,
                                    key = { it.id }
                                ) { habit ->
                                    HabitCard(
                                        habit = habit,
                                        onCompleteClick = { viewModel.completeHabit(habit.id) },
                                        onDeleteClick = { viewModel.deleteHabit(habit) },
                                        onCardClick = { navigator.navigateToHabitDetails(habit.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyProgressBanner(habits: List<Habit>, modifier: Modifier = Modifier) {
    val completedCount = habits.count { it.completedToday >= it.targetPerDay }
    val totalCount = habits.size
    val progressPercent = if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat() * 100).toInt() else 0
    val targetProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 800),
        label = "ProgressAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ПРОГРЕСС НА СЕГОДНЯ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Выполнено на $progressPercent%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Выполнено привычек: $completedCount из $totalCount",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    strokeWidth = 6.dp
                )
                Text(
                    text = "$progressPercent%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Composable
fun EmptyState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Проверить привычки",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Добро пожаловать в HabitFlow!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Привычки еще не созданы. Отслеживайте потребление воды, чтение, тренировки или другие дела регулярной рутины.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Text("Создать первую привычку")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HabitCard(
    habit: Habit,
    onCompleteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val progress = if (habit.targetPerDay > 0) {
        habit.completedToday.toFloat() / habit.targetPerDay.toFloat()
    } else {
        0.0f
    }.coerceAtMost(1.0f)

    val isDone = habit.completedToday >= habit.targetPerDay

    // Dynamically choose pastel background and modern emoji icon based on habit keywords
    val titleLower = habit.title.lowercase()
    val (emoji, bgTint, textTint) = when {
        titleLower.contains("water") || titleLower.contains("drink") || titleLower.contains("hydrate") ||
        titleLower.contains("вода") || titleLower.contains("пить") || titleLower.contains("гидрат") || titleLower.contains("аква") ->
            Triple("💧", Color(0xFFEFF6FF), Color(0xFF2563EB))
        titleLower.contains("read") || titleLower.contains("book") ||
        titleLower.contains("читать") || titleLower.contains("книга") || titleLower.contains("книгу") ->
            Triple("📖", Color(0xFFFFF7ED), Color(0xFFEA580C))
        titleLower.contains("exercise") || titleLower.contains("workout") || titleLower.contains("run") || titleLower.contains("gym") || titleLower.contains("fit") ||
        titleLower.contains("тренировка") || titleLower.contains("спорт") || titleLower.contains("бег") || titleLower.contains("зал") || titleLower.contains("фитнес") ->
            Triple("💪", Color(0xFFF0FDF4), Color(0xFF16A34A))
        titleLower.contains("learn") || titleLower.contains("english") || titleLower.contains("language") || titleLower.contains("study") ||
        titleLower.contains("учить") || titleLower.contains("английский") || titleLower.contains("язык") || titleLower.contains("учеба") || titleLower.contains("изучать") ->
            Triple("📚", Color(0xFFFAF5FF), Color(0xFF9333EA))
        titleLower.contains("meditate") || titleLower.contains("mindful") || titleLower.contains("zen") ||
        titleLower.contains("медитация") || titleLower.contains("медитировать") || titleLower.contains("осознанность") || titleLower.contains("дзен") ->
            Triple("🧘", Color(0xFFFDF2F8), Color(0xFFDB2777))
        else ->
            Triple("✨", Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}")
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Elegant category badge with modern emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgTint),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            // Title & Progress labels
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Цель: ${habit.targetPerDay} в день • Выполнено: ${habit.completedToday}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Complete pill checkmark
                IconButton(
                    onClick = onCompleteClick,
                    enabled = !isDone,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDone) Color(0xFF22C55E)
                            else Color.Transparent
                        )
                        .border(
                            width = if (isDone) 0.dp else 2.dp,
                            color = if (isDone) Color.Transparent else Color(0xFFE2E8F0),
                            shape = CircleShape
                        )
                        .nameCheckTestTag("complete_button_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Записать прогресс",
                        tint = if (isDone) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete Action button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .nameCheckTestTag("delete_button_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить привычку",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Inline custom Modifier to make writing test tags easy
fun Modifier.nameCheckTestTag(tag: String) = this.testTag(tag)

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    MaterialTheme {
        HabitCard(
            habit = Habit(1, "Drink Water", "Drink 8 cups of water per day", 8, 3),
            onCompleteClick = {},
            onDeleteClick = {},
            onCardClick = {}
        )
    }
}
