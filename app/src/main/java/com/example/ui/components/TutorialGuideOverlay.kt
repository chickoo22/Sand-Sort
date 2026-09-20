package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TutorialStep
import kotlin.math.roundToInt

@Composable
fun TutorialGuideOverlay(
    tutorialStep: TutorialStep?,
    targetBottlePosition: Offset?,
    onDismissTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tutorialStep == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "tutorialHand")

    // Gentle bounce motion for the pointing hand
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handBounce"
    )

    // Expanding pulse ripple circle at the fingertip
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fingerPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("tutorial_guide_overlay")
    ) {
        // 1. Top Tutorial Instructional Banner
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp, start = 16.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF281854),
                                Color(0xFF1B0E3D)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFD54F),
                                Color(0xFFFF7043),
                                Color(0xFFFFD54F)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glowing Tip Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4527A0))
                            .border(1.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tutorial Tip",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tutorialStep.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tutorialStep.instruction,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dismiss / Skip button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable(onClick = onDismissTutorial)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Skip Tutorial",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Animated Pointing Hand Cursor over the target bottle
        if (tutorialStep.showFinger && targetBottlePosition != null) {
            val handX = targetBottlePosition.x
            val handY = targetBottlePosition.y + bounceOffset

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Expanding tap wave ripples at the mouth of the target bottle
                val rippleRadius = 15f + (pulseProgress * 35f)
                val rippleAlpha = (1f - pulseProgress).coerceIn(0f, 1f) * 0.8f
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = rippleAlpha),
                    radius = rippleRadius,
                    center = targetBottlePosition,
                    style = Stroke(width = 3.5f)
                )
                drawCircle(
                    color = Color(0xFFFF7043).copy(alpha = rippleAlpha * 0.6f),
                    radius = rippleRadius * 0.7f,
                    center = targetBottlePosition,
                    style = Stroke(width = 2.5f)
                )

                // Draw Pointing Hand Glove
                // Fingertip aims right at (targetBottlePosition.x, targetBottlePosition.y)
                drawTutorialHandGlove(
                    tip = Offset(handX, handY),
                    isPourStep = tutorialStep.isPourStep
                )
            }
        }
    }
}

/**
 * Draws a cartoon pointing glove hand targeting the bottle mouth
 */
private fun DrawScope.drawTutorialHandGlove(tip: Offset, isPourStep: Boolean) {
    // Hand points downward at the bottle
    val fingerW = 16f
    val fingerLen = 32f
    val palmW = 34f
    val palmH = 34f
    val cuffW = 38f
    val cuffH = 12f

    val startX = tip.x
    val startY = tip.y - 12f // slightly above bottle mouth

    // Drop shadow
    val shadowPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = startX - palmW / 2f + 4f,
                top = startY - fingerLen - palmH + 6f,
                right = startX + palmW / 2f + 4f,
                bottom = startY + 6f,
                cornerRadius = CornerRadius(14f, 14f)
            )
        )
    }
    drawPath(path = shadowPath, color = Color(0x66000000))

    // 1. Extended Index Finger
    val fingerPath = Path().apply {
        moveTo(startX - fingerW / 2f, startY - fingerLen)
        lineTo(startX - fingerW / 2f, startY - 6f)
        cubicTo(
            startX - fingerW / 2f, startY,
            startX + fingerW / 2f, startY,
            startX + fingerW / 2f, startY - 6f
        )
        lineTo(startX + fingerW / 2f, startY - fingerLen)
        close()
    }
    drawPath(
        path = fingerPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF0EAE1), Color(0xFFFFFFFF), Color(0xFFE2D6C6)),
            startY = startY - fingerLen,
            endY = startY
        )
    )
    drawPath(
        path = fingerPath,
        color = Color(0xFF795548).copy(alpha = 0.6f),
        style = Stroke(width = 2f)
    )

    // 2. Glove Palm Body
    val palmTop = startY - fingerLen - palmH
    val palmRect = RoundRect(
        left = startX - palmW / 2f,
        top = palmTop,
        right = startX + palmW / 2f,
        bottom = startY - fingerLen + 4f,
        cornerRadius = CornerRadius(14f, 14f)
    )
    val palmPath = Path().apply { addRoundRect(palmRect) }
    drawPath(
        path = palmPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFEDE7F6), Color(0xFFD1C4E9)),
            startY = palmTop,
            endY = startY - fingerLen + 4f
        )
    )
    drawPath(
        path = palmPath,
        color = Color(0xFF512DA8).copy(alpha = 0.5f),
        style = Stroke(width = 2f)
    )

    // Folded thumb and other fingers details
    drawRoundRect(
        color = Color(0xFFE0E0E0),
        topLeft = Offset(startX - palmW / 2f + 4f, palmTop + 8f),
        size = androidx.compose.ui.geometry.Size(12f, 18f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 1.5f)
    )

    // 3. Golden Wrist Cuff
    val cuffTop = palmTop - cuffH + 3f
    val cuffRect = RoundRect(
        left = startX - cuffW / 2f,
        top = cuffTop,
        right = startX + cuffW / 2f,
        bottom = palmTop + 4f,
        cornerRadius = CornerRadius(6f, 6f)
    )
    val cuffPath = Path().apply { addRoundRect(cuffRect) }
    drawPath(
        path = cuffPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFFF8F00)),
            startY = cuffTop,
            endY = palmTop + 4f
        )
    )
    drawPath(
        path = cuffPath,
        color = Color(0xFFFF6F00),
        style = Stroke(width = 1.5f)
    )

    // Action indicator icon / badge above the cuff (e.g. green plus or golden sparkle)
    val badgeCenter = Offset(startX, cuffTop - 10f)
    drawCircle(
        color = if (isPourStep) Color(0xFF00E676) else Color(0xFFFFD54F),
        radius = 8f,
        center = badgeCenter
    )
    drawCircle(
        color = Color.White,
        radius = 8f,
        center = badgeCenter,
        style = Stroke(width = 1.5f)
    )
}
