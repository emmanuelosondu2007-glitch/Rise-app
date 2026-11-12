package com.example.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.R
import com.example.RiseApplication
import com.example.data.model.DailyTask
import com.example.data.model.Quote
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.CyanFocus
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHighlight
import com.example.ui.theme.RiseTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val app = application as RiseApplication

        setContent {
            RiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NavyBackground
                ) {
                    AlarmScreen(
                        app = app,
                        onSnooze = {
                            app.soundManager.stopAlarmRingtone()
                            app.vibrationHelper.stopVibration()
                            app.alarmScheduler.scheduleSnooze(5)
                            // After Snooze, the next day's normal alarm must still be scheduled
                            app.applicationScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                val profile = app.repository.getProfileSync()
                                if (profile.alarmEnabled) {
                                    app.alarmScheduler.scheduleMorningAlarm(profile.alarmHour, profile.alarmMinute)
                                }
                            }
                            Toast.makeText(this, "Alarm snoozed for 5 minutes ⏰", Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        onDismissToApp = {
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmScreen(
    app: RiseApplication,
    onSnooze: () -> Unit,
    onDismissToApp: () -> Unit
) {
    var isAwakePhase by remember { mutableStateOf(false) }
    var quoteOfTheDay by remember { mutableStateOf<Quote?>(null) }
    var todayTasks by remember { mutableStateOf<List<DailyTask>>(emptyList()) }
    var userName by remember { mutableStateOf("Emmanuel") }
    var todayQuestion by remember { mutableStateOf("What is the one thing that would make today a win?") }
    var winAnswer by remember { mutableStateOf("") }
    var confettiTrigger by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()
    val isSpeaking by app.ttsManager.isSpeaking.collectAsState()

    var userProfileState by remember { mutableStateOf<com.example.data.model.UserProfile?>(null) }
    var isRealisticAudioPlaying by remember { mutableStateOf(false) }

    // Real loud alarm starts ringing immediately with strong vibration. NO speech while ringing!
    DisposableEffect(Unit) {
        app.soundManager.startAlarmRingtone()
        app.vibrationHelper.vibrateAlarm()

        onDispose {
            app.soundManager.stopAlarmRingtone()
            app.soundManager.stopAudioPlayback()
            app.vibrationHelper.stopVibration()
            app.ttsManager.stop()
        }
    }

    LaunchedEffect(Unit) {
        val todayStr = app.repository.getTodayString()
        todayTasks = app.repository.getTasksForDateSync(todayStr)
        val profile = app.database.userProfileDao().getUserProfile()
        userProfileState = profile
        if (profile != null) {
            userName = profile.userName
            todayQuestion = profile.morningWinQuestion
            winAnswer = profile.morningWinAnswer
            app.ttsManager.applyVoiceSettings(profile.speechSpeed, profile.speechPitch, profile.speechVoiceName)
        }
        quoteOfTheDay = app.repository.getQuoteOfTheDay()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alarmPulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isAwakePhase) {
            // PHASE 1: LOUD RINGING ALARM (NO SPEECH)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 28.dp)
                ) {
                    Text(
                        text = "RISE & CONQUER",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 68.sp),
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Text(
                        text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        letterSpacing = 1.5.sp,
                        color = TextSilver
                    )
                }

                // Glowing Sun Emblem
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(240.dp)
                        .scale(pulseScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldPrimary.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.rise_launcher_icon_1794466194695),
                        contentDescription = "Rising Sun",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .border(3.dp, GoldPrimary, CircleShape)
                    )
                }

                // WAKE UP & SNOOZE BUTTONS
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Good morning, $userName",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Loud alarm ringing on ALARM stream",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameYellow
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Dismiss: "I'M UP!"
                    Button(
                        onClick = {
                            // 1. Immediately silence alarm ringtone and stop vibration
                            app.soundManager.stopAlarmRingtone()
                            app.vibrationHelper.stopVibration()
                            app.soundManager.playQuoteRevealChime()
                            app.vibrationHelper.vibrateSuccess()
                            isAwakePhase = true
                            confettiTrigger = System.currentTimeMillis()

                            // 2. NOW start spoken briefing on ALARM stream at full volume
                            val morningAudioFile = app.geminiTtsService.getMorningAudioFile()
                            val shouldPlayRealistic = userProfileState?.useRealisticVoice == true &&
                                    morningAudioFile.exists() && morningAudioFile.length() > 0

                            var played = false
                            if (shouldPlayRealistic) {
                                isRealisticAudioPlaying = true
                                played = app.soundManager.playAudioFile(morningAudioFile) {
                                    isRealisticAudioPlaying = false
                                }
                                if (!played) {
                                    isRealisticAudioPlaying = false
                                }
                            }

                            // If no realistic audio or playback failed, fall back to phone's built-in text-to-speech
                            if (!played) {
                                quoteOfTheDay?.let { q ->
                                    app.ttsManager.speakMorningBriefing(
                                        userName = userName,
                                        quoteText = q.text,
                                        quoteAuthor = q.author,
                                        todayQuestion = todayQuestion,
                                        tasks = todayTasks.map { it.title }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDarkest
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("im_up_button")
                    ) {
                        Text(
                            text = "I'M UP! 🌅",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SNOOZE 5 MIN BUTTON
                    OutlinedButton(
                        onClick = onSnooze,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSilver),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("snooze_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Snooze,
                            contentDescription = null,
                            tint = GoldMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Snooze 5 min",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            // PHASE 2: MORNING BRIEFING & QUOTE OF THE DAY (LOUD SPEECH ACTIVE)
            AnimatedVisibility(
                visible = isAwakePhase,
                enter = fadeIn(tween(400)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MORNING BRIEFING",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Quote of the Day",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }

                            val anyAudioPlaying = isSpeaking || isRealisticAudioPlaying || app.soundManager.isPlayingAudioFile()
                            // TTS / Realistic voice replay
                            IconButton(
                                onClick = {
                                    if (anyAudioPlaying) {
                                        app.soundManager.stopAudioPlayback()
                                        app.ttsManager.stop()
                                        isRealisticAudioPlaying = false
                                    } else {
                                        val morningAudioFile = app.geminiTtsService.getMorningAudioFile()
                                        val shouldPlayRealistic = userProfileState?.useRealisticVoice == true &&
                                                morningAudioFile.exists() && morningAudioFile.length() > 0

                                        var played = false
                                        if (shouldPlayRealistic) {
                                            isRealisticAudioPlaying = true
                                            played = app.soundManager.playAudioFile(morningAudioFile) {
                                                isRealisticAudioPlaying = false
                                            }
                                            if (!played) {
                                                isRealisticAudioPlaying = false
                                            }
                                        }

                                        if (!played) {
                                            quoteOfTheDay?.let { q ->
                                                app.ttsManager.speakMorningBriefing(
                                                    userName = userName,
                                                    quoteText = q.text,
                                                    quoteAuthor = q.author,
                                                    todayQuestion = todayQuestion,
                                                    tasks = todayTasks.map { it.title }
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NavySurfaceElevated)
                                    .testTag("tts_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (anyAudioPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Voice Briefing",
                                    tint = if (anyAudioPlaying) FlameOrange else GoldPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quote Card
                        quoteOfTheDay?.let { quote ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NavySurface),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            color = if (quote.isBibleVerse) CyanFocus.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = quote.category.uppercase(),
                                                color = if (quote.isBibleVerse) CyanFocus else FlameYellow,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Text(
                                            text = "“ ”",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary.copy(alpha = 0.7f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = quote.text,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp
                                        ),
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "— ${quote.author}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // TODAY'S QUESTION
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "TODAY'S QUESTION ❓",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = todayQuestion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = winAnswer,
                                    onValueChange = {
                                        winAnswer = it
                                        coroutineScope.launch {
                                            val p = app.database.userProfileDao().getUserProfile()
                                            if (p != null) {
                                                app.database.userProfileDao().updateUserProfile(p.copy(morningWinAnswer = it))
                                            }
                                        }
                                    },
                                    placeholder = { Text("Write your one win focus here...", color = TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = NavySurfaceHighlight,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite,
                                        focusedContainerColor = NavySurface,
                                        unfocusedContainerColor = NavySurface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Plan Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Plan (${todayTasks.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "+25 Early Riser XP",
                                    color = EmeraldSuccess,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(todayTasks) { task ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = if (task.isCompleted) EmeraldSuccess else GoldMuted.copy(alpha = 0.5f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite,
                                                maxLines = 1
                                            )
                                        }

                                        Surface(
                                            color = GoldPrimary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = task.categoryName,
                                                color = GoldBright,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Enter App Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                app.soundManager.stopAudioPlayback()
                                app.ttsManager.stop()
                                onDismissToApp()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_day_button")
                        ) {
                            Text(
                                text = "CONQUER THE DAY ⚡",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        ConfettiOverlay(trigger = confettiTrigger)
    }
}
