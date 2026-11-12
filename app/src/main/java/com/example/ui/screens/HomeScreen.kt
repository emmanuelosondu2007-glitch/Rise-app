package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.WeeklyGoal
import com.example.ui.RiseViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    viewModel: RiseViewModel,
    onNavigateWeeklyGoals: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val weeklyGoals by viewModel.weeklyGoals.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val quoteOfTheDay by viewModel.quoteOfTheDay.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val showMaxLimitDialog by viewModel.showMaxTaskLimitDialog.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    val isBusyDay = userProfile?.busyDayMode == true

    // Filter tasks based on Busy Day Mode and Category Filter
    val filteredTasks = todayTasks.filter { task ->
        val matchesBusy = if (isBusyDay) (task.isEssential || task.isRoutine) else true
        val matchesCat = if (selectedCategory != null) task.categoryName == selectedCategory else true
        matchesBusy && matchesCat
    }

    val routineTasks = filteredTasks.filter { it.isRoutine }
    val customTasks = filteredTasks.filter { !it.isRoutine }
    val customTaskCountTotal = todayTasks.count { !it.isRoutine }

    val completedCount = filteredTasks.count { it.isCompleted }
    val totalCount = filteredTasks.size
    val progressFloat = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressFloat, label = "progress")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP GAMIFICATION BAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level Badge
                    Surface(
                        onClick = onNavigateProfile,
                        shape = RoundedCornerShape(16.dp),
                        color = NavySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("level_badge_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary)
                            ) {
                                Text(
                                    text = "${userProfile?.level ?: 1}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = NavyDarkest
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = (userProfile?.levelTitle ?: "Starter").uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Streak & Shield
                    Surface(
                        onClick = onNavigateProfile,
                        shape = RoundedCornerShape(16.dp),
                        color = NavySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("streak_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Daily Streak",
                                tint = FlameOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.currentStreak ?: 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FlameYellow
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Streak Shield",
                                tint = if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Coins Balance
                    Surface(
                        onClick = onNavigateShop,
                        shape = RoundedCornerShape(16.dp),
                        color = NavySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("coins_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("🪙", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${userProfile?.coins ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = GoldBright
                            )
                        }
                    }
                }
            }

            // 2. HABIT TRAINER & BUSY DAY BAR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Habit streak tracker
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurfaceElevated
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("📋", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "List Master Streak: ${userProfile?.listCompletionStreak ?: 0} Days",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                        }
                    }

                    // BUSY DAY TOGGLE BUTTON
                    Surface(
                        onClick = { viewModel.toggleBusyDayMode() },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isBusyDay) FlameOrange else NavySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isBusyDay) FlameYellow else NavySurfaceBorder
                        ),
                        modifier = Modifier.testTag("busy_day_toggle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (isBusyDay) NavyDarkest else FlameYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBusyDay) "Busy Day: ON" else "Busy Day",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isBusyDay) NavyDarkest else TextSilver
                            )
                        }
                    }
                }
            }

            // 3. MORNING ALARM & BRIEFING CARD (HERO FEATURE)
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(GoldPrimary, FlameOrange)),
                            RoundedCornerShape(22.dp)
                        )
                        .testTag("morning_alarm_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MORNING ALARM",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        letterSpacing = 1.5.sp
                                    )
                                    val hour = userProfile?.alarmHour ?: 7
                                    val minute = userProfile?.alarmMinute ?: 0
                                    val timeStr = String.format("%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, if (hour >= 12) "PM" else "AM")
                                    Text(
                                        text = "$timeStr • Loud Real Alarm",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextWhite
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (isSpeaking) {
                                        viewModel.ttsManager.stop()
                                    } else {
                                        viewModel.speakMorningBriefing()
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NavySurfaceElevated)
                                    .testTag("home_tts_speak_button")
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Speak Briefing",
                                    tint = if (isSpeaking) FlameOrange else GoldPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quote & Today's Question
                        quoteOfTheDay?.let { quote ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NavySurfaceElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "“${quote.text}” — ${quote.author}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Focus Question: ${userProfile?.morningWinQuestion}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FlameYellow,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.testAlarmNow() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = NavyDarkest
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("test_alarm_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test Alarm (Loud)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = onNavigateProfile,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavySurfaceElevated,
                                    contentColor = TextWhite
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Voice & Settings",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. CATEGORY FILTER CHIPS
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        if (selectedCategory != null) {
                            Text(
                                text = "Clear filter",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                modifier = Modifier.clickable { viewModel.setCategoryFilter(null) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Surface(
                                onClick = { viewModel.setCategoryFilter(null) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedCategory == null) GoldPrimary else NavySurfaceElevated
                            ) {
                                Text(
                                    text = "All",
                                    color = if (selectedCategory == null) NavyDarkest else TextSilver,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat.name
                            val catColor = try {
                                Color(android.graphics.Color.parseColor(cat.colorHex))
                            } catch (e: Exception) {
                                GoldPrimary
                            }

                            Surface(
                                onClick = { viewModel.setCategoryFilter(if (isSelected) null else cat.name) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) catColor else NavySurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, catColor.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(cat.iconEmoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = cat.name,
                                        color = if (isSelected) NavyDarkest else TextWhite,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. DAILY ROUTINE SECTION (RECURRING EVERY DAY)
            if (routineTasks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Routine (${routineTasks.count { it.isCompleted }}/${routineTasks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }

                items(routineTasks, key = { it.id }) { task ->
                    DailyTaskCard(
                        task = task,
                        linkedGoal = null,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            // 6. TODAY'S TASKS LIST HEADER (5 TASKS MAX)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Tasks (${customTaskCountTotal}/5 max)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Capped at 5 so your day is always winnable",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Surface(
                        onClick = { showAddTaskDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = GoldPrimary,
                        modifier = Modifier.testTag("quick_add_goal_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = NavyDarkest,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Task",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = NavyDarkest
                            )
                        }
                    }
                }
            }

            // Custom Tasks items
            if (customTasks.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎯", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBusyDay) "Busy Day Mode active" else "No extra tasks for today",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = if (isBusyDay) "Only essential routine tasks are shown." else "Tap '+ Add Task' above (up to 5 max) to win your day!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSilver,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(customTasks, key = { it.id }) { task ->
                    val linkedGoal = weeklyGoals.find { it.id == task.weeklyGoalId }
                    DailyTaskCard(
                        task = task,
                        linkedGoal = linkedGoal,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            // 7. SUNDAY NIGHT CHECK-IN CARD & EVENING REFLECTION BANNER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openEveningCheckIn() }
                            .border(1.dp, FlameOrange.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .testTag("evening_checkin_banner")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🌙 Evening Check-in", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FlameYellow)
                            Text("Score today & plan", style = MaterialTheme.typography.bodySmall, color = TextSilver)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openSundayCheckIn() }
                            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .testTag("sunday_checkin_banner")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🧭 Sunday Review", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GoldBright)
                            Text("+200 XP Bounty", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
                        }
                    }
                }
            }
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AddTaskWithCategoriesDialog(
                weeklyGoals = weeklyGoals,
                categories = categories,
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { title, notes, weeklyGoalId, category, priority, isEssential ->
                    viewModel.addTask(title, notes, weeklyGoalId, category, priority, isEssential)
                    showAddTaskDialog = false
                }
            )
        }

        // Gentle 5-Task Limit Dialog
        if (showMaxLimitDialog) {
            MaxTaskLimitGentleDialog(onDismiss = { viewModel.dismissMaxTaskLimitDialog() })
        }
    }
}

@Composable
fun DailyTaskCard(
    task: DailyTask,
    linkedGoal: WeeklyGoal?,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = task.isCompleted
    val animBgColor by animateColorAsState(
        targetValue = if (isCompleted) NavySurfaceElevated.copy(alpha = 0.7f) else NavySurface,
        label = "taskBg"
    )

    val catColor = try {
        Color(android.graphics.Color.parseColor(task.categoryColor))
    } catch (e: Exception) {
        GoldPrimary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .border(
                1.dp,
                if (isCompleted) EmeraldSuccess.copy(alpha = 0.4f) else NavySurfaceBorder,
                RoundedCornerShape(16.dp)
            )
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) EmeraldSuccess else NavySurfaceElevated)
                        .border(2.dp, if (isCompleted) EmeraldSuccess else GoldMuted, CircleShape)
                        .clickable { onToggle() }
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = NavyDarkest,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                            color = if (isCompleted) TextMuted else TextWhite,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Category chip
                        Surface(
                            color = catColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = task.categoryName,
                                color = catColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Moved tag
                        if (task.isMovedFromPreviousDay) {
                            Surface(
                                color = FlameOrange.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Moved",
                                    color = FlameYellow,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Routine badge
                        if (task.isRoutine) {
                            Surface(
                                color = GoldPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Routine",
                                    color = GoldBright,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = GoldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+${task.xpReward} XP",
                        color = GoldBright,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                if (!task.isRoutine) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskWithCategoriesDialog(
    weeklyGoals: List<WeeklyGoal>,
    categories: List<CategoryItem>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, notes: String, weeklyGoalId: Long?, category: CategoryItem, priority: Int, isEssential: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿")) }
    var priority by remember { mutableIntStateOf(1) }
    var isEssential by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Add Daily Goal 🎯",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldPrimary
                )
                Text(
                    text = "Limit: 5 tasks max per day to keep your list winnable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What will you conquer?", color = TextMuted) },
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

                // Category Selector
                Text("Select Category:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = selectedCategory.id == cat.id || selectedCategory.name == cat.name
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) GoldPrimary else NavySurfaceElevated
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                // Essential for Busy Day checkbox
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mark as essential (visible on Busy Days)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceElevated, contentColor = TextSilver),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, notes, selectedGoalId, selectedCategory, priority, isEssential)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Add Task ⚡", fontWeight = FontWeight.Black)
                    }
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
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🛡️", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Your Day is Full!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rise caps each day at 5 tasks (plus your daily routine) so your list is always winnable! Please move this task to tomorrow or complete an active task first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSilver,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Got It, Keep It Winnable! ⚡", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
