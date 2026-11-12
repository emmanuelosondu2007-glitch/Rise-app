package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarm.AlarmScheduler
import com.example.data.model.Quote
import com.example.ui.theme.AfternoonSkyMid
import com.example.ui.theme.AfternoonSkyTop
import com.example.ui.theme.AfternoonWarmGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NightSkyMid
import com.example.ui.theme.NightSkyTop
import com.example.ui.theme.NightStarlight
import com.example.ui.theme.PeachSoft
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.SerifHeadingFontFamily
import com.example.ui.theme.SunriseCoral
import com.example.ui.theme.SunriseTwilight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class TimePeriod {
    MORNING, AFTERNOON, NIGHT
}

@Composable
fun TimeOfDaySkyHeader(
    userName: String,
    quote: Quote?,
    isSpeaking: Boolean,
    onToggleSpeak: () -> Unit,
    alarmHour: Int,
    alarmMinute: Int,
    alarmStatus: AlarmScheduler.AlarmStatus,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    val hour = cal.get(Calendar.HOUR_OF_DAY)

    val timePeriod = when (hour) {
        in 5..11 -> TimePeriod.MORNING
        in 12..16 -> TimePeriod.AFTERNOON
        else -> TimePeriod.NIGHT
    }

    val greeting = when (hour) {
        in 5..11 -> "Good morning,\n$userName"
        in 12..16 -> "Good afternoon,\n$userName"
        in 17..20 -> "Good evening,\n$userName"
        else -> "Good night,\n$userName"
    }

    val dateLabel = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val infiniteTransition = rememberInfiniteTransition(label = "skyPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val skyColors = when (timePeriod) {
        TimePeriod.MORNING -> listOf(
            SunriseTwilight,
            SunriseCoral.copy(alpha = 0.85f),
            PeachWarm.copy(alpha = 0.65f),
            NavyBackground
        )
        TimePeriod.AFTERNOON -> listOf(
            AfternoonSkyTop,
            AfternoonSkyMid,
            AfternoonWarmGlow.copy(alpha = 0.48f),
            NavyBackground
        )
        TimePeriod.NIGHT -> listOf(
            NightSkyTop,
            NightSkyMid,
            Color(0xFF131D3D),
            NavyBackground
        )
    }

    // Deterministic gentle star positions for night sky
    val stars = remember {
        listOf(
            Triple(0.12f, 0.16f, 2.2f),
            Triple(0.24f, 0.28f, 1.4f),
            Triple(0.35f, 0.12f, 1.8f),
            Triple(0.48f, 0.24f, 1.2f),
            Triple(0.62f, 0.14f, 2.0f),
            Triple(0.75f, 0.30f, 1.6f),
            Triple(0.88f, 0.19f, 2.4f),
            Triple(0.92f, 0.38f, 1.3f),
            Triple(0.18f, 0.44f, 1.5f),
            Triple(0.56f, 0.40f, 1.7f),
            Triple(0.69f, 0.52f, 1.2f),
            Triple(0.83f, 0.49f, 1.9f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colors = skyColors))
    ) {
        // Custom Canvas Sky Art (original soft horizon sun / warm sun glow / starry moonlit sky)
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            when (timePeriod) {
                TimePeriod.MORNING -> {
                    val sunCenter = Offset(w * 0.82f, h * 0.34f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GoldBright.copy(alpha = 0.55f * glowAlpha),
                                PeachWarm.copy(alpha = 0.30f * glowAlpha),
                                Color.Transparent
                            ),
                            center = sunCenter,
                            radius = w * 0.46f
                        ),
                        center = sunCenter,
                        radius = w * 0.46f
                    )
                    drawCircle(
                        color = PeachSoft.copy(alpha = 0.85f),
                        radius = w * 0.09f,
                        center = sunCenter
                    )
                }
                TimePeriod.AFTERNOON -> {
                    val sunCenter = Offset(w * 0.80f, h * 0.26f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AfternoonWarmGlow.copy(alpha = 0.55f * glowAlpha),
                                GoldBright.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = sunCenter,
                            radius = w * 0.50f
                        ),
                        center = sunCenter,
                        radius = w * 0.50f
                    )
                    drawCircle(
                        color = Color(0xFFFFF4DC).copy(alpha = 0.85f),
                        radius = w * 0.075f,
                        center = sunCenter
                    )
                }
                TimePeriod.NIGHT -> {
                    stars.forEachIndexed { idx, (xFrac, yFrac, r) ->
                        val twinkle = if (idx % 2 == 0) glowAlpha else (1.35f - glowAlpha).coerceIn(0.3f, 1f)
                        drawCircle(
                            color = NightStarlight.copy(alpha = 0.85f * twinkle),
                            radius = r.dp.toPx(),
                            center = Offset(w * xFrac, h * yFrac)
                        )
                    }
                    // Crescent Moon silhouette
                    val moonCenter = Offset(w * 0.84f, h * 0.27f)
                    val moonR = 20.dp.toPx()
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NightStarlight.copy(alpha = 0.22f * glowAlpha),
                                Color.Transparent
                            ),
                            center = moonCenter,
                            radius = moonR * 2.8f
                        ),
                        center = moonCenter,
                        radius = moonR * 2.8f
                    )
                    drawCircle(
                        color = NightStarlight.copy(alpha = 0.92f),
                        radius = moonR,
                        center = moonCenter
                    )
                    drawCircle(
                        color = NightSkyMid,
                        radius = moonR * 0.86f,
                        center = Offset(moonCenter.x + moonR * 0.38f, moonCenter.y - moonR * 0.18f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 18.dp, bottom = 18.dp)
        ) {
            // Subtle date & alarm whisper row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateLabel.uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    color = PeachSoft,
                    letterSpacing = 1.4.sp
                )

                val alarmTimeText = String.format(
                    "%d:%02d %s",
                    if (alarmHour % 12 == 0) 12 else alarmHour % 12,
                    alarmMinute,
                    if (alarmHour >= 12) "PM" else "AM"
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = NavyDarkest.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (alarmStatus.needsPermissionFix) Color(0xFFFF5252).copy(alpha = 0.7f)
                        else PeachSoft.copy(alpha = 0.28f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Alarm,
                            contentDescription = "Morning Alarm",
                            tint = if (alarmStatus.needsPermissionFix) Color(0xFFFF6B6B) else GoldBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (alarmStatus.isScheduled) alarmTimeText else if (alarmStatus.needsPermissionFix) "Alarm: Fix" else "Alarm off",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (alarmStatus.needsPermissionFix) Color(0xFFFF8A8A) else TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Elegant Serif Greeting
            Text(
                text = greeting,
                fontFamily = SerifHeadingFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 42.sp,
                color = TextWhite,
                modifier = Modifier.testTag("hero_greeting")
            )

            // Clear Alarm Status line (green when scheduled, red + Fix button if permission needed)
            if (alarmStatus.isScheduled || alarmStatus.needsPermissionFix) {
                Spacer(modifier = Modifier.height(10.dp))
                if (alarmStatus.isScheduled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Alarm set for ${alarmStatus.formattedTime ?: "tomorrow"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else if (alarmStatus.needsPermissionFix) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF5252).copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5252))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Alarm NOT set: permission needed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFB4B4),
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
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Fix", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Gentle Quote Whisper + Listen Briefing Action
            if (quote != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NavyDarkest.copy(alpha = 0.42f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PeachSoft.copy(alpha = 0.22f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleSpeak() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "“${quote.text}”",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = TextWhite.copy(alpha = 0.92f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "— ${quote.author}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PeachSoft.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onToggleSpeak,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSpeaking) FlameOrange.copy(alpha = 0.25f)
                                    else GoldPrimary.copy(alpha = 0.16f)
                                )
                                .testTag("home_tts_speak_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop Briefing" else "Listen to Morning Briefing",
                                tint = if (isSpeaking) FlameOrange else GoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
