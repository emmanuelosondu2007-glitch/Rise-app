package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.CelebrationEvent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun CelebrationModal(
    event: CelebrationEvent,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, GoldPrimary, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                val (emoji, title, subtitle, bonusText) = when (event) {
                    is CelebrationEvent.LevelUp -> Quad(
                        "👑",
                        "LEVEL UP!",
                        "You advanced to Level ${event.level}: ${event.title}",
                        "+${event.xpGained} XP"
                    )
                    is CelebrationEvent.CleanSweep -> Quad(
                        "✨",
                        "DAILY CLEAN SWEEP!",
                        "You conquered every single goal scheduled for today!",
                        "+${event.bonusCoins} 🪙 & +${event.bonusXp} XP Bonus"
                    )
                    is CelebrationEvent.PerfectWeek -> Quad(
                        "🏆",
                        "PERFECT WEEK ASCENDED!",
                        "You crushed 100% of your weekly targets! Legendary discipline.",
                        "+${event.bonusCoins} 🪙 & +${event.bonusXp} XP Big Bonus"
                    )
                    is CelebrationEvent.BadgeUnlocked -> Quad(
                        event.badge.iconEmoji,
                        "BADGE UNLOCKED!",
                        "${event.badge.title} — ${event.badge.description}",
                        "+${event.badge.xpBonus} XP"
                    )
                    is CelebrationEvent.SundayBounty -> Quad(
                        "🧭",
                        "SUNDAY STRATEGY COMPLETED!",
                        "You reviewed last week and set next week's course. You are mastering the habit!",
                        "+${event.bonusCoins} 🪙 & +${event.bonusXp} XP Bounty"
                    )
                    is CelebrationEvent.RewardRedeemed -> Quad(
                        event.reward.iconEmoji,
                        "REWARD UNLOCKED!",
                        "Enjoy your well-earned '${event.reward.title}'! You earned every bit of it.",
                        "-${event.reward.coinPrice} 🪙 spent"
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldPrimary.copy(alpha = 0.3f), NavySurfaceElevated)
                            )
                        )
                        .border(2.dp, GoldPrimary.copy(alpha = 0.7f), CircleShape)
                ) {
                    Text(text = emoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSilver,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = NavySurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = bonusText,
                        color = GoldBright,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("celebration_claim_button")
                ) {
                    Text(
                        text = "CLAIM VICTORY ⚡",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
