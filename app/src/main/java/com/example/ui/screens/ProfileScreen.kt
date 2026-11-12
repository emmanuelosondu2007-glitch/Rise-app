package com.example.ui.screens

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.GeminiTtsService
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.ui.RiseViewModel
import com.example.ui.theme.CalmCardBorder
import com.example.ui.theme.CalmCardSurface
import com.example.ui.theme.CalmCardSurfaceWarm
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHighlight
import com.example.ui.theme.PeachSoft
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.SerifHeadingFontFamily
import com.example.ui.theme.SunriseTwilight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun ProfileScreen(
    viewModel: RiseViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val friendlyVoices by viewModel.friendlyVoices.collectAsState()
    val geminiAudioStatus by viewModel.geminiAudioStatus.collectAsState()
    val isGeneratingGeminiAudio by viewModel.isGeneratingGeminiAudio.collectAsState()
    val activePreviewVoice by viewModel.activePreviewVoice.collectAsState()
    val isPreviewGenerating by viewModel.isPreviewGenerating.collectAsState()
    val isPreviewPlaying by viewModel.isPreviewPlaying.collectAsState()
    val geminiDailyUsesCount by viewModel.geminiDailyUsesCount.collectAsState()
    val alarmStatus by viewModel.alarmStatus.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showCreateRewardDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var showExactAlarmPermissionDialog by remember { mutableStateOf(false) }

    // Collapsible Settings sections — ALL collapsed by default
    var isAlarmExpanded by remember { mutableStateOf(false) }
    var isVoiceExpanded by remember { mutableStateOf(false) }
    var isCategoriesExpanded by remember { mutableStateOf(false) }
    var isAdvancedExpanded by remember { mutableStateOf(false) }

    // Gemini Realistic Voice State
    var useRealisticVoice by remember(userProfile?.useRealisticVoice) {
        mutableStateOf(userProfile?.useRealisticVoice ?: false)
    }
    var geminiApiKeyInput by remember { mutableStateOf(viewModel.getGeminiApiKey()) }
    var isApiKeyMasked by remember { mutableStateOf(true) }
    val selectedGeminiVoice = userProfile?.geminiVoiceName ?: "Charon"
    var geminiVoiceStyle by remember(userProfile?.geminiVoiceStyle) {
        mutableStateOf(
            userProfile?.geminiVoiceStyle
                ?: "Warm, deep, unhurried storyteller. Speaks like a wise, encouraging mentor, with natural pauses and a slight smile in the voice."
        )
    }

    // Phone TTS State
    var showAllLanguages by remember { mutableStateOf(false) }
    var speedSlider by remember(userProfile?.speechSpeed) { mutableFloatStateOf(userProfile?.speechSpeed ?: 0.95f) }
    var pitchSlider by remember(userProfile?.speechPitch) { mutableFloatStateOf(userProfile?.speechPitch ?: 0.85f) }
    var selectedVoiceName by remember(userProfile?.speechVoiceName) { mutableStateOf(userProfile?.speechVoiceName ?: "") }

    val level = userProfile?.level ?: 1
    val currentXp = userProfile?.xp ?: 0
    val userCoins = userProfile?.coins ?: 0
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
    val levelProgress = ((currentXp - minXp).toFloat() / (maxXp - minXp).coerceAtLeast(1)).coerceIn(0f, 1f)

    val alarmHour = userProfile?.alarmHour ?: 7
    val alarmMin = userProfile?.alarmMinute ?: 0
    val alarmEnabled = userProfile?.alarmEnabled ?: true
    val eveningHour = userProfile?.eveningHour ?: 20
    val eveningMin = userProfile?.eveningMinute ?: 30
    val eveningEnabled = userProfile?.eveningReminderEnabled ?: true

    val alarmTimeFormatted = String.format(
        "%d:%02d %s",
        if (alarmHour % 12 == 0) 12 else alarmHour % 12,
        alarmMin,
        if (alarmHour >= 12) "PM" else "AM"
    )
    val eveningTimeFormatted = String.format(
        "%d:%02d %s",
        if (eveningHour % 12 == 0) 12 else eveningHour % 12,
        eveningMin,
        if (eveningHour >= 12) "PM" else "AM"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("me_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 0. CALM SERIF HEADER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(SunriseTwilight.copy(alpha = 0.85f), NavyBackground)
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "Me",
                            fontFamily = SerifHeadingFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your sanctuary, earned rewards & gentle preferences",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PeachSoft.copy(alpha = 0.88f)
                        )
                    }
                }
            }

            // 1. PROFILE CARD (Avatar, Name, Level & XP, Streak/Shield, Badges)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CalmCardSurfaceWarm),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.38f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(GoldPrimary, PeachWarm)
                                            )
                                        )
                                ) {
                                    Text(
                                        text = "${userProfile?.userName?.firstOrNull()?.uppercaseChar() ?: 'E'}",
                                        fontFamily = SerifHeadingFontFamily,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDarkest
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile?.userName ?: "Emmanuel",
                                            fontFamily = SerifHeadingFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        IconButton(
                                            onClick = { showEditNameDialog = true },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Name",
                                                tint = PeachSoft,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = GoldPrimary.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "Level $level • ${userProfile?.levelTitle ?: "Starter"}",
                                            color = GoldBright,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$currentXp XP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                                Text(
                                    text = "${(maxXp - currentXp).coerceAtLeast(0)} to next",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${userProfile?.currentStreak ?: 1} Day Streak",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameYellow
                                    )
                                }
                                Text(
                                    text = "Clean sweeps: ${userProfile?.listCompletionStreak ?: 0} days",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSilver
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = NavySurfaceElevated.copy(alpha = 0.8f),
                                border = BorderStroke(
                                    1.dp,
                                    if (userProfile?.streakShieldAvailable == true) EmeraldSuccess.copy(alpha = 0.6f)
                                    else CalmCardBorder
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Streak Shield",
                                        tint = if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (userProfile?.streakShieldAvailable == true) "Shield Ready" else "Shield Used",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (userProfile?.streakShieldAvailable == true) EmeraldSuccess else TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        if (badges.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = CalmCardBorder)
                            Spacer(modifier = Modifier.height(12.dp))
                            val unlockedCount = badges.count { it.isUnlocked }
                            Text(
                                text = "Milestones Earned ($unlockedCount/${badges.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = PeachSoft,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(badges, key = { it.id }) { badge ->
                                    CompactBadgeChip(badge = badge)
                                }
                            }
                        }
                    }
                }
            }

            // 2. REWARDS SHOP CARD
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
                    border = BorderStroke(1.dp, CalmCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("rewards_shop_card")
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Rewards Shop",
                                    fontFamily = SerifHeadingFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$userCoins coins available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
                                    )
                                }
                            }

                            Button(
                                onClick = { showCreateRewardDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary.copy(alpha = 0.16f),
                                    contentColor = GoldBright
                                ),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .height(40.dp)
                                    .testTag("create_custom_reward_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Custom Reward",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Custom", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            rewards.forEach { reward ->
                                val canAfford = userCoins >= reward.coinPrice
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = NavySurfaceElevated.copy(alpha = 0.65f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (canAfford) GoldPrimary.copy(alpha = 0.35f) else NavySurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reward_item_${reward.id}")
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
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(NavySurface)
                                                    .border(1.dp, GoldSecondary.copy(alpha = 0.3f), CircleShape)
                                            ) {
                                                Text(text = reward.iconEmoji, fontSize = 20.sp)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = reward.title,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextWhite
                                                )
                                                if (reward.description.isNotBlank()) {
                                                    Text(
                                                        text = reward.description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextSilver
                                                    )
                                                }
                                                if (reward.timesClaimed > 0) {
                                                    Text(
                                                        text = "Enjoyed ${reward.timesClaimed} ${if (reward.timesClaimed == 1) "time" else "times"}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = EmeraldSuccess
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(horizontalAlignment = Alignment.End) {
                                            Button(
                                                onClick = { viewModel.redeemReward(reward) },
                                                enabled = canAfford,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = GoldPrimary,
                                                    contentColor = NavyDarkest,
                                                    disabledContainerColor = NavySurface,
                                                    disabledContentColor = TextMuted
                                                ),
                                                shape = RoundedCornerShape(50),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .height(38.dp)
                                                    .testTag("redeem_button_${reward.id}")
                                            ) {
                                                Text(
                                                    text = "${reward.coinPrice} 🪙",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            if (!canAfford) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Need ${reward.coinPrice - userCoins} more",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            } else if (!reward.isPreset) {
                                                IconButton(
                                                    onClick = { viewModel.deleteReward(reward) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete reward",
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(16.dp)
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
            }

            // 3. SETTINGS SECTION HEADER
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Settings",
                        fontFamily = SerifHeadingFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Tap any section below to customize",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // 3A. ALARM (Collapsed by default)
            item {
                CollapsibleSettingsCard(
                    title = "Alarm",
                    summary = if (alarmEnabled) "$alarmTimeFormatted • Evening $eveningTimeFormatted" else "Morning alarm off • Evening $eveningTimeFormatted",
                    icon = Icons.Rounded.Alarm,
                    accentColor = PeachWarm,
                    expanded = isAlarmExpanded,
                    onToggleExpand = { isAlarmExpanded = !isAlarmExpanded },
                    testTag = "settings_alarm_card"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Morning Wake-Up Alarm
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Morning Wake-Up Alarm",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    "Loud ALARM stream until you tap 'I'm up'",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSilver
                                )
                            }
                            Switch(
                                checked = alarmEnabled,
                                onCheckedChange = { isEnabled ->
                                    if (isEnabled && !viewModel.alarmScheduler.canScheduleExactAlarms()) {
                                        showExactAlarmPermissionDialog = true
                                    }
                                    viewModel.updateAlarmTime(alarmHour, alarmMin, isEnabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GoldPrimary
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    TimePickerDialog(context, { _, h, m ->
                                        val timeStr = String.format(
                                            "%02d:%02d %s",
                                            if (h % 12 == 0) 12 else h % 12,
                                            m,
                                            if (h >= 12) "PM" else "AM"
                                        )
                                        Toast.makeText(context, "Alarm set for $timeStr", Toast.LENGTH_SHORT).show()
                                        if (!viewModel.alarmScheduler.canScheduleExactAlarms()) {
                                            showExactAlarmPermissionDialog = true
                                        }
                                        viewModel.updateAlarmTime(h, m, true)
                                    }, alarmHour, alarmMin, false).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = NavySurfaceElevated,
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = alarmTimeFormatted,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.testAlarmNow() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = NavyDarkest
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Test Loud Alarm", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Clear green/red Alarm Status line
                        if (alarmStatus.isScheduled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EmeraldSuccess.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Alarm set for ${alarmStatus.formattedTime ?: "tomorrow"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else if (alarmStatus.needsPermissionFix) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF5252).copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFF5252))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Alarm NOT set: permission needed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF5252),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        if (!alarmStatus.canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                                data = Uri.parse("package:${context.packageName}")
                                            }
                                            context.startActivity(intent)
                                        } else if (!alarmStatus.canUseFullScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                                                data = Uri.parse("package:${context.packageName}")
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.parse("package:${context.packageName}")
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                ) {
                                    Text("Fix", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        HorizontalDivider(color = CalmCardBorder)

                        // Evening Check-In Time Setting
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Evening Check-In Reminder",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    "Daily reflection & tomorrow's plan at $eveningTimeFormatted",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSilver
                                )
                            }
                            Switch(
                                checked = eveningEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.updateEveningTime(eveningHour, eveningMin, enabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GoldPrimary
                                )
                            )
                        }

                        Surface(
                            onClick = {
                                TimePickerDialog(context, { _, h, m ->
                                    viewModel.updateEveningTime(h, m, true)
                                }, eveningHour, eveningMin, false).show()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = NavySurfaceElevated,
                            border = BorderStroke(1.dp, CalmCardBorder)
                        ) {
                            Text(
                                text = "Evening reminder: $eveningTimeFormatted",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = PeachSoft,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // 3B. VOICE (Phone Voice Studio — Collapsed by default)
            item {
                CollapsibleSettingsCard(
                    title = "Voice",
                    summary = "Speed ${String.format("%.2fx", speedSlider)} • Pitch ${String.format("%.2f", pitchSlider)}",
                    icon = Icons.Rounded.GraphicEq,
                    accentColor = GoldPrimary,
                    expanded = isVoiceExpanded,
                    onToggleExpand = { isVoiceExpanded = !isVoiceExpanded },
                    testTag = "settings_voice_card"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phone Voice Studio",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            OutlinedButton(
                                onClick = {
                                    viewModel.resetPhoneVoiceToDefault()
                                    speedSlider = com.example.audio.TtsManager.DEFAULT_SPEED
                                    pitchSlider = com.example.audio.TtsManager.DEFAULT_PITCH
                                    selectedVoiceName = ""
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("reset_default_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Reset",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset Default", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Column {
                            Text(
                                text = "Speech Speed: ${String.format("%.2f", speedSlider)}x (Default: 0.95x)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSilver
                            )
                            Slider(
                                value = speedSlider,
                                onValueChange = {
                                    speedSlider = it
                                    viewModel.updateSpeechSettings(speedSlider, pitchSlider, selectedVoiceName)
                                },
                                valueRange = 0.5f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldPrimary,
                                    activeTrackColor = GoldPrimary,
                                    inactiveTrackColor = NavySurfaceElevated
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "Voice Pitch: ${String.format("%.2f", pitchSlider)} (Default: 0.85)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSilver
                            )
                            Slider(
                                value = pitchSlider,
                                onValueChange = {
                                    pitchSlider = it
                                    viewModel.updateSpeechSettings(speedSlider, pitchSlider, selectedVoiceName)
                                },
                                valueRange = 0.5f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = PeachWarm,
                                    activeTrackColor = PeachWarm,
                                    inactiveTrackColor = NavySurfaceElevated
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Device Voices (${if (showAllLanguages) friendlyVoices.size else friendlyVoices.count { it.isEnglish }})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (showAllLanguages) "All languages" else "English only",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSilver
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = showAllLanguages,
                                    onCheckedChange = { showAllLanguages = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NavyDarkest,
                                        checkedTrackColor = GoldPrimary
                                    )
                                )
                            }
                        }

                        val displayedFriendlyVoices = if (showAllLanguages) {
                            friendlyVoices
                        } else {
                            friendlyVoices.filter { it.isEnglish }
                        }

                        if (displayedFriendlyVoices.isEmpty()) {
                            Text(
                                text = "Using system default English voice (deep adult configuration).",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(displayedFriendlyVoices) { friendlyVoice ->
                                    val isSelected = selectedVoiceName == friendlyVoice.voiceName
                                    Surface(
                                        onClick = {
                                            selectedVoiceName = friendlyVoice.voiceName
                                            viewModel.updateSpeechSettings(speedSlider, pitchSlider, friendlyVoice.voiceName)
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) GoldPrimary else NavySurfaceElevated,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) GoldPrimary else NavySurfaceBorder
                                        ),
                                        modifier = Modifier.testTag("voice_${friendlyVoice.displayName.replace(" ", "_")}")
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            Text(
                                                text = friendlyVoice.displayName,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) NavyDarkest else TextWhite
                                            )
                                            Text(
                                                text = friendlyVoice.locale.displayCountry.ifBlank { friendlyVoice.locale.language.uppercase() },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = if (isSelected) NavyDarkest.copy(alpha = 0.7f) else TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.previewVoice(selectedVoiceName, speedSlider, pitchSlider) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavySurfaceHighlight,
                                contentColor = TextWhite
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("preview_phone_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Preview Phone Voice", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3C. CATEGORIES (Collapsed by default)
            item {
                CollapsibleSettingsCard(
                    title = "Categories",
                    summary = "${categories.size} life areas",
                    icon = Icons.Rounded.Category,
                    accentColor = Color(0xFF7FD1B9),
                    expanded = isCategoriesExpanded,
                    onToggleExpand = { isCategoriesExpanded = !isCategoriesExpanded },
                    testTag = "settings_categories_card"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Task Categories (${categories.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Surface(
                                onClick = { showAddCategoryDialog = true },
                                shape = RoundedCornerShape(50),
                                color = GoldPrimary.copy(alpha = 0.16f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Category",
                                        tint = GoldBright,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Add Category",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        categories.forEach { cat ->
                            val catColor = try {
                                Color(android.graphics.Color.parseColor(cat.colorHex))
                            } catch (_: Exception) {
                                GoldPrimary
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = NavySurfaceElevated.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, CalmCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                                .background(catColor.copy(alpha = 0.2f))
                                        ) {
                                            Text(cat.iconEmoji, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = cat.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                            if (cat.description.isNotBlank()) {
                                                Text(
                                                    text = cat.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSilver,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { editingCategory = cat },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit category",
                                                tint = TextSilver,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        if (!cat.isDefault) {
                                            IconButton(
                                                onClick = { viewModel.deleteCategory(cat) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete category",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(16.dp)
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

            // 3D. ADVANCED (Gemini API key & realistic AI voice options — Collapsed by default)
            item {
                CollapsibleSettingsCard(
                    title = "Advanced",
                    summary = if (useRealisticVoice) "Gemini Voice: $selectedGeminiVoice • $geminiDailyUsesCount/10" else "Gemini realistic voice options",
                    icon = Icons.Rounded.Tune,
                    accentColor = Color(0xFF9F8FEF),
                    expanded = isAdvancedExpanded,
                    onToggleExpand = { isAdvancedExpanded = !isAdvancedExpanded },
                    testTag = "settings_advanced_card"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Use Realistic Voice Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Use realistic voice",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Gemini neural voice for morning briefing",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSilver
                                )
                            }
                            Switch(
                                checked = useRealisticVoice,
                                onCheckedChange = { checked ->
                                    useRealisticVoice = checked
                                    viewModel.updateRealisticVoiceSettings(checked, selectedGeminiVoice, geminiVoiceStyle)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GoldPrimary,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = NavySurfaceElevated
                                ),
                                modifier = Modifier.testTag("realistic_voice_switch")
                            )
                        }

                        HorizontalDivider(color = CalmCardBorder)

                        // Encrypted Gemini API Key Input
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Your Gemini API Key",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (viewModel.hasGeminiApiKey()) EmeraldSuccess.copy(alpha = 0.15f) else FlameOrange.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (viewModel.hasGeminiApiKey()) "Encrypted 🔒" else "Required ⚠️",
                                        color = if (viewModel.hasGeminiApiKey()) EmeraldSuccess else FlameYellow,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Stored ONLY on this phone (AES-GCM encrypted in Android KeyStore). Never hard-coded into repo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = geminiApiKeyInput,
                                onValueChange = { geminiApiKeyInput = it },
                                placeholder = { Text("Paste your Gemini API key (AIza...)", color = TextMuted, fontSize = 12.sp) },
                                singleLine = true,
                                visualTransformation = if (isApiKeyMasked) PasswordVisualTransformation() else VisualTransformation.None,
                                trailingIcon = {
                                    IconButton(onClick = { isApiKeyMasked = !isApiKeyMasked }) {
                                        Icon(
                                            imageVector = if (isApiKeyMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle key visibility",
                                            tint = TextSilver,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavySurfaceBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = NavySurfaceElevated,
                                    unfocusedContainerColor = NavySurfaceElevated
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gemini_api_key_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { viewModel.saveGeminiApiKey(geminiApiKeyInput) },
                                    enabled = geminiApiKeyInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = NavyDarkest
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("save_gemini_key_button")
                                ) {
                                    Text("Save Key Securely", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                if (viewModel.hasGeminiApiKey()) {
                                    OutlinedButton(
                                        onClick = {
                                            geminiApiKeyInput = ""
                                            viewModel.clearGeminiApiKey()
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameOrange),
                                        modifier = Modifier.height(42.dp)
                                    ) {
                                        Text("Clear", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Daily Usage Quota Counter
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NavySurfaceElevated,
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gemini_usage_counter_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Realistic voice uses today: $geminiDailyUsesCount of 10",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (geminiDailyUsesCount >= 10) FlameOrange else GoldBright,
                                    modifier = Modifier.testTag("gemini_usage_counter")
                                )
                                Text(
                                    text = "Free Tier",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSilver,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Error Banner for Preview / Generation Failures & 429 Quota Exceeded
                        if (geminiAudioStatus.contains("failed", ignoreCase = true) ||
                            geminiAudioStatus.contains("error", ignoreCase = true) ||
                            geminiAudioStatus.contains("limit reached", ignoreCase = true)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FlameOrange.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, FlameOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gemini_error_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚠️", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = geminiAudioStatus,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FlameYellow,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Gemini Voice Picker
                        Column {
                            Text(
                                text = "Choose Gemini Voice (${GeminiTtsService.PREBUILT_VOICES.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Selected: $selectedGeminiVoice",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldBright,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.testTag("selected_voice_label")
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                GeminiTtsService.PREBUILT_VOICES.forEach { voice ->
                                    val isSelected = selectedGeminiVoice == voice.name
                                    val isThisVoiceActive = activePreviewVoice == voice.name
                                    val isThisVoiceGenerating = isThisVoiceActive && isPreviewGenerating
                                    val isThisVoicePlaying = isThisVoiceActive && isPreviewPlaying
                                    val isOtherVoiceGenerating = isPreviewGenerating && !isThisVoiceActive

                                    Surface(
                                        onClick = {
                                            viewModel.updateRealisticVoiceSettings(useRealisticVoice, voice.name, geminiVoiceStyle)
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else NavySurfaceElevated,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) GoldPrimary else NavySurfaceBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("voice_card_${voice.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RecordVoiceOver,
                                                    contentDescription = null,
                                                    tint = if (isSelected) GoldPrimary else TextMuted,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = voice.label,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) GoldBright else TextWhite
                                                    )
                                                    Text(
                                                        text = voice.description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextSilver,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    if (isThisVoicePlaying) {
                                                        viewModel.stopPreview()
                                                    } else {
                                                        viewModel.previewGeminiVoice(voice.name, geminiVoiceStyle)
                                                    }
                                                },
                                                enabled = !isOtherVoiceGenerating,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isThisVoicePlaying) FlameOrange else if (isSelected) GoldPrimary else NavySurfaceHighlight,
                                                    contentColor = if (isThisVoicePlaying) TextWhite else if (isSelected) NavyDarkest else TextWhite,
                                                    disabledContainerColor = NavySurfaceHighlight.copy(alpha = 0.5f),
                                                    disabledContentColor = TextMuted
                                                ),
                                                shape = RoundedCornerShape(50),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(36.dp)
                                                    .testTag("preview_gemini_${voice.name}")
                                            ) {
                                                if (isThisVoiceGenerating) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(12.dp),
                                                        color = if (isSelected) NavyDarkest else GoldPrimary,
                                                        strokeWidth = 2.dp
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Loading...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                } else if (isThisVoicePlaying) {
                                                    Icon(
                                                        imageVector = Icons.Default.Stop,
                                                        contentDescription = "Stop",
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.VolumeUp,
                                                        contentDescription = "Preview",
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Voice Style Text Box
                        Column {
                            Text(
                                text = "Voice Style Prompt",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Directs Gemini's tone, pacing, emotion and inflection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = geminiVoiceStyle,
                                onValueChange = {
                                    geminiVoiceStyle = it
                                    viewModel.updateRealisticVoiceSettings(useRealisticVoice, selectedGeminiVoice, it)
                                },
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavySurfaceBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = NavySurfaceElevated,
                                    unfocusedContainerColor = NavySurfaceElevated
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gemini_voice_style_input")
                            )
                        }

                        // Night-Before Morning Audio Status & Regenerate Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Night-Before Audio Status",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameYellow
                                    )
                                    if (isGeneratingGeminiAudio) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = GoldPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (geminiAudioStatus.isNotBlank()) {
                                        geminiAudioStatus
                                    } else if (userProfile?.lastMorningAudioGeneratedForDate?.isNotEmpty() == true) {
                                        "Audio ready for ${userProfile?.lastMorningAudioGeneratedForDate} ✅"
                                    } else {
                                        "Will generate once tonight during evening check-in (or automatically at 11:00 PM)."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.generateTomorrowMorningAudio(force = true) },
                                    enabled = useRealisticVoice && viewModel.hasGeminiApiKey() && !isGeneratingGeminiAudio,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = NavyDarkest
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("regenerate_morning_audio_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Regenerate morning audio", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Custom Reward Dialog (reused cleanly from RewardsShopScreen.kt)
        if (showCreateRewardDialog) {
            CreateRewardDialog(
                onDismiss = { showCreateRewardDialog = false },
                onConfirm = { title, description, price, emoji ->
                    viewModel.addCustomReward(title, description, price, emoji)
                    showCreateRewardDialog = false
                }
            )
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            AddOrEditCategoryDialog(
                initialCategory = null,
                onDismiss = { showAddCategoryDialog = false },
                onSave = { name, colorHex, emoji ->
                    viewModel.addCategory(name, colorHex, emoji)
                    showAddCategoryDialog = false
                }
            )
        }

        // Edit Category Dialog
        editingCategory?.let { cat ->
            AddOrEditCategoryDialog(
                initialCategory = cat,
                onDismiss = { editingCategory = null },
                onSave = { name, colorHex, emoji ->
                    viewModel.updateCategory(cat.copy(name = name, colorHex = colorHex, iconEmoji = emoji))
                    editingCategory = null
                }
            )
        }

        // Edit Name Dialog
        if (showEditNameDialog) {
            var tempName by remember { mutableStateOf(userProfile?.userName ?: "Emmanuel") }
            Dialog(onDismissRequest = { showEditNameDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Text(
                            "Edit Name",
                            fontFamily = SerifHeadingFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
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
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEditNameDialog = false },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = TextSilver)
                            }
                            Button(
                                onClick = {
                                    viewModel.updateUserName(tempName)
                                    showEditNameDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = NavyDarkest
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Exact Alarm Permission Dialog
        if (showExactAlarmPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showExactAlarmPermissionDialog = false },
                title = {
                    Text(
                        text = "Exact Alarm Permission",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Rise needs permission to wake you up on time. Without this, alarms may be delayed or not ring at all.",
                        color = TextSilver,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExactAlarmPermissionDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest)
                    ) {
                        Text("Open Settings", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExactAlarmPermissionDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                },
                containerColor = NavySurface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun CollapsibleSettingsCard(
    title: String,
    summary: String,
    icon: ImageVector,
    accentColor: Color,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CalmCardSurface),
        border = BorderStroke(
            1.dp,
            if (expanded) accentColor.copy(alpha = 0.45f) else CalmCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.16f))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSilver,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                    tint = TextSilver
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    HorizontalDivider(color = CalmCardBorder)
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun CompactBadgeChip(badge: BadgeItem) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (badge.isUnlocked) GoldPrimary.copy(alpha = 0.14f) else NavySurfaceElevated.copy(alpha = 0.55f),
        border = BorderStroke(
            1.dp,
            if (badge.isUnlocked) GoldPrimary.copy(alpha = 0.5f) else CalmCardBorder
        ),
        modifier = Modifier
            .alpha(if (badge.isUnlocked) 1f else 0.55f)
            .testTag("badge_chip_${badge.code}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = badge.iconEmoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) GoldBright else TextSilver
                )
                Text(
                    text = if (badge.isUnlocked) "Unlocked" else "Locked",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (badge.isUnlocked) EmeraldSuccess else TextMuted
                )
            }
        }
    }
}

@Composable
fun AddOrEditCategoryDialog(
    initialCategory: CategoryItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, colorHex: String, emoji: String) -> Unit
) {
    var name by remember { mutableStateOf(initialCategory?.name ?: "") }
    var colorHex by remember { mutableStateOf(initialCategory?.colorHex ?: "#8B5CF6") }
    var emoji by remember { mutableStateOf(initialCategory?.iconEmoji ?: "🏷️") }

    val colors = listOf("#8B5CF6", "#3B82F6", "#10B981", "#F59E0B", "#EC4899", "#06B6D4", "#F43F5E")
    val emojis = listOf("✨", "⚖️", "🌱", "🎬", "🤝", "🌿", "📚", "🎯", "💼", "🛡️")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = if (initialCategory == null) "New Category" else "Edit Category",
                    fontFamily = SerifHeadingFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name", color = TextMuted) },
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

                Text("Icon Emoji:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { em ->
                        Surface(
                            onClick = { emoji = em },
                            shape = RoundedCornerShape(10.dp),
                            color = if (emoji == em) GoldPrimary.copy(alpha = 0.3f) else NavySurfaceElevated
                        ) {
                            Text(text = em, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Color:", style = MaterialTheme.typography.labelSmall, color = TextSilver)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { hex ->
                        val col = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(if (colorHex == hex) 3.dp else 0.dp, TextWhite, CircleShape)
                                .clickable { colorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSilver)
                    }
                    Button(
                        onClick = { if (name.isNotBlank()) onSave(name, colorHex, emoji) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDarkest
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
