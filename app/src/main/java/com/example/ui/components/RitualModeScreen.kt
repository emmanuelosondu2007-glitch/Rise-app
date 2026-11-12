package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTask
import com.example.ui.theme.AfternoonSkyTop
import com.example.ui.theme.CalmCardBorder
import com.example.ui.theme.CalmCardSurface
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NightSkyTop
import com.example.ui.theme.PeachSoft
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.SerifHeadingFontFamily
import com.example.ui.theme.SunriseTwilight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class RitualType(val title: String, val emoji: String) {
    MORNING("Morning Ritual", "🌅"),
    MY_DAY("My Day", "☀️"),
    EVENING("Evening Ritual", "🌙")
}

@Composable
fun RitualModeScreen(
    ritualType: RitualType,
    tasks: List<DailyTask>,
    userName: String,
    onCompleteTask: suspend (DailyTask) -> Pair<Int, Int>,
    onPlayGentleSound: () -> Unit,
    onOpenEveningCheckIn: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    val scope = rememberCoroutineScope()
    val pendingTasks = remember(ritualType) {
        val incomplete = tasks.filter { !it.isCompleted }
        if (incomplete.isNotEmpty()) incomplete else tasks
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isCompletedAll by remember { mutableStateOf(pendingTasks.isEmpty()) }
    var sessionXp by remember { mutableIntStateOf(0) }
    var sessionCoins by remember { mutableIntStateOf(0) }
    var completedInSession by remember { mutableIntStateOf(0) }
    var justCheckedCurrent by remember { mutableStateOf(false) }

    // Optional focus timer state
    var selectedMinutes by remember { mutableIntStateOf(5) }
    var remainingSeconds by remember { mutableIntStateOf(5 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showTimerPanel by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, remainingSeconds) {
        if (isTimerRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
            if (remainingSeconds == 0) {
                isTimerRunning = false
                onPlayGentleSound()
            }
        }
    }

    // Reset timer cleanly when switching task
    LaunchedEffect(currentIndex) {
        isTimerRunning = false
        remainingSeconds = selectedMinutes * 60
        showTimerPanel = false
    }

    val backdropBrush = when (ritualType) {
        RitualType.MORNING -> Brush.verticalGradient(
            listOf(SunriseTwilight, Color(0xFF281D3A), NavyBackground, NavyDarkest)
        )
        RitualType.MY_DAY -> Brush.verticalGradient(
            listOf(AfternoonSkyTop, Color(0xFF132244), NavyBackground, NavyDarkest)
        )
        RitualType.EVENING -> Brush.verticalGradient(
            listOf(NightSkyTop, Color(0xFF10193B), NavyBackground, NavyDarkest)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backdropBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ritual_mode_screen")
    ) {
        if (isCompletedAll) {
            // End-of-Ritual Celebration View
            RitualCelebrationView(
                ritualType = ritualType,
                userName = userName,
                completedCount = completedInSession,
                xpEarned = sessionXp,
                coinsEarned = sessionCoins,
                onOpenEveningCheckIn = onOpenEveningCheckIn,
                onFinish = onClose
            )
        } else {
            val currentTask = pendingTasks.getOrNull(currentIndex) ?: return@Box
            val progressFraction = ((currentIndex + (if (justCheckedCurrent) 1 else 0)).toFloat() / pendingTasks.size.coerceAtLeast(1)).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = progressFraction,
                animationSpec = tween(400),
                label = "ritualStepProgress"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("ritual_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Exit Ritual",
                            tint = TextWhite
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${ritualType.emoji} ${ritualType.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = PeachSoft,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Step ${currentIndex + 1} of ${pendingTasks.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    TextButton(
                        onClick = {
                            if (justCheckedCurrent) return@TextButton
                            if (currentIndex + 1 < pendingTasks.size) {
                                currentIndex += 1
                            } else {
                                isCompletedAll = true
                            }
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Skip", color = TextSilver, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = PeachWarm,
                    trackColor = Color.White.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )

                // Main Focus Stage (slides gently between tasks)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentTask,
                        transitionSpec = {
                            (slideInHorizontally(tween(340, easing = FastOutSlowInEasing)) { it / 2 } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(tween(260)) { -it / 2 } + fadeOut(tween(220)))
                        },
                        label = "ritualTaskTransition"
                    ) { task ->
                        val catColor = remember(task.categoryColor) {
                            try {
                                Color(android.graphics.Color.parseColor(task.categoryColor))
                            } catch (e: Exception) {
                                GoldPrimary
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Category Pill
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = catColor.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, catColor.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = task.categoryName.uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = catColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                                )
                            }

                            if (task.isMovedFromPreviousDay) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Carried softly from yesterday",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PeachWarm
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Big Serif Task Title
                            Text(
                                text = task.title,
                                fontFamily = SerifHeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                lineHeight = 36.sp,
                                color = TextWhite,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            if (task.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = task.notes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSilver.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Reward Preview Pill
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = GoldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "+${task.xpReward} XP  •  +${task.coinReward} 🪙",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldBright,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(26.dp))

                            // Gentle Completion Checkmark Feedback OR Optional Timer
                            if (justCheckedCurrent) {
                                val checkScale by animateFloatAsState(
                                    targetValue = 1.0f,
                                    animationSpec = spring(dampingRatio = 0.55f, stiffness = 260f),
                                    label = "checkScale"
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(84.dp)
                                        .scale(checkScale)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Task Completed",
                                        tint = NavyDarkest,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            } else {
                                // Optional Timer Toggle & Panel
                                if (!showTimerPanel) {
                                    Surface(
                                        onClick = { showTimerPanel = true },
                                        shape = RoundedCornerShape(50),
                                        color = Color.White.copy(alpha = 0.08f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Timer,
                                                contentDescription = "Optional Timer",
                                                tint = PeachSoft,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Optional focus timer",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = PeachSoft
                                            )
                                        }
                                    }
                                } else {
                                    RitualFocusTimerCard(
                                        selectedMinutes = selectedMinutes,
                                        remainingSeconds = remainingSeconds,
                                        isRunning = isTimerRunning,
                                        onSelectMinutes = { mins ->
                                            selectedMinutes = mins
                                            remainingSeconds = mins * 60
                                            isTimerRunning = false
                                        },
                                        onToggleRun = { isTimerRunning = !isTimerRunning },
                                        onReset = {
                                            isTimerRunning = false
                                            remainingSeconds = selectedMinutes * 60
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Single Large Primary Button: DONE
                Button(
                    onClick = {
                        if (justCheckedCurrent) return@Button
                        scope.launch {
                            justCheckedCurrent = true
                            val (xp, coins) = onCompleteTask(currentTask)
                            if (xp > 0 || coins > 0) {
                                sessionXp += xp
                                sessionCoins += coins
                                completedInSession += 1
                            } else if (!currentTask.isCompleted) {
                                sessionXp += currentTask.xpReward
                                sessionCoins += currentTask.coinReward
                                completedInSession += 1
                            }
                            delay(550L)
                            justCheckedCurrent = false
                            if (currentIndex + 1 < pendingTasks.size) {
                                currentIndex += 1
                            } else {
                                isCompletedAll = true
                            }
                        }
                    },
                    enabled = !justCheckedCurrent,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (justCheckedCurrent) EmeraldSuccess else GoldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("ritual_done_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (justCheckedCurrent) "Completed" else "Done",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDarkest
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun RitualFocusTimerCard(
    selectedMinutes: Int,
    remainingSeconds: Int,
    isRunning: Boolean,
    onSelectMinutes: (Int) -> Unit,
    onToggleRun: () -> Unit,
    onReset: () -> Unit
) {
    val totalSec = (selectedMinutes * 60).coerceAtLeast(1)
    val fraction = remainingSeconds.toFloat() / totalSec
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeText = String.format("%02d:%02d", mins, secs)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CalmCardSurface.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CalmCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(5, 10, 15, 25).forEach { m ->
                    val isSel = selectedMinutes == m
                    Surface(
                        onClick = { onSelectMinutes(m) },
                        shape = RoundedCornerShape(50),
                        color = if (isSel) PeachWarm else NavySurfaceElevated
                    ) {
                        Text(
                            text = "${m}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSel) NavyDarkest else TextSilver,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 5.dp.toPx()
                        drawArc(
                            color = Color.White.copy(alpha = 0.12f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = PeachWarm,
                            startAngle = -90f,
                            sweepAngle = 360f * fraction,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                IconButton(
                    onClick = onToggleRun,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                        tint = NavyDarkest
                    )
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NavySurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Reset Timer",
                        tint = TextSilver
                    )
                }
            }
        }
    }
}

@Composable
private fun RitualCelebrationView(
    ritualType: RitualType,
    userName: String,
    completedCount: Int,
    xpEarned: Int,
    coinsEarned: Int,
    onOpenEveningCheckIn: (() -> Unit)?,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Soft concentric ambient celebration glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.36f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.25f),
                        PeachWarm.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.55f
                ),
                center = center,
                radius = size.width * 0.55f
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(GoldBright, PeachWarm))
                        )
                ) {
                    Text(ritualType.emoji, fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${ritualType.title}\nComplete",
                    fontFamily = SerifHeadingFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (completedCount > 0) "Peaceful focus, $userName. You completed $completedCount ${if (completedCount == 1) "step" else "steps"}."
                    else "Everything in this ritual is already complete, $userName.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSilver,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("XP EARNED", style = MaterialTheme.typography.labelSmall, color = PeachSoft)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$xpEarned XP",
                                fontFamily = SerifHeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = GoldBright
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PeachWarm.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("COINS EARNED", style = MaterialTheme.typography.labelSmall, color = PeachSoft)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$coinsEarned 🪙",
                                fontFamily = SerifHeadingFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = PeachWarm
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (ritualType == RitualType.EVENING && onOpenEveningCheckIn != null) {
                    OutlinedButton(
                        onClick = {
                            onFinish()
                            onOpenEveningCheckIn()
                        },
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PeachSoft.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Icon(Icons.Rounded.NightsStay, contentDescription = null, tint = PeachSoft, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Evening Check-in & Plan Tomorrow", color = PeachSoft, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = onFinish,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("ritual_celebration_done")
                ) {
                    Text(
                        text = "Back to Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDarkest
                    )
                }
            }
        }
    }
}
