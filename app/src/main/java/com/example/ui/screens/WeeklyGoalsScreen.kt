package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryItem
import com.example.ui.RiseViewModel
import com.example.ui.components.WeeklyProgressChartSection
import com.example.ui.theme.CalmCardBorder
import com.example.ui.theme.CalmCardSurface
import com.example.ui.theme.CalmCardSurfaceWarm
import com.example.ui.theme.EmeraldSuccess
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
import com.example.ui.theme.SunriseTwilight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun WeeklyGoalsScreen(
    viewModel: RiseViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val weeklyGoals by viewModel.weeklyGoals.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val weeklyReport by viewModel.weeklyReport.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Soft warm serif header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(SunriseTwilight.copy(alpha = 0.7f), NavyBackground)
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "WEEKLY REFLECTION",
                            style = MaterialTheme.typography.labelMedium,
                            color = PeachSoft,
                            letterSpacing = 1.4.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your Journey",
                            fontFamily = SerifHeadingFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Steady small steps shape your week.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSilver
                        )
                    }
                }
            }

            // 1. EMBEDDED WEEKLY PROGRESS CHART
            item {
                WeeklyProgressChartSection(
                    report = weeklyReport,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // 2. PERFECT WEEK BOUNTY CARD
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(CalmCardSurfaceWarm, CalmCardSurface)))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏆", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "PERFECT WEEK BOUNTY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PeachSoft,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "+250 🪙 Coins & +500 XP",
                                fontFamily = SerifHeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = GoldBright
                            )
                        }
                    }
                }
            }

            // 3. WEEKLY FOCUS GOALS HEADER
            item {
                Text(
                    text = "Weekly Focus Goals (${weeklyGoals.size}/5)",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextWhite,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (weeklyGoals.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌱", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No weekly goals set yet",
                                fontFamily = SerifHeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Create 3 to 5 gentle weekly intentions to link your daily steps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(weeklyGoals, key = { it.id }) { goal ->
                    val linkedTasks = todayTasks.filter { it.weeklyGoalId == goal.id }
                    val completedCount = linkedTasks.count { it.isCompleted }
                    val progress = if (goal.targetCount > 0) (completedCount.toFloat() / goal.targetCount).coerceIn(0f, 1f) else 0f
                    val goalColor = remember(goal.colorHex) {
                        try {
                            Color(android.graphics.Color.parseColor(goal.colorHex))
                        } catch (e: Exception) {
                            GoldPrimary
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .border(1.dp, goalColor.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(goalColor.copy(alpha = 0.18f))
                                    ) {
                                        Text(goal.iconName, fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "${goal.categoryName} • $completedCount of ${goal.targetCount} linked",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSilver
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteWeeklyGoal(goal) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete Goal",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = goalColor,
                                trackColor = NavySurfaceElevated,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (completedCount >= goal.targetCount) "Goal Achieved" else "${goal.targetCount - completedCount} more steps",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (completedCount >= goal.targetCount) EmeraldSuccess else TextMuted
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = goalColor
                                )
                            }
                        }
                    }
                }
            }

            // One clear primary button at the bottom of Journey screen
            item {
                Button(
                    onClick = { showAddGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(54.dp)
                        .testTag("add_weekly_goal_fab")
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Set a New Weekly Goal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAddGoalDialog) {
        AddWeeklyGoalWithCategoryDialog(
            categories = categories,
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, targetCount, category ->
                viewModel.addWeeklyGoal(title, targetCount, category)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun AddWeeklyGoalWithCategoryDialog(
    categories: List<CategoryItem>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, targetCount: Int, category: CategoryItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetCount by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿")) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PeachSoft.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "New Weekly Goal",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = GoldBright
                )
                Text(
                    text = "Set a gentle weekly intention (aim for 3–5 big priorities).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Read 1 book / Study 5 lectures", color = TextMuted) },
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

                Spacer(modifier = Modifier.height(14.dp))

                Text("Target completions this week: $targetCount", style = MaterialTheme.typography.labelMedium, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 4, 5, 7).forEach { num ->
                        Surface(
                            onClick = { targetCount = num },
                            shape = RoundedCornerShape(14.dp),
                            color = if (targetCount == num) GoldPrimary else NavySurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$num",
                                color = if (targetCount == num) NavyDarkest else TextWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                                onConfirm(title, targetCount, selectedCategory)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Save Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
