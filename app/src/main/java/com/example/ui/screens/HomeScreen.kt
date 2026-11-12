package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyGoal
import com.example.ui.RiseViewModel
import com.example.ui.components.RitualType
import com.example.ui.components.TimeOfDaySkyHeader
import com.example.ui.theme.CalmCardBorder
import com.example.ui.theme.CalmCardSurface
import com.example.ui.theme.CalmCardSurfaceWarm
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.PeachSoft
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.SerifHeadingFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.util.Calendar

private enum class DayTab { TODAY, TOMORROW }

@Composable
fun HomeScreen(
    viewModel: RiseViewModel,
    onStartRitual: (RitualType, List<DailyTask>) -> Unit,
    onNavigateJourney: () -> Unit,
    onNavigateMe: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val tomorrowTasks by viewModel.tomorrowTasks.collectAsState()
    val weeklyGoals by viewModel.weeklyGoals.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val quoteOfTheDay by viewModel.quoteOfTheDay.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val showMaxLimitDialog by viewModel.showMaxTaskLimitDialog.collectAsState()
    val alarmStatus by viewModel.alarmStatus.collectAsState()

    var selectedDayTab by remember { mutableStateOf(DayTab.TODAY) }
    var showStatsDetailsDialog by remember { mutableStateOf(false) }
    var addTaskTargetDate by remember { mutableStateOf<String?>(null) }
    var editingTask by remember { mutableStateOf<DailyTask?>(null) }
    var movingTask by remember { mutableStateOf<DailyTask?>(null) }

    val isBusyDay = userProfile?.busyDayMode == true
    val isSunday = remember { Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY }

    // Apply Busy Day filtering cleanly while preserving ritual classification
    val visibleTodayTasks = remember(todayTasks, isBusyDay) {
        if (isBusyDay) todayTasks.filter { it.isEssential || it.isRoutine } else todayTasks
    }

    // Morning Ritual: Devotion, After lectures, plus Tuesday Sermon 1 / Friday Sermon 2 on their day only
    val morningTasks = remember(visibleTodayTasks) {
        visibleTodayTasks.filter { task ->
            (task.isRoutine && (task.title.contains("Devotion", ignoreCase = true) ||
                    task.title.contains("After lectures", ignoreCase = true) ||
                    task.title.contains("waking", ignoreCase = true))) ||
                    task.scheduledDayOfWeek == Calendar.TUESDAY ||
                    task.scheduledDayOfWeek == Calendar.FRIDAY
        }
    }

    // Evening Ritual: Read 10 pages, Submit daily report, plus Saturday voice note on Saturday only
    val eveningTasks = remember(visibleTodayTasks) {
        visibleTodayTasks.filter { task ->
            (task.isRoutine && (task.title.contains("10 pages", ignoreCase = true) ||
                    task.title.contains("daily report", ignoreCase = true) ||
                    task.title.contains("Evening", ignoreCase = true) ||
                    task.title.contains("Night", ignoreCase = true))) ||
                    task.scheduledDayOfWeek == Calendar.SATURDAY
        }
    }

    // My Day: Personal tasks only (max 5; never routine or weekly tasks)
    val myDayTasks = remember(visibleTodayTasks) {
        visibleTodayTasks.filter { !it.isRoutine && it.scheduledDayOfWeek == null }
    }
    val todayPersonalCount = remember(todayTasks) {
        todayTasks.count { !it.isRoutine && it.scheduledDayOfWeek == null }
    }
    val tomorrowPersonalCount = remember(tomorrowTasks) {
        tomorrowTasks.count { !it.isRoutine && it.scheduledDayOfWeek == null }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. TIME-OF-DAY SKY GRADIENT HERO HEADER
            item {
                TimeOfDaySkyHeader(
                    userName = userProfile?.userName ?: "Emmanuel",
                    quote = quoteOfTheDay,
                    isSpeaking = isSpeaking,
                    onToggleSpeak = {
                        if (isSpeaking) viewModel.ttsManager.stop() else viewModel.speakMorningBriefing()
                    },
                    alarmHour = userProfile?.alarmHour ?: 7,
                    alarmMinute = userProfile?.alarmMinute ?: 0,
                    alarmStatus = alarmStatus
                )
            }

            // 2. SINGLE COMPACT STATS ROW (Streak, Level, Coins) — Tap for details
            item {
                CompactStatsRow(
                    streak = userProfile?.currentStreak ?: 1,
                    level = userProfile?.level ?: 1,
                    coins = userProfile?.coins ?: 0,
                    onClick = { showStatsDetailsDialog = true },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // 3. "TODAY | TOMORROW" SWITCH + BUSY DAY PILL
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = CalmCardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            DaySwitchPill(
                                label = "Today",
                                selected = selectedDayTab == DayTab.TODAY,
                                onClick = { selectedDayTab = DayTab.TODAY },
                                testTag = "tab_today_switch"
                            )
                            DaySwitchPill(
                                label = "Tomorrow",
                                selected = selectedDayTab == DayTab.TOMORROW,
                                onClick = { selectedDayTab = DayTab.TOMORROW },
                                testTag = "tab_tomorrow_switch"
                            )
                        }
                    }

                    // Gentle Busy Day Toggle Pill
                    Surface(
                        onClick = { viewModel.toggleBusyDayMode() },
                        shape = RoundedCornerShape(50),
                        color = if (isBusyDay) PeachWarm.copy(alpha = 0.2f) else CalmCardSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isBusyDay) PeachWarm else CalmCardBorder
                        ),
                        modifier = Modifier.testTag("busy_day_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Spa,
                                contentDescription = "Busy Day Mode",
                                tint = if (isBusyDay) PeachWarm else TextSilver,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBusyDay) "Busy Day On" else "Busy Day",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isBusyDay) PeachSoft else TextSilver,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 4. ANIMATED SWITCH BETWEEN TODAY RITUALS AND TOMORROW PLANNER
            item {
                AnimatedContent(
                    targetState = selectedDayTab,
                    transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(180)) },
                    label = "todayTomorrowSwitch"
                ) { tab ->
                    if (tab == DayTab.TODAY) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // RITUAL CARD 1: MORNING RITUAL
                            val morningDone = morningTasks.count { it.isCompleted }
                            val morningTotal = morningTasks.size
                            RitualCard(
                                title = "Morning Ritual",
                                subtitle = "Devotion, prayer & morning foundations",
                                emoji = "🌅",
                                accentColor = PeachWarm,
                                completedCount = morningDone,
                                totalCount = morningTotal,
                                tasks = morningTasks,
                                initiallyExpanded = morningDone < morningTotal,
                                onStartClick = { onStartRitual(RitualType.MORNING, morningTasks) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onEditTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) editingTask = it },
                                onMoveTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) movingTask = it },
                                onDeleteTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) viewModel.deleteTask(it) },
                                testTag = "morning_ritual_card"
                            )

                            // RITUAL CARD 2: MY DAY (Maximum 5 personal tasks)
                            val myDayDone = myDayTasks.count { it.isCompleted }
                            val myDayTotal = myDayTasks.size
                            RitualCard(
                                title = "My Day",
                                subtitle = "$todayPersonalCount / 5 personal tasks",
                                emoji = "☀️",
                                accentColor = GoldPrimary,
                                completedCount = myDayDone,
                                totalCount = myDayTotal,
                                tasks = myDayTasks,
                                initiallyExpanded = true,
                                emptyMessage = "Keep your day calm and winnable — add up to 5 priorities.",
                                headerActionLabel = "+ Add",
                                onHeaderAction = {
                                    if (todayPersonalCount >= 5) {
                                        viewModel.triggerMaxTaskLimitDialog()
                                    } else {
                                        addTaskTargetDate = viewModel.todayDateString
                                    }
                                },
                                onStartClick = { onStartRitual(RitualType.MY_DAY, myDayTasks) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onEditTask = { editingTask = it },
                                onMoveTask = { movingTask = it },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                testTag = "my_day_ritual_card"
                            )

                            // RITUAL CARD 3: EVENING RITUAL (Includes 10 pages, daily report, Saturday voice note, Evening Check-In)
                            val eveningCheckInCompleted = userProfile?.lastMorningAudioGeneratedForDate == viewModel.tomorrowDateString
                            val eveningDone = eveningTasks.count { it.isCompleted } + (if (eveningCheckInCompleted) 1 else 0)
                            val eveningTotal = eveningTasks.size + 1
                            RitualCard(
                                title = "Evening Ritual",
                                subtitle = "10 pages, daily report & evening check-in",
                                emoji = "🌙",
                                accentColor = Color(0xFF9F8FEF),
                                completedCount = eveningDone,
                                totalCount = eveningTotal,
                                tasks = eveningTasks,
                                initiallyExpanded = morningDone >= morningTotal,
                                extraFooterLabel = "Evening Check-in & Reflection",
                                extraFooterCompleted = eveningCheckInCompleted,
                                onExtraFooterClick = { viewModel.openEveningCheckIn() },
                                onStartClick = { onStartRitual(RitualType.EVENING, eveningTasks) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onEditTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) editingTask = it },
                                onMoveTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) movingTask = it },
                                onDeleteTask = { if (!it.isRoutine && it.scheduledDayOfWeek == null) viewModel.deleteTask(it) },
                                testTag = "evening_ritual_card"
                            )

                            // Gentle Sunday Review whisper when it's Sunday
                            if (isSunday) {
                                Surface(
                                    onClick = { viewModel.openSundayCheckIn() },
                                    shape = RoundedCornerShape(24.dp),
                                    color = CalmCardSurfaceWarm,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sunday_checkin_banner")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Sunday Night Check-in",
                                                fontFamily = SerifHeadingFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = GoldBright
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Reflect on your week & claim +200 XP",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSilver
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Rounded.ChevronRight,
                                            contentDescription = "Open Sunday Check-in",
                                            tint = GoldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // TOMORROW VIEW: Plan, edit, delete, and move tasks cleanly
                        TomorrowPlannerSection(
                            tomorrowTasks = tomorrowTasks,
                            tomorrowPersonalCount = tomorrowPersonalCount,
                            onAddClick = { addTaskTargetDate = viewModel.tomorrowDateString },
                            onEditTask = { editingTask = it },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onMoveTask = { movingTask = it }
                        )
                    }
                }
            }
        }
    }

    // Compact Stats Details Dialog (triggered by tapping the Streak/Level/Coins row)
    if (showStatsDetailsDialog) {
        StatsDetailsDialog(
            profile = userProfile,
            onDismiss = { showStatsDetailsDialog = false },
            onToggleBusyDay = { viewModel.toggleBusyDayMode() },
            onOpenEveningCheckIn = {
                showStatsDetailsDialog = false
                viewModel.openEveningCheckIn()
            },
            onOpenSundayCheckIn = {
                showStatsDetailsDialog = false
                viewModel.openSundayCheckIn()
            },
            onNavigateMe = {
                showStatsDetailsDialog = false
                onNavigateMe()
            }
        )
    }

    // Add Task Dialog (works for Today or Tomorrow)
    addTaskTargetDate?.let { targetDate ->
        AddOrEditTaskDialog(
            initialTask = null,
            targetLabel = if (targetDate == viewModel.tomorrowDateString) "Tomorrow" else "Today",
            weeklyGoals = weeklyGoals,
            categories = categories,
            onDismiss = { addTaskTargetDate = null },
            onConfirm = { title, notes, weeklyGoalId, category, priority, isEssential ->
                viewModel.addTaskForDate(
                    date = targetDate,
                    title = title,
                    notes = notes,
                    weeklyGoalId = weeklyGoalId,
                    category = category,
                    priority = priority,
                    isEssential = isEssential
                )
                addTaskTargetDate = null
            }
        )
    }

    // Edit Task Dialog
    editingTask?.let { taskToEdit ->
        AddOrEditTaskDialog(
            initialTask = taskToEdit,
            targetLabel = if (taskToEdit.date == viewModel.tomorrowDateString) "Tomorrow" else "Today",
            weeklyGoals = weeklyGoals,
            categories = categories,
            onDismiss = { editingTask = null },
            onConfirm = { title, notes, weeklyGoalId, category, priority, isEssential ->
                viewModel.updateTaskDetails(
                    task = taskToEdit,
                    title = title,
                    notes = notes,
                    category = category,
                    priority = priority,
                    isEssential = isEssential,
                    weeklyGoalId = weeklyGoalId
                )
                editingTask = null
            }
        )
    }

    // Move Task Dialog (Move to Today, Tomorrow, or Day After Tomorrow)
    movingTask?.let { taskToMove ->
        MoveTaskDialog(
            task = taskToMove,
            todayDate = viewModel.todayDateString,
            tomorrowDate = viewModel.tomorrowDateString,
            dayAfterDate = viewModel.dayAfterTomorrowDateString,
            onDismiss = { movingTask = null },
            onMoveTo = { targetDate ->
                viewModel.moveTaskToDate(taskToMove, targetDate)
                movingTask = null
            }
        )
    }

    // Gentle 5-Task Limit Dialog
    if (showMaxLimitDialog) {
        MaxTaskLimitGentleDialog(onDismiss = { viewModel.dismissMaxTaskLimitDialog() })
    }
}

