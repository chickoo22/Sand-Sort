package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SandColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GoalChestsBar(
    goalColors: List<SandColor>,
    unlockedColors: Set<SandColor>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        goalColors.forEach { color ->
            val isUnlocked = unlockedColors.contains(color)
            GoalChestItem(
                color = color,
                isUnlocked = isUnlocked,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

@Composable
fun GoalChestItem(
    color: SandColor,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chestSparkle")
    val sparkleAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chestSparkleRot"
    )

    val scale by animateFloatAsState(
        targetValue = if (isUnlocked) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chestScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .testTag("chest_${color.id}")
    ) {
        // 3D Chest Container
        Box(
            modifier = Modifier
                .size(width = 66.dp, height = 54.dp)
                .shadow(
                    elevation = if (isUnlocked) 12.dp else 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = if (isUnlocked) color.color else Color.Black,
                    spotColor = if (isUnlocked) color.highlightColor else Color(0xFF6749C2)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRealisticChest(
                    color = color,
                    isUnlocked = isUnlocked,
                    sparkleAngle = sparkleAngle
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Label pill
        if (isUnlocked) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF33206E))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Done",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color.color.copy(alpha = 0.7f))
            )
        }
    }
}

/**
 * Draws a 3D wooden and metallic treasure chest with gem lock
 */
private fun DrawScope.drawRealisticChest(
    color: SandColor,
    isUnlocked: Boolean,
    sparkleAngle: Float
) {
    val w = size.width
    val h = size.height
    val cornerR = 10f

    // 1. Base Chest Body (Lower half)
    val bodyTop = h * 0.38f
    val bodyH = h - bodyTop - 3f

    // Chest wood gradient
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = if (isUnlocked) {
                listOf(color.highlightColor, color.color, color.shadowColor)
            } else {
                listOf(Color(0xFF3E246A), Color(0xFF241544), Color(0xFF160B2E))
            },
            startY = bodyTop,
            endY = h - 3f
        ),
        topLeft = Offset(3f, bodyTop),
        size = Size(w - 6f, bodyH),
        cornerRadius = CornerRadius(cornerR, cornerR)
    )

    // Wood horizontal plank seams
    drawLine(
        color = Color(0x33000000),
        start = Offset(6f, bodyTop + bodyH * 0.5f),
        end = Offset(w - 6f, bodyTop + bodyH * 0.5f),
        strokeWidth = 1.5f
    )

    // 2. Chest Lid (Upper curved lid)
    val lidH = h * 0.42f
    val lidTop = 3f

    if (isUnlocked) {
        // Open lid glowing light burst
        for (i in 0..4) {
            val rayAngle = Math.PI * (0.2 + i * 0.15)
            val rx = w / 2f + (22f * cos(rayAngle)).toFloat()
            val ry = lidTop - 6f
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE082), Color.Transparent),
                    startY = ry - 12f,
                    endY = bodyTop
                ),
                start = Offset(w / 2f, bodyTop),
                end = Offset(rx, ry - 10f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }

    // Arched Lid Rect
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = if (isUnlocked) {
                listOf(Color(0xFFFFD54F), color.color)
            } else {
                listOf(Color(0xFF4C2F80), Color(0xFF2C1952))
            },
            startY = lidTop,
            endY = bodyTop
        ),
        topLeft = Offset(2f, lidTop),
        size = Size(w - 4f, lidH),
        cornerRadius = CornerRadius(cornerR + 2f, cornerR + 2f)
    )

    // 3. Metallic Straps & Corner Brackets with Rivets
    val strapW = 8f
    val strapLeft1 = w * 0.22f - strapW / 2f
    val strapLeft2 = w * 0.78f - strapW / 2f

    val metalBrush = Brush.verticalGradient(
        colors = if (isUnlocked) {
            listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFFB78103))
        } else {
            listOf(Color(0xFF9FA8DA), Color(0xFF5C6BC0), Color(0xFF3949AB))
        }
    )

    // Left strap
    drawRoundRect(
        brush = metalBrush,
        topLeft = Offset(strapLeft1, lidTop),
        size = Size(strapW, h - lidTop - 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Right strap
    drawRoundRect(
        brush = metalBrush,
        topLeft = Offset(strapLeft2, lidTop),
        size = Size(strapW, h - lidTop - 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Gold/Iron Rivets
    val rivetR = 1.8f
    val rivetColor = if (isUnlocked) Color(0xFFFFF8E1) else Color(0xFFC5CAE9)
    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft1 + strapW / 2f, lidTop + 6f))
    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft1 + strapW / 2f, bodyTop + 6f))
    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft1 + strapW / 2f, h - 8f))

    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft2 + strapW / 2f, lidTop + 6f))
    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft2 + strapW / 2f, bodyTop + 6f))
    drawCircle(color = rivetColor, radius = rivetR, center = Offset(strapLeft2 + strapW / 2f, h - 8f))

    // 4. Golden Center Latch with Jewel Gem
    val latchW = 16f
    val latchH = 18f
    val latchX = w / 2f - latchW / 2f
    val latchY = bodyTop - 6f

    // Golden latch plate
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFE57F), Color(0xFFFFB300), Color(0xFFE65100))
        ),
        topLeft = Offset(latchX, latchY),
        size = Size(latchW, latchH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF795548),
        topLeft = Offset(latchX, latchY),
        size = Size(latchW, latchH),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 1.5f)
    )

    // Center Gem matching the sand color
    val gemSize = 8f
    val gemX = w / 2f - gemSize / 2f
    val gemY = latchY + latchH / 2f - gemSize / 2f
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(color.highlightColor, color.color, color.shadowColor),
            center = Offset(gemX + gemSize / 2f, gemY + gemSize / 2f),
            radius = gemSize
        ),
        topLeft = Offset(gemX, gemY),
        size = Size(gemSize, gemSize),
        cornerRadius = CornerRadius(2.5f, 2.5f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.8f),
        topLeft = Offset(gemX, gemY),
        size = Size(gemSize, gemSize),
        cornerRadius = CornerRadius(2.5f, 2.5f),
        style = Stroke(width = 1f)
    )

    // 5. If Unlocked: Sparkle stars around chest
    if (isUnlocked) {
        val rotRad = Math.toRadians(sparkleAngle.toDouble())
        drawSparkleStar(
            center = Offset(w * 0.85f, lidTop + 2f),
            radius = 6f,
            rotation = sparkleAngle,
            color = Color(0xFFFFE57F)
        )
        drawSparkleStar(
            center = Offset(w * 0.15f, bodyTop - 2f),
            radius = 4.5f,
            rotation = -sparkleAngle,
            color = Color(0xFFFFF9C4)
        )
    }

    // Outer chest border
    drawRoundRect(
        color = if (isUnlocked) Color(0xFFFFD54F) else Color(0x33FFFFFF),
        topLeft = Offset(2f, lidTop),
        size = Size(w - 4f, h - lidTop - 3f),
        cornerRadius = CornerRadius(cornerR, cornerR),
        style = Stroke(width = 1.5f)
    )
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
