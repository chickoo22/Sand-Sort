package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.SandColor
import kotlin.math.sin

@Composable
fun PouringStreamEffect(
    fromCenter: Offset,
    toCenter: Offset,
    color: SandColor,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sandStream")

    // Flowing phase of the falling particles
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(240, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sandFlowPhase"
    )

    // Splash ripple expansion at destination neck
    val splashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashPhase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Stream starts at mouth edge of source bottle and arches into mouth of target bottle
        val startPoint = fromCenter
        val endPoint = toCenter

        // Parabolic trajectory arc peak
        val midX = (startPoint.x + endPoint.x) / 2f
        val peakY = minOf(startPoint.y, endPoint.y) - 30f
        val controlPoint = Offset(midX, peakY)

        val streamPath = Path().apply {
            moveTo(startPoint.x, startPoint.y)
            quadraticTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)
        }

        // 1. Soft atmospheric outer glow
        drawPath(
            path = streamPath,
            color = color.highlightColor.copy(alpha = 0.35f),
            style = Stroke(width = 16f, cap = StrokeCap.Round)
        )

        // 2. Base colorful sand stream
        drawPath(
            path = streamPath,
            brush = Brush.linearGradient(
                colors = listOf(color.highlightColor, color.color, color.shadowColor),
                start = startPoint,
                end = endPoint
            ),
            style = Stroke(width = 9f, cap = StrokeCap.Round)
        )

        // 3. Bright core highlight
        drawPath(
            path = streamPath,
            color = Color.White.copy(alpha = 0.75f),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        // 4. Cascading granular particles flowing down the arc
        val particleCount = 12
        for (i in 0 until particleCount) {
            val t = ((i.toFloat() / particleCount) + flowPhase * 0.4f) % 1f

            // Quadratic Bézier curve point: B(t) = (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
            val u = 1f - t
            val x = u * u * startPoint.x + 2 * u * t * controlPoint.x + t * t * endPoint.x
            val y = u * u * startPoint.y + 2 * u * t * controlPoint.y + t * t * endPoint.y

            // Subtle lateral dispersion jitter
            val jitter = sin((t * 20f + i).toDouble()).toFloat() * 3f

            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 2.2f,
                center = Offset(x + jitter, y)
            )
            drawCircle(
                color = color.highlightColor.copy(alpha = 0.7f),
                radius = 3.5f,
                center = Offset(x, y)
            )
        }

        // 5. Destination impact splash ripples & droplets at target bottle mouth
        val splashR = 8f + splashPhase * 18f
        val splashAlpha = (1f - splashPhase).coerceIn(0f, 1f) * 0.7f

        // Ripple ring
        drawCircle(
            color = color.highlightColor.copy(alpha = splashAlpha),
            radius = splashR,
            center = endPoint,
            style = Stroke(width = 2.5f)
        )

        // Splash droplet sparks
        for (d in 0..4) {
            val dropAngle = Math.PI * (0.8 + d * 0.35)
            val dropDist = 6f + splashPhase * 16f
            val dx = endPoint.x + (dropDist * Math.cos(dropAngle)).toFloat()
            val dy = endPoint.y - (dropDist * Math.sin(dropAngle)).toFloat() * 0.7f

            drawCircle(
                color = color.highlightColor.copy(alpha = splashAlpha),
                radius = 2f,
                center = Offset(dx, dy)
            )
        }
    }
}
