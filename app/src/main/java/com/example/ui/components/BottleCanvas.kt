package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.SandBottle
import com.example.model.SandColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BottleView(
    bottle: SandBottle,
    isSelected: Boolean,
    isHinted: Boolean,
    isPouringSource: Boolean,
    pourTargetIsRight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottleWidth: Dp = 56.dp,
    bottleHeight: Dp = 150.dp
) {
    // Selection pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "bottleAnimations")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Sparkle star rotation for completed bottle
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
    )

    // Sunbeam ray rotation for completed bottle
    val sunbeamRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunbeamRotation"
    )

    // Smooth physical pouring tilt animation
    val animatedTilt by animateFloatAsState(
        targetValue = if (isPouringSource) {
            if (pourTargetIsRight) 68f else -68f
        } else 0f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "pourTilt"
    )

    // Smooth physical lift & translate
    val animatedYOffset by animateDpAsState(
        targetValue = when {
            isPouringSource -> (-34).dp
            isSelected -> (-18).dp
            else -> 0.dp
        },
        animationSpec = tween(260, easing = FastOutSlowInEasing),
        label = "bottleYOffset"
    )

    val animatedXOffset by animateDpAsState(
        targetValue = when {
            isPouringSource -> if (pourTargetIsRight) 34.dp else (-34).dp
            else -> 0.dp
        },
        animationSpec = tween(260, easing = FastOutSlowInEasing),
        label = "bottleXOffset"
    )

    // Cork drop-down bounce when bottle completes
    val corkDropProgress by animateFloatAsState(
        targetValue = if (bottle.isCompleted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "corkDropProgress"
    )

    Box(
        modifier = modifier
            .offset(x = animatedXOffset, y = animatedYOffset)
            .graphicsLayer {
                rotationZ = animatedTilt
                // Rotate around mouth/spout region for physical pouring realism
                transformOrigin = TransformOrigin(0.5f, 0.12f)
            }
            .width(bottleWidth)
            .height(bottleHeight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("bottle_${bottle.id}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bottle dimensions
            val lipWidth = w * 0.58f
            val neckWidth = w * 0.44f
            val bodyWidth = w * 0.88f
            val neckHeight = h * 0.16f
            val lipHeight = h * 0.045f
            val bodyHeight = h * 0.79f
            val cornerR = bodyWidth * 0.30f

            val centerX = w / 2f
            val lipLeft = centerX - lipWidth / 2f
            val neckLeft = centerX - neckWidth / 2f
            val bodyLeft = centerX - bodyWidth / 2f
            val bodyTop = h - bodyHeight
            val bodyBottom = h - 6f

            // 0. Completed Bottle Radiant Sunbeam Background
            if (bottle.isCompleted) {
                drawCompletedSunbeams(
                    center = Offset(centerX, h / 2f),
                    radius = bodyWidth * 0.85f,
                    rotation = sunbeamRotation
                )
            }

            // 1. Selection or Hint Golden Aura Glow
            if (isSelected || isHinted) {
                val auraColor = if (isSelected) {
                    Color(0xFFFFD54F).copy(alpha = glowAlpha)
                } else {
                    Color(0xFF00E5FF).copy(alpha = glowAlpha)
                }
                // Ambient outer glow
                drawRoundRect(
                    color = auraColor.copy(alpha = glowAlpha * 0.35f),
                    topLeft = Offset(bodyLeft - 12f, bodyTop - 10f),
                    size = Size(bodyWidth + 24f, bodyHeight + 18f),
                    cornerRadius = CornerRadius(cornerR + 12f, cornerR + 12f),
                    style = Stroke(width = 10f)
                )
                // Crisp highlight ring
                drawRoundRect(
                    color = auraColor,
                    topLeft = Offset(bodyLeft - 6f, bodyTop - 4f),
                    size = Size(bodyWidth + 12f, bodyHeight + 8f),
                    cornerRadius = CornerRadius(cornerR + 6f, cornerR + 6f),
                    style = Stroke(width = 4f)
                )
            }

            // 2. Interior Path for Sand Clipping
            val interiorPath = Path().apply {
                moveTo(neckLeft + 4f, bodyTop)
                lineTo(neckLeft + 4f, lipHeight + 4f)
                lineTo(centerX + neckWidth / 2f - 4f, lipHeight + 4f)
                lineTo(centerX + neckWidth / 2f - 4f, bodyTop)
                addRoundRect(
                    RoundRect(
                        left = bodyLeft + 4f,
                        top = bodyTop,
                        right = bodyLeft + bodyWidth - 4f,
                        bottom = bodyBottom - 4f,
                        cornerRadius = CornerRadius(cornerR - 2f, cornerR - 2f)
                    )
                )
            }

            // 3. Draw Sand Inside Bottle
            clipPath(interiorPath) {
                // Background interior glass depth
                drawRect(
                    color = Color(0x1A000000),
                    topLeft = Offset(bodyLeft, bodyTop),
                    size = Size(bodyWidth, bodyHeight)
                )

                val maxLayers = bottle.capacity
                val layerH = (bodyHeight - 12f) / maxLayers
                val layersCount = bottle.layers.size

                for (i in 0 until layersCount) {
                    val colorObj = bottle.layers[i]
                    val layerBottom = bodyBottom - (i * layerH)
                    val layerTop = layerBottom - layerH

                    // Sand layer gradient fill
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorObj.highlightColor,
                                colorObj.color,
                                colorObj.shadowColor
                            ),
                            startY = layerTop,
                            endY = layerBottom
                        ),
                        topLeft = Offset(0f, layerTop),
                        size = Size(w, layerH)
                    )

                    // Granular sand texture speckles (deterministic based on layer & position)
                    for (dot in 0..8) {
                        val seed = (i * 37 + dot * 23)
                        val dotX = bodyLeft + 8f + (seed % (bodyWidth.toInt() - 16))
                        val dotY = layerTop + 5f + ((seed * 11) % (layerH.toInt() - 8))

                        drawCircle(
                            color = colorObj.highlightColor.copy(alpha = 0.65f),
                            radius = 1.6f,
                            center = Offset(dotX.toFloat(), dotY.toFloat())
                        )
                        drawCircle(
                            color = colorObj.shadowColor.copy(alpha = 0.5f),
                            radius = 1.3f,
                            center = Offset(dotX.toFloat() + 4f, dotY.toFloat() + 3f)
                        )
                    }

                    // Natural meniscus curved surface line
                    val wavePath = Path().apply {
                        moveTo(bodyLeft, layerTop)
                        cubicTo(
                            centerX - 10f, layerTop - 2.5f,
                            centerX + 10f, layerTop + 2.5f,
                            bodyLeft + bodyWidth, layerTop
                        )
                    }
                    drawPath(
                        path = wavePath,
                        color = colorObj.highlightColor.copy(alpha = 0.6f),
                        style = Stroke(width = 2f)
                    )
                }

                // Inner Glass ambient reflection
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.12f)
                        ),
                        startX = bodyLeft,
                        endX = bodyLeft + bodyWidth
                    ),
                    topLeft = Offset(bodyLeft, bodyTop),
                    size = Size(bodyWidth, bodyHeight)
                )
            }

            // 4. Draw Glass Flask Exterior Structure
            // Outer Glass Body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x66FFFFFF),
                        Color(0x33FFFFFF),
                        Color(0x5590CAF9)
                    ),
                    startY = bodyTop,
                    endY = bodyBottom
                ),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight - 6f),
                cornerRadius = CornerRadius(cornerR, cornerR),
                style = Stroke(width = 4f)
            )

            // Neck lines
            val neckPath = Path().apply {
                moveTo(neckLeft, bodyTop + 2f)
                lineTo(neckLeft, lipHeight + 2f)
                moveTo(centerX + neckWidth / 2f, lipHeight + 2f)
                lineTo(centerX + neckWidth / 2f, bodyTop + 2f)
            }
            drawPath(
                path = neckPath,
                color = Color(0x88FFFFFF),
                style = Stroke(width = 4f)
            )

            // Flask Rim / Lip
            drawRoundRect(
                color = Color(0xAAFFFFFF),
                topLeft = Offset(lipLeft, 2f),
                size = Size(lipWidth, lipHeight + 4f),
                cornerRadius = CornerRadius(4.5f, 4.5f),
                style = Stroke(width = 3.5f)
            )

            // Primary Glass Specular Sheen (curved highlight on the left edge)
            val highlightPath = Path().apply {
                moveTo(bodyLeft + 6f, bodyTop + 14f)
                lineTo(bodyLeft + 6f, bodyBottom - 24f)
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = 0.55f),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Secondary subtle highlight on the right edge
            val rightHighlightPath = Path().apply {
                moveTo(bodyLeft + bodyWidth - 6f, bodyTop + 22f)
                lineTo(bodyLeft + bodyWidth - 6f, bodyBottom - 32f)
            }
            drawPath(
                path = rightHighlightPath,
                color = Color.White.copy(alpha = 0.25f),
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )

            // Laboratory graduation measurement notches
            for (g in 1..3) {
                val notchY = bodyBottom - (g * (bodyHeight / 4f))
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(bodyLeft + 8f, notchY),
                    end = Offset(bodyLeft + 16f, notchY),
                    strokeWidth = 1.5f
                )
            }

            // 5. Wooden Cork Stopper when Completed (Animated bounce drop-in)
            if (corkDropProgress > 0.05f) {
                val corkW = neckWidth * 0.95f
                val corkH = neckHeight * 0.9f
                val corkLeft = centerX - corkW / 2f

                // Drop from above: when progress is 0, cork is at -30f; at 1f, cork is at 0f
                val startCorkTop = -35f
                val endCorkTop = 0f
                val currentCorkTop = startCorkTop + (endCorkTop - startCorkTop) * corkDropProgress

                // Cork wood gradient
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE5A663),
                            Color(0xFFC67B38),
                            Color(0xFF8D4E1B)
                        ),
                        startY = currentCorkTop,
                        endY = currentCorkTop + corkH
                    ),
                    topLeft = Offset(corkLeft, currentCorkTop),
                    size = Size(corkW, corkH),
                    cornerRadius = CornerRadius(4.5f, 4.5f)
                )

                // Wood grain lines
                drawLine(
                    color = Color(0xFF743B0E),
                    start = Offset(corkLeft + 4f, currentCorkTop + corkH * 0.35f),
                    end = Offset(corkLeft + corkW - 4f, currentCorkTop + corkH * 0.35f),
                    strokeWidth = 1.2f
                )
                drawLine(
                    color = Color(0xFF743B0E),
                    start = Offset(corkLeft + 5f, currentCorkTop + corkH * 0.68f),
                    end = Offset(corkLeft + corkW - 5f, currentCorkTop + corkH * 0.68f),
                    strokeWidth = 1.2f
                )

                // Cork rim border
                drawRoundRect(
                    color = Color(0xFF6B360D),
                    topLeft = Offset(corkLeft, currentCorkTop),
                    size = Size(corkW, corkH),
                    cornerRadius = CornerRadius(4.5f, 4.5f),
                    style = Stroke(width = 2f)
                )

                // Golden Sparkle stars around the cork
                if (corkDropProgress >= 0.8f) {
                    drawSparkleStar(
                        center = Offset(centerX + 22f, currentCorkTop - 4f),
                        radius = 8.5f,
                        rotation = sparkleRotation,
                        color = Color(0xFFFFE57F)
                    )
                    drawSparkleStar(
                        center = Offset(centerX - 24f, currentCorkTop + 14f),
                        radius = 6.5f,
                        rotation = -sparkleRotation,
                        color = Color(0xFFFFF9C4)
                    )
                }
            }
        }

        // 6. Bonus Tag badge if locked or bonus
        if (bottle.isLocked) {
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = (-20).dp)
                    .size(26.dp),
                shape = CircleShape,
                color = Color(0xFF4CAF50),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Bonus Tube",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Draws ambient sunbeam rays behind a completed bottle
 */
private fun DrawScope.drawCompletedSunbeams(
    center: Offset,
    radius: Float,
    rotation: Float
) {
    val rayCount = 12
    val rotRad = Math.toRadians(rotation.toDouble())
    for (i in 0 until rayCount) {
        val angle = rotRad + (i * 2 * Math.PI / rayCount)
        val endX = center.x + (radius * cos(angle)).toFloat()
        val endY = center.y + (radius * sin(angle)).toFloat()
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x55FFD54F), Color.Transparent),
                center = center,
                radius = radius
            ),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSparkleStar(
    center: Offset,
    radius: Float,
    rotation: Float,
    color: Color
) {
    val rotRad = Math.toRadians(rotation.toDouble())
    val path = Path()
    val points = 8
    for (i in 0 until points) {
        val angle = rotRad + (i * Math.PI / 4)
        val r = if (i % 2 == 0) radius else radius * 0.35f
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}
