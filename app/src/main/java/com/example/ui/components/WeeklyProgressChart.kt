package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RiseRepository
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun WeeklyProgressChartSection(
    report: RiseRepository.WeeklyChartReport?,
    modifier: Modifier = Modifier
) {
    if (report == null) return

    val categoryColorMap = mapOf(
        "Spiritual" to Color(0xFF8B5CF6),
        "Law" to Color(0xFF3B82F6),
        "Mentorship" to Color(0xFF10B981),
        "Editing / Bezalim" to Color(0xFFF59E0B),
        "Fellowship" to Color(0xFFEC4899),
        "Personal" to Color(0xFF06B6D4)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. COMPARISON HEADER BANNER
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.CalmCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.CalmCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WEEKLY PACE & VELOCITY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.PeachSoft,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.thisWeekTotalCompleted} Tasks Completed",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "vs ${report.lastWeekTotalCompleted} last week",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilver
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (report.percentChangeVsLastWeek >= 0) EmeraldSuccess.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (report.percentChangeVsLastWeek >= 0) EmeraldSuccess else FlameOrange
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = if (report.percentChangeVsLastWeek >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (report.percentChangeVsLastWeek >= 0) EmeraldSuccess else FlameOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val sign = if (report.percentChangeVsLastWeek > 0) "+" else ""
                        Text(
                            text = "$sign${report.percentChangeVsLastWeek}%",
                            color = if (report.percentChangeVsLastWeek >= 0) EmeraldSuccess else FlameOrange,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. DAILY CATEGORY-COLORED BAR CHART (Mon to Sun)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.CalmCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.CalmCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Daily Completions (Mon – Sun)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "Each segment shows tasks completed by category",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                val maxDailyCount = (report.days.maxOfOrNull { it.completedCount } ?: 1).coerceAtLeast(5)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    report.days.forEach { day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Value above bar
                            Text(
                                text = if (day.completedCount > 0) "${day.completedCount}" else "",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Stacked Bar
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavySurfaceElevated),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                if (day.completedCount > 0) {
                                    val barHeightRatio = (day.completedCount.toFloat() / maxDailyCount).coerceIn(0.1f, 1f)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((100 * barHeightRatio).dp)
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    ) {
                                        day.categoryBreakdown.forEach { (catName, count) ->
                                            val segmentRatio = count.toFloat() / day.completedCount
                                            val color = categoryColorMap[catName] ?: GoldPrimary
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(segmentRatio)
                                                    .background(color)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Day Label
                            Text(
                                text = day.dayName.take(3),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSilver,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. EFFORT BREAKDOWN RING CHART BY CATEGORY
        if (report.categoryDistribution.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.CalmCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.CalmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Weekly Effort by Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ring / Donut Chart
                        val total = report.categoryDistribution.values.sum().coerceAtLeast(1)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(110.dp)
                        ) {
                            Canvas(modifier = Modifier.size(96.dp)) {
                                var startAngle = -90f
                                report.categoryDistribution.forEach { (catName, count) ->
                                    val sweep = (count.toFloat() / total) * 360f
                                    val color = categoryColorMap[catName] ?: GoldPrimary
                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = 16f, cap = StrokeCap.Round),
                                        size = Size(size.width, size.height)
                                    )
                                    startAngle += sweep
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$total",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Goals",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        // Category Legends with Percentages
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            report.categoryDistribution.entries.sortedByDescending { it.value }.take(5).forEach { (cat, count) ->
                                val pct = ((count.toFloat() / total) * 100).toInt()
                                val color = categoryColorMap[cat] ?: GoldPrimary
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = TextWhite
                                        )
                                    }

                                    Text(
                                        text = "$pct% ($count)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
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
