package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NetworkPromptReason
import com.example.model.SandBottle
import com.example.ui.components.BannerAdView
import com.example.ui.components.BottleView
import com.example.ui.components.CelebrationOverlay
import com.example.ui.components.GoalChestsBar
import com.example.ui.components.NetworkRequiredDialog
import com.example.ui.components.PouringStreamEffect
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TutorialGuideOverlay

@Composable
fun SandGameScreen(
    viewModel: SandGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val bottleCenters = remember { mutableStateMapOf<Int, Offset>() }
    var rootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF160D33),
                        Color(0xFF100726),
                        Color(0xFF090317)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .onGloballyPositioned { rootCoordinates = it }
            .testTag("sand_game_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar: Settings, Level Pill (LVL X), Coins, Hearts
            TopGameHeader(
                level = uiState.currentLevel,
                coins = uiState.coins,
                hearts = uiState.hearts,
                onSettingsClick = { viewModel.setSettingsDialogVisible(true) }
            )

            // 2. Goal Chests Collection Row
            if (uiState.goalChests.isNotEmpty()) {
                GoalChestsBar(
                    goalColors = uiState.goalChests,
                    unlockedColors = uiState.unlockedChests
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Bottles Grid Area (Middle)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                BottlesPlayfield(
                    bottles = uiState.bottles,
                    selectedBottleId = uiState.selectedBottleId,
                    hint = uiState.hint,
                    activePour = uiState.activePour,
                    rootCoordinates = rootCoordinates,
                    onBottleClick = { viewModel.onBottleClicked(it) },
                    onPositioned = { id, offset ->
                        bottleCenters[id] = offset
                    }
                )
            }

            // 4. Bottom Action Bar (Undo, Hint, Rewarded Extra Tube, Restart)
            BottomActionBar(
                canUndo = uiState.canUndo,
                extraBottleAdded = uiState.extraBottleAdded,
                freeHintsRemaining = uiState.freeHintsRemaining,
                onUndo = { viewModel.undo() },
                onHint = { viewModel.requestHint() },
                onAddBottle = { viewModel.requestRewardedExtraBottle() },
                onRestart = { viewModel.restartLevel() }
            )

            // 5. Monetization Banner Ad (Google Play / Indus Appstore compliant)
            BannerAdView(
                storeTarget = uiState.targetStore,
                onBannerClick = { viewModel.setSettingsDialogVisible(true) },
                modifier = Modifier.navigationBarsPadding()
            )
        }

        // 6. Render Pouring Stream if active (connected across root coordinates)
        val pour = uiState.activePour
        if (pour != null) {
            val fromPos = bottleCenters[pour.fromBottleId]
            val toPos = bottleCenters[pour.toBottleId]
            if (fromPos != null && toPos != null) {
                PouringStreamEffect(
                    fromCenter = fromPos,
                    toCenter = toPos,
                    color = pour.color
                )
            }
        }

        // 7. Interactive Training / Tutorial Guidance Overlay for initial levels
        val targetPos = uiState.tutorialStep?.targetBottleId?.let { bottleCenters[it] }
        TutorialGuideOverlay(
            tutorialStep = uiState.tutorialStep,
            targetBottlePosition = targetPos,
            onDismissTutorial = { viewModel.dismissTutorial() }
        )

        // 8. Settings Dialog with Store & Monetization Hub
        SettingsDialog(
            show = uiState.showSettingsDialog,
            isSoundEnabled = uiState.isSoundEnabled,
            isVibrationEnabled = uiState.isVibrationEnabled,
            targetStore = uiState.targetStore,
            onToggleSound = { viewModel.toggleSound() },
            onToggleVibration = { viewModel.toggleVibration() },
            onRestartLevel = { viewModel.restartLevel() },
            onSelectTargetStore = { viewModel.setTargetStore(it) },
            onDismiss = { viewModel.setSettingsDialogVisible(false) }
        )

        // 9. Celebration Overlay on Win with 2X Rewarded Ad Claim
        CelebrationOverlay(
            show = uiState.showWinDialog,
            coinsEarned = 40,
            currentLevel = uiState.currentLevel,
            onNextLevel = { viewModel.nextLevel() },
            onClaimDoubleCoins = { viewModel.claimDoubleCoins() }
        )

        // 10. Rewarded & Interstitial Ad Experience Dialog
        RewardedAdDialog(
            show = uiState.isShowingRewardedAd || uiState.isShowingInterstitialAd,
            adType = uiState.activeRewardedType,
            isInterstitial = uiState.isShowingInterstitialAd,
            storeTarget = uiState.targetStore,
            isOnline = uiState.isOnline,
            onRewardEarned = {
                if (uiState.isShowingInterstitialAd) {
                    viewModel.onInterstitialCompleted()
                } else {
                    viewModel.onRewardedAdCompleted()
                }
            },
            onDismiss = { viewModel.dismissAd() }
        )

        // 11. Network Required Dialog (for Double Coins & >2 Hints)
        NetworkRequiredDialog(
            show = uiState.networkPromptReason != NetworkPromptReason.NONE,
            reason = uiState.networkPromptReason,
            onCheckConnection = { viewModel.retryNetworkAction() },
            onClaimOffline = { viewModel.claimOfflineCoins() },
            onDismiss = { viewModel.dismissNetworkPrompt() }
        )
    }
}