@Composable
private fun CompactStatsRow(
    streak: Int,
    level: Int,
    coins: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = CalmCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("compact_stats_row")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${streak}d streak",
                    style = MaterialTheme.typography.labelLarge,
                    color = PeachSoft
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(CalmCardBorder)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lv. $level",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(CalmCardBorder)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$coins",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldBright
                )
            }
        }
    }
}

@Composable
private fun DaySwitchPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) GoldPrimary else Color.Transparent,
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) NavyDarkest else TextSilver,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun RitualCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color,
    completedCount: Int,
    totalCount: Int,
    tasks: List<DailyTask>,
    initiallyExpanded: Boolean,
    emptyMessage: String = "Everything is peacefully aligned here.",
    headerActionLabel: String? = null,
    onHeaderAction: (() -> Unit)? = null,
    extraFooterLabel: String? = null,
    extraFooterCompleted: Boolean = false,
    onExtraFooterClick: (() -> Unit)? = null,
    onStartClick: () -> Unit,
    onToggleTask: (DailyTask) -> Unit,
    onEditTask: (DailyTask) -> Unit,
    onMoveTask: (DailyTask) -> Unit,
    onDeleteTask: (DailyTask) -> Unit,
    testTag: String
) {
    var expanded by remember(initiallyExpanded) { mutableStateOf(initiallyExpanded) }
    val progress = if (totalCount > 0) (completedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(550),
        label = "ritualRingProgress"
    )
    val allDone = totalCount > 0 && completedCount >= totalCount

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CalmCardSurfaceWarm, CalmCardSurface)
                )
            )
            .border(
                1.dp,
                if (allDone) EmeraldSuccess.copy(alpha = 0.45f) else accentColor.copy(alpha = 0.28f),
                RoundedCornerShape(24.dp)
            )
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded }
                ) {
                    // Progress Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 5.dp.toPx()
                            drawArc(
                                color = Color.White.copy(alpha = 0.1f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (allDone) EmeraldSuccess else accentColor,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text(text = emoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = title,
                            fontFamily = SerifHeadingFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSilver
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (allDone) "Completed ($completedCount/$totalCount)"
                            else "$completedCount of $totalCount done",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (allDone) EmeraldSuccess else accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Start Button (launches full-screen guided Ritual Mode)
                Button(
                    onClick = onStartClick,
                    enabled = tasks.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allDone) NavySurfaceElevated else GoldPrimary,
                        contentColor = if (allDone) TextWhite else NavyDarkest,
                        disabledContainerColor = NavySurfaceElevated.copy(alpha = 0.5f),
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("${testTag}_start_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (allDone) "Review" else "Start",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expand/Collapse & Header Action Row
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (expanded) "Hide tasks" else "Show tasks",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expanded) "Hide steps" else "View ${tasks.size} steps",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                if (headerActionLabel != null && onHeaderAction != null) {
                    Surface(
                        onClick = onHeaderAction,
                        shape = RoundedCornerShape(50),
                        color = accentColor.copy(alpha = 0.16f),
                        modifier = Modifier.testTag("add_personal_task_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add Task",
                                tint = accentColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = headerActionLabel.removePrefix("+ "),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tasks.isEmpty()) {
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        tasks.forEach { task ->
                            CompactRitualTaskRow(
                                task = task,
                                allowModify = !task.isRoutine && task.scheduledDayOfWeek == null,
                                onToggle = { onToggleTask(task) },
                                onEdit = { onEditTask(task) },
                                onMove = { onMoveTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }

                    if (extraFooterLabel != null && onExtraFooterClick != null) {
                        Surface(
                            onClick = onExtraFooterClick,
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurfaceElevated.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (extraFooterCompleted) EmeraldSuccess.copy(alpha = 0.4f) else PeachSoft.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("evening_checkin_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.NightsStay,
                                        contentDescription = null,
                                        tint = if (extraFooterCompleted) EmeraldSuccess else PeachSoft,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = extraFooterLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (extraFooterCompleted) EmeraldSuccess else TextWhite
                                    )
                                }
                                Icon(
                                    imageVector = if (extraFooterCompleted) Icons.Rounded.Check else Icons.Rounded.ChevronRight,
                                    contentDescription = null,
                                    tint = if (extraFooterCompleted) EmeraldSuccess else TextSilver,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactRitualTaskRow(
    task: DailyTask,
    allowModify: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    val catColor = remember(task.categoryColor) {
        try {
            Color(android.graphics.Color.parseColor(task.categoryColor))
        } catch (e: Exception) {
            GoldPrimary
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (task.isCompleted) NavyDarkest.copy(alpha = 0.4f) else NavySurfaceElevated.copy(alpha = 0.65f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) EmeraldSuccess else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (task.isCompleted) EmeraldSuccess else catColor.copy(alpha = 0.7f),
                        CircleShape
                    )
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Done",
                        tint = NavyDarkest,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (task.isCompleted) TextMuted else TextWhite,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 3.dp)
                ) {
                    Text(
                        text = task.categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = catColor
                    )
                    if (task.isMovedFromPreviousDay) {
                        Surface(
                            color = PeachWarm.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "moved",
                                style = MaterialTheme.typography.labelSmall,
                                color = PeachWarm,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    if (task.scheduledDayOfWeek != null) {
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "weekly",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBright,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            if (allowModify) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit task",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onMove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.DriveFileMove,
                        contentDescription = "Move task",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete task",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TomorrowPlannerSection(
    tomorrowTasks: List<DailyTask>,
    tomorrowPersonalCount: Int,
    onAddClick: () -> Unit,
    onEditTask: (DailyTask) -> Unit,
    onDeleteTask: (DailyTask) -> Unit,
    onMoveTask: (DailyTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tomorrow's Plan",
                            fontFamily = SerifHeadingFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$tomorrowPersonalCount / 5 personal tasks planned",
                            style = MaterialTheme.typography.bodySmall,
                            color = PeachSoft
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tomorrowTasks.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NavySurfaceElevated.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌤️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "A clear canvas for tomorrow",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add up to 5 priorities so tomorrow morning begins with calm intention.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        tomorrowTasks.forEach { task ->
                            val catColor = remember(task.categoryColor) {
                                try {
                                    Color(android.graphics.Color.parseColor(task.categoryColor))
                                } catch (e: Exception) {
                                    GoldPrimary
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = NavySurfaceElevated.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditTask(task) }
                                    .testTag("tomorrow_task_${task.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite
                                        )
                                        if (task.notes.isNotBlank()) {
                                            Text(
                                                text = task.notes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSilver,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${task.categoryName} • +${task.xpReward} XP",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = catColor
                                        )
                                    }

                                    IconButton(onClick = { onEditTask(task) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "Edit Tomorrow Task",
                                            tint = GoldBright,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { onMoveTask(task) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.DriveFileMove,
                                            contentDescription = "Move Tomorrow Task",
                                            tint = PeachWarm,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { onDeleteTask(task) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.DeleteOutline,
                                            contentDescription = "Delete Tomorrow Task",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // One main button in Tomorrow view
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_tomorrow_task_button")
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Plan a Task for Tomorrow", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatsDetailsDialog(
    profile: UserProfile?,
    onDismiss: () -> Unit,
    onToggleBusyDay: () -> Unit,
    onOpenEveningCheckIn: () -> Unit,
    onOpenSundayCheckIn: () -> Unit,
    onNavigateMe: () -> Unit
) {
    val level = profile?.level ?: 1
    val xp = profile?.xp ?: 0
    val (minXp, maxXp) = when (level) {
        1 -> Pair(0, 150)
        2 -> Pair(150, 350)
        3 -> Pair(350, 700)
        4 -> Pair(700, 1200)
        5 -> Pair(1200, 2000)
        6 -> Pair(2000, 3000)
        7 -> Pair(3000, 4500)
        else -> Pair(4500, 6000)
    }
    val levelProgress = ((xp - minXp).toFloat() / (maxXp - minXp).coerceAtLeast(1)).coerceIn(0f, 1f)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PeachSoft.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Your Progress",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Level $level • ${profile?.levelTitle ?: "Starter"}",
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = GoldPrimary,
                    trackColor = NavySurfaceElevated,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$xp / $maxXp XP to next level • ${profile?.coins ?: 0} 🪙 coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Daily Streak", style = MaterialTheme.typography.bodyMedium, color = TextSilver)
                            Text("🔥 ${profile?.currentStreak ?: 1} days (Best: ${profile?.highestStreak ?: 1})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PeachSoft)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("To-Do List Streak", style = MaterialTheme.typography.bodyMedium, color = TextSilver)
                            Text("📋 ${profile?.listCompletionStreak ?: 0} days", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = GoldBright)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Weekly Streak Shield", style = MaterialTheme.typography.bodyMedium, color = TextSilver)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Shield, contentDescription = null, tint = if (profile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (profile?.streakShieldAvailable == true) "Ready" else "Used",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (profile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Busy Day Mode", style = MaterialTheme.typography.titleSmall, color = TextWhite)
                        Text("Show only essentials & keep streak", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                    Switch(
                        checked = profile?.busyDayMode == true,
                        onCheckedChange = { onToggleBusyDay() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NavyDarkest, checkedTrackColor = PeachWarm)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onOpenEveningCheckIn,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Evening Check-in", fontSize = 12.sp, color = PeachSoft)
                    }
                    OutlinedButton(
                        onClick = onOpenSundayCheckIn,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sunday Review", fontSize = 12.sp, color = GoldBright)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onNavigateMe,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Visit Rewards Shop & Me", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddOrEditTaskDialog(
    initialTask: DailyTask?,
    targetLabel: String,
    weeklyGoals: List<WeeklyGoal>,
    categories: List<CategoryItem>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, notes: String, weeklyGoalId: Long?, category: CategoryItem, priority: Int, isEssential: Boolean) -> Unit
) {
    val defaultCat = categories.find { it.name == initialTask?.categoryName }
        ?: categories.firstOrNull()
        ?: CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿")

    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }
    var selectedGoalId by remember { mutableStateOf(initialTask?.weeklyGoalId) }
    var selectedCategory by remember { mutableStateOf(defaultCat) }
    var priority by remember { mutableIntStateOf(initialTask?.priority ?: 2) }
    var isEssential by remember { mutableStateOf(initialTask?.isEssential ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PeachSoft.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = if (initialTask == null) "Plan for $targetLabel" else "Edit Task",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = GoldBright
                )
                Text(
                    text = "Up to 5 personal tasks keep your day peaceful and winnable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = NavySurfaceElevated,
                        unfocusedContainerColor = NavySurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = NavySurfaceElevated,
                        unfocusedContainerColor = NavySurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Category:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = selectedCategory.id == cat.id || selectedCategory.name == cat.name
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) GoldPrimary else NavySurfaceElevated
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cat.iconEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat.name,
                                    color = if (isSel) NavyDarkest else TextWhite,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (weeklyGoals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Link to Weekly Goal (optional):", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(weeklyGoals) { goal ->
                            val isSel = selectedGoalId == goal.id
                            Surface(
                                onClick = { selectedGoalId = if (isSel) null else goal.id },
                                shape = RoundedCornerShape(50),
                                color = if (isSel) PeachWarm else NavySurfaceElevated
                            ) {
                                Text(
                                    text = goal.title,
                                    color = if (isSel) NavyDarkest else TextSilver,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isEssential = !isEssential }
                ) {
                    Checkbox(
                        checked = isEssential,
                        onCheckedChange = { isEssential = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldPrimary,
                            checkmarkColor = NavyDarkest,
                            uncheckedColor = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Essential (always shown on Busy Days)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Cancel", color = TextSilver)
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, notes, selectedGoalId, selectedCategory, priority, isEssential)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(if (initialTask == null) "Save" else "Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoveTaskDialog(
    task: DailyTask,
    todayDate: String,
    tomorrowDate: String,
    dayAfterDate: String,
    onDismiss: () -> Unit,
    onMoveTo: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PeachSoft.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Move Task",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (task.date != todayDate) {
                    Button(
                        onClick = { onMoveTo(todayDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Move to Today", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (task.date != tomorrowDate) {
                    Button(
                        onClick = { onMoveTo(tomorrowDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = PeachWarm, contentColor = NavyDarkest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Move to Tomorrow", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = { onMoveTo(dayAfterDate) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Move to Day After Tomorrow", color = TextSilver)
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel", color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun MaxTaskLimitGentleDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PeachWarm, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🌿", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Keep Your Day Winnable",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Each day holds up to 5 personal tasks (not counting your routine or weekly tasks) so you never feel overwhelmed. Move this one to tomorrow instead!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSilver,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
