package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryItem
import com.example.ui.RiseViewModel
import com.example.ui.components.CelebrationModal
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.EveningCheckInDialog
import com.example.ui.components.SundayNightCheckInDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsShopScreen
import com.example.ui.screens.WeeklyGoalsScreen
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.RiseTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class RiseScreen(val title: String) {
    HOME("Home"),
    WEEKLY("Weekly"),
    SHOP("Shop"),
    PROFILE("Profile")
}

class MainActivity : ComponentActivity() {

    private val viewModel: RiseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
}

@Composable
fun MainAppContent(viewModel: RiseViewModel) {
    var currentScreen by rememberSaveable { mutableStateOf(RiseScreen.HOME) }

    val userProfile by viewModel.userProfile.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val celebrationEvent by viewModel.activeCelebration.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val showEveningCheckIn by viewModel.showEveningCheckIn.collectAsState()
    val showSundayCheckIn by viewModel.showSundayCheckIn.collectAsState()
    val weeklyReport by viewModel.weeklyReport.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NavyDarkest,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == RiseScreen.HOME,
                    onClick = { currentScreen = RiseScreen.HOME },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontWeight = if (currentScreen == RiseScreen.HOME) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDarkest,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen == RiseScreen.WEEKLY,
                    onClick = { currentScreen = RiseScreen.WEEKLY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Weekly Goals"
                        )
                    },
                    label = {
                        Text(
                            text = "Weekly",
                            fontWeight = if (currentScreen == RiseScreen.WEEKLY) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDarkest,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_weekly")
                )

                NavigationBarItem(
                    selected = currentScreen == RiseScreen.SHOP,
                    onClick = { currentScreen = RiseScreen.SHOP },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Rewards Shop"
                        )
                    },
                    label = {
                        Text(
                            text = "Shop",
                            fontWeight = if (currentScreen == RiseScreen.SHOP) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDarkest,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_shop")
                )

                NavigationBarItem(
                    selected = currentScreen == RiseScreen.PROFILE,
                    onClick = { currentScreen = RiseScreen.PROFILE },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile & Settings"
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            fontWeight = if (currentScreen == RiseScreen.PROFILE) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDarkest,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    RiseScreen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateWeeklyGoals = { currentScreen = RiseScreen.WEEKLY },
                        onNavigateShop = { currentScreen = RiseScreen.SHOP },
                        onNavigateProfile = { currentScreen = RiseScreen.PROFILE }
                    )
                    RiseScreen.WEEKLY -> WeeklyGoalsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = RiseScreen.HOME }
                    )
                    RiseScreen.SHOP -> RewardsShopScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = RiseScreen.HOME }
                    )
                    RiseScreen.PROFILE -> ProfileScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = RiseScreen.HOME }
                    )
                }
            }

            // Confetti Overlay
            ConfettiOverlay(trigger = confettiTrigger)

            // Celebration Modal (Level Up / Clean Sweep / Perfect Week / Sunday Bounty / Badge)
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
                        viewModel.addTask(title, "Scheduled during evening reflection", null, defaultCat, 2)
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
        }
    }
}
