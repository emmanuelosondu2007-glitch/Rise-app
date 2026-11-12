package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.ui.RiseViewModel
import com.example.ui.components.CelebrationModal
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.EveningCheckInDialog
import com.example.ui.components.RitualModeScreen
import com.example.ui.components.RitualType
import com.example.ui.components.SundayNightCheckInDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WeeklyGoalsScreen
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.RiseTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

enum class RiseScreen(val title: String) {
    TODAY("Today"),
    JOURNEY("Journey"),
    ME("Me")
}

class MainActivity : ComponentActivity() {

    private val viewModel: RiseViewModel by viewModels()

    // Request POST_NOTIFICATIONS on Android 13+ on first launch
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.refreshAlarmStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (intent?.getBooleanExtra("OPEN_EVENING_CHECKIN", false) == true) {
            viewModel.openEveningCheckIn()
        }
        if (intent?.getBooleanExtra("OPEN_SUNDAY_CHECKIN", false) == true) {
            viewModel.openSundayCheckIn()
        }

        setContent {
            RiseTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.rescheduleSavedAlarmIfPermitted()
        viewModel.refreshAlarmStatus()
    }
}

@Composable
fun MainAppContent(viewModel: RiseViewModel) {
    var currentScreen by rememberSaveable { mutableStateOf(RiseScreen.TODAY) }
    var activeRitual by remember { mutableStateOf<Pair<RitualType, List<DailyTask>>?>(null) }

    val userProfile by viewModel.userProfile.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val celebrationEvent by viewModel.activeCelebration.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val showEveningCheckIn by viewModel.showEveningCheckIn.collectAsState()
    val showSundayCheckIn by viewModel.showSundayCheckIn.collectAsState()
    val weeklyReport by viewModel.weeklyReport.collectAsState()
    val alarmStatus by viewModel.alarmStatus.collectAsState()
    val context = LocalContext.current
    var showFullScreenDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(alarmStatus.canUseFullScreen) {
        if (!alarmStatus.canUseFullScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            showFullScreenDialog = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = NavyDarkest,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == RiseScreen.TODAY,
                        onClick = { currentScreen = RiseScreen.TODAY },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.WbSunny,
                                contentDescription = "Today"
                            )
                        },
                        label = {
                            Text(
                                text = "Today",
                                fontWeight = if (currentScreen == RiseScreen.TODAY) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkest,
                            selectedTextColor = GoldBright,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_today")
                    )

                    NavigationBarItem(
                        selected = currentScreen == RiseScreen.JOURNEY,
                        onClick = { currentScreen = RiseScreen.JOURNEY },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Flag,
                                contentDescription = "Journey"
                            )
                        },
                        label = {
                            Text(
                                text = "Journey",
                                fontWeight = if (currentScreen == RiseScreen.JOURNEY) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkest,
                            selectedTextColor = GoldBright,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_journey")
                    )

                    NavigationBarItem(
                        selected = currentScreen == RiseScreen.ME,
                        onClick = { currentScreen = RiseScreen.ME },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Me"
                            )
                        },
                        label = {
                            Text(
                                text = "Me",
                                fontWeight = if (currentScreen == RiseScreen.ME) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkest,
                            selectedTextColor = GoldBright,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_me")
                    )
                }
            },
            containerColor = NavyBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                    when (screen) {
                        RiseScreen.TODAY -> HomeScreen(
                            viewModel = viewModel,
                            onStartRitual = { type, tasks -> activeRitual = type to tasks },
                            onNavigateJourney = { currentScreen = RiseScreen.JOURNEY },
                            onNavigateMe = { currentScreen = RiseScreen.ME }
                        )
                        RiseScreen.JOURNEY -> WeeklyGoalsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = RiseScreen.TODAY }
                        )
                        RiseScreen.ME -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = RiseScreen.TODAY }
                        )
                    }
                }
            }
        }

        // Full-Screen Guided Ritual Mode Overlay (covers bottom navigation cleanly)
        AnimatedVisibility(
            visible = activeRitual != null,
            enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 6 }) + fadeOut()
        ) {
            activeRitual?.let { (ritualType, ritualTasks) ->
                RitualModeScreen(
                    ritualType = ritualType,
                    tasks = ritualTasks,
                    userName = userProfile?.userName ?: "Emmanuel",
                    onCompleteTask = { task -> viewModel.completeTaskInRitual(task) },
                    onPlayGentleSound = { viewModel.playGentleRitualCompleteSound() },
                    onOpenEveningCheckIn = if (ritualType == RitualType.EVENING) {
                        {
                            activeRitual = null
                            viewModel.openEveningCheckIn()
                        }
                    } else null,
                    onClose = { activeRitual = null }
                )
            }
        }

        // Confetti Overlay
        ConfettiOverlay(trigger = confettiTrigger)

        // Celebration Modal (Level Up / Clean Sweep / Perfect Week / Sunday Bounty / Badge / Reward)
        celebrationEvent?.let { event ->
            CelebrationModal(
                event = event,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }

        // Evening Check-In Dialog
        if (showEveningCheckIn && userProfile != null) {
            EveningCheckInDialog(
                profile = userProfile!!,
                todayTasks = todayTasks,
                onDismiss = { viewModel.closeEveningCheckIn() },
                onSpeakReflection = { viewModel.speakEveningReflection() },
                onAddTomorrowTask = { title ->
                    val defaultCat = categories.firstOrNull() ?: CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿")
                    viewModel.addTomorrowTask(title, defaultCat)
                }
            )
        }

        // Sunday Night Check-In Dialog
        if (showSundayCheckIn) {
            SundayNightCheckInDialog(
                report = weeklyReport,
                onDismiss = { viewModel.closeSundayCheckIn() },
                onClaimBounty = { viewModel.claimSundayCheckInBounty() },
                onAddGoalForNextWeek = { goalTitle ->
                    val defaultCat = categories.firstOrNull() ?: CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿")
                    viewModel.addWeeklyGoal(goalTitle, 5, defaultCat)
                }
            )
        }

        // Android 14+ Full-Screen Alarm Permission Dialog
        if (showFullScreenDialog && !alarmStatus.canUseFullScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            AlertDialog(
                onDismissRequest = { showFullScreenDialog = false },
                title = {
                    Text(
                        text = "Full-Screen Alarm Permission",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Rise needs permission to show the full-screen alarm so you can wake up and dismiss or snooze it, even when your phone is locked.",
                        color = TextSilver,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showFullScreenDialog = false
                            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest)
                    ) {
                        Text("Open Settings", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFullScreenDialog = false }) {
                        Text("Later", color = TextMuted)
                    }
                },
                containerColor = NavySurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
