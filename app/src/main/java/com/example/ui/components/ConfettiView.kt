package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val angleRad: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiOverlay(
    trigger: Long, // Pass timestamp to trigger a burst
    modifier: Modifier = Modifier
) {
    if (trigger <= 0) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFFAA00), // Amber
            Color(0xFFFF6B35), // Flame
            Color(0xFF10B981), // Emerald
            Color(0xFF06B6D4), // Cyan
            Color(0xFFF8FAFC), // White sparkle
            Color(0xFFA855F7)  // Purple
        )
        List(65) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 700f + 350f
            ConfettiParticle(
                initialX = 0.5f,
                initialY = 0.45f,
                angleRad = angle,
                speed = speed,
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = progress.value
            val alpha = ((1f - t) * 1.5f).coerceIn(0f, 1f)

            particles.forEach { p ->
                val originX = size.width * p.initialX
                val originY = size.height * p.initialY

                val distance = p.speed * t
                val gravity = 900f * t * t

                val currentX = originX + (kotlin.math.cos(p.angleRad.toDouble()) * distance).toFloat()
                val currentY = originY + (kotlin.math.sin(p.angleRad.toDouble()) * distance).toFloat() + gravity

                val rotation = p.rotationSpeed * t

                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2f,
                            center = Offset(currentX, currentY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                            size = Size(p.size, p.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
