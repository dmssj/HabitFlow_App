package com.example.feature.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.UiState
import com.example.core.navigation.NavigationContract
import com.example.domain.model.HabitStatistics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitStatisticsScreen(
    viewModel: HabitStatisticsViewModel,
    navigator: NavigationContract,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Результаты и статистика", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.navigateBack() },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("stats_back")
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
                            .testTag("stats_loader")
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
                    StatsContent(stats = state.data)
                }
            }
        }
    }
}

@Composable
fun StatsContent(stats: HabitStatistics) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Card - Total Habits overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Поток ваших привычек",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Отличный прогресс сегодня!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Поток",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Metrics Grid (Streak, Completes Rate, Active Tasks)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                value = "${stats.streakDays} дн.",
                label = "Активная серия",
                icon = Icons.Default.Star,
                iconColor = Color(0xFFFF6D00),
                modifier = Modifier
                    .weight(1f)
                    .testTag("streak_metric")
            )

            MetricCard(
                value = "${stats.averageCompletionRate.toInt()}%",
                label = "Прогресс сегодня",
                icon = Icons.Default.Info,
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .testTag("completion_rate_metric")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                value = "${stats.completedHabitsCount}/${stats.totalHabits}",
                label = "Выполнено сегодня",
                icon = Icons.Default.CheckCircle,
                iconColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                value = "${stats.totalHabits}",
                label = "Всего привычек",
                icon = Icons.Default.Check,
                iconColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        // Custom Weekly Chart Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weekly_chart_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Активность в течение недели",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                WeeklyBarChart(
                    completionsByDay = stats.completionsByDayOfWeek,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    secondaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyBarChart(
    completionsByDay: Map<Int, Int>,
    primaryColor: Color,
    secondaryColor: Color,
    textColor: Color
) {
    val dayLabels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val maxCompletions = (completionsByDay.values.maxOrNull() ?: 1).coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val width = size.width
        val height = size.height

        val labelAreaHeight = 24.dp.toPx()
        val chartAreaHeight = height - labelAreaHeight

        val barCount = 7
        val totalSpacing = width * 0.25f
        val availableWidthForBars = width - totalSpacing
        val barWidth = availableWidthForBars / barCount
        val spacingBetweenBars = totalSpacing / (barCount - 1)

        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(
                (textColor.alpha * 255).toInt(),
                (textColor.red * 255).toInt(),
                (textColor.green * 255).toInt(),
                (textColor.blue * 255).toInt()
            )
            textSize = 11.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        for (i in 0 until barCount) {
            val key = i + 1
            val count = completionsByDay[key] ?: 0

            val relativeHeightRatio = count.toFloat() / maxCompletions.toFloat()
            val barHeight = relativeHeightRatio * chartAreaHeight

            val leftX = i * (barWidth + spacingBetweenBars)
            val topY = chartAreaHeight - barHeight
            val rightX = leftX + barWidth
            val bottomY = chartAreaHeight

            // Draw Bar
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.6f))
                ),
                topLeft = Offset(leftX, topY),
                size = Size(barWidth, barHeight.coerceAtLeast(6.dp.toPx())),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Draw Day Text
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    dayLabels[i],
                    leftX + barWidth / 2f,
                    height - 4.dp.toPx(),
                    textPaint
                )

                // Optional label for count over bar
                if (count > 0) {
                    canvas.nativeCanvas.drawText(
                        count.toString(),
                        leftX + barWidth / 2f,
                        (topY - 6.dp.toPx()).coerceAtLeast(12.dp.toPx()),
                        textPaint
                    )
                }
            }
        }
    }
}
