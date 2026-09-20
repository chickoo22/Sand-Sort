package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdStoreTarget
import com.example.model.RewardedAdType
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    show: Boolean,
    adType: RewardedAdType?,
    isInterstitial: Boolean = false,
    storeTarget: AdStoreTarget = AdStoreTarget.GOOGLE_PLAY,
    isOnline: Boolean = true,
    onRewardEarned: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val totalDuration = if (isInterstitial) 3 else 5
    var secondsRemaining by remember(show) { mutableIntStateOf(totalDuration) }
    var rewardGranted by remember(show) { mutableStateOf(false) }

    LaunchedEffect(show) {
        secondsRemaining = totalDuration
        rewardGranted = false
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        rewardGranted = true
    }

    val progress by animateFloatAsState(
        targetValue = 1f - (secondsRemaining.toFloat() / totalDuration.toFloat()),
        animationSpec = tween(1000),
        label = "adProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF0080414))
            .testTag("rewarded_ad_dialog"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header Bar with Store Badge & Countdown Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ad Badge & Store Identification
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E1402)
                        )
                    }

                    Column {
                        Text(
                            text = if (isInterstitial) "Sponsored Interlude" else "Rewarded Sponsor Video",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        val connectionText = if (isOnline) "🟢 Wi-Fi/Net Connected" else "⚪ Offline Creative"
                        Text(
                            text = "$connectionText • ${storeTarget.displayName}",
                            fontSize = 10.sp,
                            color = if (isOnline) Color(0xFF69F0AE) else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Countdown & Close Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (secondsRemaining > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Reward in ${secondsRemaining}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    } else {
                        // Close / Claim Button
                        IconButton(
                            onClick = {
                                if (rewardGranted || isInterstitial) {
                                    onRewardEarned()
                                }
                                onDismiss()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                                .testTag("close_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Ad",
                                tint = Color(0xFF063816),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF00E676),
                trackColor = Color(0x33FFFFFF)
            )

            // 2. Middle Sponsor Creative Showcase Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1E103F), Color(0xFF13092A))
                        )
                    )
                    .border(1.5.dp, Color(0xFF4A318C), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Video Player Mock Visual Frame
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF9C27B0), Color(0xFF3F51B5), Color(0xFF00BCD4))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sand Master 3D: Epic Sort",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "4.9 ★ (1.2M Reviews)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Experience the #1 Top Trending relaxation puzzle. Download now on Google Play & Indus Appstore!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Store Install Button
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF00E676),
                        shadowElevation = 8.dp,
                        modifier = Modifier.clip(RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = "INSTALL FREE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF063816),
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Bottom Reward Status Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1F133F),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isInterstitial) "Level Progression" else (adType?.title ?: "Reward Ready"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = if (isInterstitial) {
                                "Tap Close when done to proceed to the next level."
                            } else {
                                adType?.rewardDescription ?: "Thank you for supporting this game!"
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    if (rewardGranted || isInterstitial) {
                        Button(
                            onClick = {
                                onRewardEarned()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("claim_ad_reward_button")
                        ) {
                            Text(
                                text = if (isInterstitial) "Continue" else "Claim",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF063816)
                            )
                        }
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFFFFD54F),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
        }
    }
}