@Composable
private fun TopGameHeader(
    level: Int,
    coins: Int,
    hearts: String,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(42.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFF2C1952))
                .border(1.5.dp, Color(0x44FFFFFF), CircleShape)
                .testTag("settings_button")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        // Level Title Pill Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF241447),
            shadowElevation = 6.dp,
            modifier = Modifier
                .border(2.dp, Color(0xFF6749C2), RoundedCornerShape(20.dp))
                .testTag("level_badge")
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LVL $level",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }

        // Right Stats: Coins & Hearts
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coins Counter
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF241447),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp))
                    .testTag("coins_counter")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            // Hearts Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF241447),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .border(1.5.dp, Color(0xFFFF5252), RoundedCornerShape(16.dp))
                    .testTag("hearts_counter")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Lives",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = hearts,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BottlesPlayfield(
    bottles: List<SandBottle>,
    selectedBottleId: Int?,
    hint: com.example.model.HintMove?,
    activePour: PouringAnimationState?,
    rootCoordinates: LayoutCoordinates?,
    onBottleClick: (Int) -> Unit,
    onPositioned: (Int, Offset) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val count = bottles.size
        val isTwoRows = count > 5
        val (row1, row2) = if (isTwoRows) {
            val half = (count + 1) / 2
            Pair(bottles.take(half), bottles.drop(half))
        } else {
            Pair(bottles, emptyList<SandBottle>())
        }

        val bottleWidth = when {
            count <= 4 -> 64.dp
            count <= 5 -> 56.dp
            else -> 50.dp
        }
        val bottleHeight = when {
            isTwoRows -> 142.dp
            else -> 175.dp
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Row 1
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (count <= 4) 18.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row1.forEach { bottle ->
                    val isSelected = bottle.id == selectedBottleId
                    val isHinted = hint?.fromBottleId == bottle.id || hint?.toBottleId == bottle.id
                    val isPouringSource = activePour?.fromBottleId == bottle.id
                    val pourTargetIsRight = if (activePour != null) {
                        bottle.id < activePour.toBottleId
                    } else false

                    BottleView(
                        bottle = bottle,
                        isSelected = isSelected,
                        isHinted = isHinted,
                        isPouringSource = isPouringSource,
                        pourTargetIsRight = pourTargetIsRight,
                        onClick = { onBottleClick(bottle.id) },
                        bottleWidth = bottleWidth,
                        bottleHeight = bottleHeight,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            val root = rootCoordinates
                            if (root != null && root.isAttached && coordinates.isAttached) {
                                val mouthOffset = root.localPositionOf(coordinates, Offset(coordinates.size.width / 2f, 16f))
                                onPositioned(bottle.id, mouthOffset)
                            }
                        }
                    )
                }
            }

            // Row 2 (if present)
            if (isTwoRows && row2.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row2.forEach { bottle ->
                        val isSelected = bottle.id == selectedBottleId
                        val isHinted = hint?.fromBottleId == bottle.id || hint?.toBottleId == bottle.id
                        val isPouringSource = activePour?.fromBottleId == bottle.id
                        val pourTargetIsRight = if (activePour != null) {
                            bottle.id < activePour.toBottleId
                        } else false

                        BottleView(
                            bottle = bottle,
                            isSelected = isSelected,
                            isHinted = isHinted,
                            isPouringSource = isPouringSource,
                            pourTargetIsRight = pourTargetIsRight,
                            onClick = { onBottleClick(bottle.id) },
                            bottleWidth = bottleWidth,
                            bottleHeight = bottleHeight,
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                val root = rootCoordinates
                                if (root != null && root.isAttached && coordinates.isAttached) {
                                    val mouthOffset = root.localPositionOf(coordinates, Offset(coordinates.size.width / 2f, 16f))
                                    onPositioned(bottle.id, mouthOffset)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    canUndo: Boolean,
    extraBottleAdded: Boolean,
    freeHintsRemaining: Int,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onAddBottle: () -> Unit,
    onRestart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo Button
        BottomActionButton(
            icon = Icons.AutoMirrored.Filled.Undo,
            label = "Undo",
            badge = if (canUndo) "1" else null,
            enabled = canUndo,
            accentColor = Color(0xFFB388FF),
            onClick = onUndo,
            testTag = "undo_button"
        )

        // Extra Tube / Bottle Button (Rewarded Ad Action)
        BottomActionButton(
            icon = Icons.Default.Add,
            label = "+ Tube",
            badge = if (!extraBottleAdded) "AD" else "Max",
            enabled = !extraBottleAdded,
            accentColor = Color(0xFF00E676),
            onClick = onAddBottle,
            testTag = "add_bottle_button"
        )

        // Hint Button: First 2 hints per level are Free & Offline; after that, Rewarded AD with Wi-Fi / Net
        val hintBadge = if (freeHintsRemaining > 0) "$freeHintsRemaining Free" else "AD • Net"
        BottomActionButton(
            icon = Icons.Default.Lightbulb,
            label = "Hint",
            badge = hintBadge,
            accentColor = Color(0xFFFFD54F),
            onClick = onHint,
            testTag = "hint_button"
        )

        // Restart Button
        BottomActionButton(
            icon = Icons.Default.Refresh,
            label = "Reset",
            accentColor = Color(0xFFFF5252),
            onClick = onRestart,
            testTag = "restart_button"
        )
    }
}

@Composable
private fun BottomActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    enabled: Boolean = true,
    accentColor: Color = Color(0xFF7A58E6)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else if (enabled) 1f else 0.92f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    val alpha = if (enabled) 1f else 0.45f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Main Circular 3D Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = if (enabled) 8.dp else 1.dp,
                        shape = CircleShape,
                        ambientColor = accentColor,
                        spotColor = accentColor
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3B2376).copy(alpha = alpha),
                                Color(0xFF22114B).copy(alpha = alpha),
                                Color(0xFF150A30).copy(alpha = alpha)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                (if (enabled) accentColor else Color.White.copy(alpha = 0.3f)),
                                (if (enabled) accentColor.copy(alpha = 0.4f) else Color.Transparent)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) accentColor else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(26.dp)
                )
            }

            // Optional Badge Pill on top-right
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFF9800))
                            )
                        )
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3E2723)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = if (enabled) 0.9f else 0.4f)
        )
    }
}
