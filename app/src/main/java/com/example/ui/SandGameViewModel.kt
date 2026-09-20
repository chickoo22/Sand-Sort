package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.model.AdStoreTarget
import com.example.model.HintMove
import com.example.model.NetworkPromptReason
import com.example.model.RewardedAdType
import com.example.model.SandBottle
import com.example.model.SandColor
import com.example.model.SandLevels
import com.example.util.NetworkMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PouringAnimationState(
    val fromBottleId: Int,
    val toBottleId: Int,
    val color: SandColor,
    val count: Int
)

data class TutorialStep(
    val title: String,
    val instruction: String,
    val targetBottleId: Int?,
    val isPourStep: Boolean,
    val showFinger: Boolean
)

data class GameUiState(
    val currentLevel: Int = 1,
    val difficultyName: String = "EASY",
    val coins: Int = 680,
    val hearts: String = "Full",
    val bottles: List<SandBottle> = emptyList(),
    val selectedBottleId: Int? = null,
    val activePour: PouringAnimationState? = null,
    val goalChests: List<SandColor> = emptyList(),
    val unlockedChests: Set<SandColor> = emptySet(),
    val completedBottleIds: Set<Int> = emptySet(),
    val isLevelWon: Boolean = false,
    val canUndo: Boolean = false,
    val hint: HintMove? = null,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val showSettingsDialog: Boolean = false,
    val showWinDialog: Boolean = false,
    val extraBottleAdded: Boolean = false,
    val isTutorialActive: Boolean = true,
    val tutorialStep: TutorialStep? = null,
    val isShowingRewardedAd: Boolean = false,
    val activeRewardedType: RewardedAdType? = null,
    val isShowingInterstitialAd: Boolean = false,
    val targetStore: AdStoreTarget = AdStoreTarget.GOOGLE_PLAY,
    val freeHintsRemaining: Int = 2,
    val networkPromptReason: NetworkPromptReason = NetworkPromptReason.NONE,
    val isOnline: Boolean = true
)

class SandGameViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)
    val networkMonitor = NetworkMonitor(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoHistory = mutableListOf<List<SandBottle>>()
    private var initialLevelBottles = listOf<SandBottle>()

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
        loadLevel(1)
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregister()
    }

    fun dismissTutorial() {
        soundManager.playButtonTap()
        _uiState.update { it.copy(isTutorialActive = false, tutorialStep = null) }
    }

    private fun computeTutorialStep(
        levelNum: Int,
        bottles: List<SandBottle>,
        selectedBottleId: Int?,
        isTutorialActive: Boolean
    ): TutorialStep? {
        if (!isTutorialActive || levelNum > 3) return null

        if (selectedBottleId == null) {
            // Find best source bottle
            val recommendedMove = findRecommendedMove(bottles)
            val targetId = recommendedMove?.fromBottleId ?: bottles.firstOrNull { it.isNotEmpty && !it.isCompleted }?.id

            val title = when (levelNum) {
                1 -> "Level 1: Tap to Select"
                2 -> "Level 2: Select a Bottle"
                else -> "Level 3: Choose Sand"
            }
            val instruction = when (levelNum) {
                1 -> "Tap this bottle to pick up the top blue sand!"
                2 -> "Tap a bottle to lift its top color."
                else -> "Collect 4 matching layers to seal the bottle!"
            }

            return TutorialStep(
                title = title,
                instruction = instruction,
                targetBottleId = targetId,
                isPourStep = false,
                showFinger = targetId != null
            )
        } else {
            // Source is selected, find target
            val sourceBottle = bottles.find { it.id == selectedBottleId } ?: return null
            val matchingTarget = bottles.firstOrNull { it.id != selectedBottleId && canPour(sourceBottle, it) && it.isNotEmpty }
            val emptyTarget = bottles.firstOrNull { it.id != selectedBottleId && canPour(sourceBottle, it) && it.isEmpty }
            val target = matchingTarget ?: emptyTarget

            val title = "Tap to Pour"
            val instruction = if (target != null && target.isEmpty) {
                "Tap this empty bottle to pour and separate colors!"
            } else if (target != null) {
                "Tap this bottle to pour matching colors together!"
            } else {
                "No valid bottle to pour into. Tap another bottle to select it."
            }

            return TutorialStep(
                title = title,
                instruction = instruction,
                targetBottleId = target?.id,
                isPourStep = true,
                showFinger = target != null
            )
        }
    }

    private fun findRecommendedMove(bottles: List<SandBottle>): HintMove? {
        // 1. Prioritize pouring onto matching colors
        for (source in bottles) {
            if (source.isEmpty || source.isCompleted) continue
            val sourceTop = source.topColor ?: continue
            for (target in bottles) {
                if (target.id == source.id || target.isCompleted || target.isFull) continue
                if (target.isNotEmpty && target.topColor == sourceTop && canPour(source, target)) {
                    return HintMove(source.id, target.id)
                }
            }
        }
        // 2. Otherwise pour into an empty bottle (unless source is already pure uniform color)
        for (source in bottles) {
            if (source.isEmpty || source.isCompleted || source.isSingleColor) continue
            for (target in bottles) {
                if (target.id == source.id || target.isCompleted || target.isFull) continue
                if (target.isEmpty && canPour(source, target)) {
                    return HintMove(source.id, target.id)
                }
            }
        }
        return null
    }

    fun loadLevel(levelNum: Int) {
        val levelData = SandLevels.getLevel(levelNum)
        val initialBottles = levelData.bottles.mapIndexed { index, layers ->
            SandBottle(
                id = index,
                capacity = levelData.capacity,
                layers = layers,
                isCompleted = layers.size == levelData.capacity && layers.all { it == layers[0] }
            )
        }
        initialLevelBottles = initialBottles
        undoHistory.clear()

        val completedIds = initialBottles.filter { it.isCompleted }.map { it.id }.toSet()
        val completedColors = initialBottles.filter { it.isCompleted }.mapNotNull { it.topColor }.toSet()
        val isTutActive = levelNum <= 3

        val initialTutorial = computeTutorialStep(
            levelNum = levelNum,
            bottles = initialBottles,
            selectedBottleId = null,
            isTutorialActive = isTutActive
        )

        val levelDifficulty = SandLevels.getDifficulty(levelNum)

        _uiState.update {
            it.copy(
                currentLevel = levelNum,
                difficultyName = levelDifficulty,
                bottles = initialBottles,
                selectedBottleId = null,
                activePour = null,
                goalChests = levelData.goalChests,
                unlockedChests = completedColors,
                completedBottleIds = completedIds,
                isLevelWon = false,
                canUndo = false,
                hint = null,
                showWinDialog = false,
                extraBottleAdded = false,
                isTutorialActive = isTutActive,
                tutorialStep = initialTutorial,
                isShowingRewardedAd = false,
                activeRewardedType = null,
                isShowingInterstitialAd = false,
                freeHintsRemaining = 2,
                networkPromptReason = NetworkPromptReason.NONE
            )
        }
    }

    fun onBottleClicked(bottleId: Int) {
        val state = _uiState.value
        if (state.isLevelWon || state.activePour != null) return

        val clickedBottle = state.bottles.find { it.id == bottleId } ?: return

        // If clicking on completed bottle, ignore or deselect
        if (clickedBottle.isCompleted) {
            val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, null, state.isTutorialActive)
            _uiState.update { it.copy(selectedBottleId = null, hint = null, tutorialStep = updatedTut) }
            return
        }

        val selectedId = state.selectedBottleId
        if (selectedId == null) {
            // First selection: only allow non-empty bottles
            if (clickedBottle.isNotEmpty) {
                soundManager.playBottleTap()
                val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, bottleId, state.isTutorialActive)
                _uiState.update { it.copy(selectedBottleId = bottleId, hint = null, tutorialStep = updatedTut) }
            }
        } else if (selectedId == bottleId) {
            // Deselect
            soundManager.playBottleTap()
            val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, null, state.isTutorialActive)
            _uiState.update { it.copy(selectedBottleId = null, tutorialStep = updatedTut) }
        } else {
            // Attempt pour from selected to clicked
            val sourceBottle = state.bottles.find { it.id == selectedId }
            if (sourceBottle == null || sourceBottle.isEmpty) {
                val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, null, state.isTutorialActive)
                _uiState.update { it.copy(selectedBottleId = null, tutorialStep = updatedTut) }
                return
            }

            if (canPour(sourceBottle, clickedBottle)) {
                performPour(sourceBottle, clickedBottle)
            } else {
                // If can't pour into this bottle, but clicked bottle is non-empty, switch selection
                if (clickedBottle.isNotEmpty) {
                    soundManager.playBottleTap()
                    val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, bottleId, state.isTutorialActive)
                    _uiState.update { it.copy(selectedBottleId = bottleId, hint = null, tutorialStep = updatedTut) }
                } else {
                    soundManager.playBottleTap()
                    val updatedTut = computeTutorialStep(state.currentLevel, state.bottles, null, state.isTutorialActive)
                    _uiState.update { it.copy(selectedBottleId = null, tutorialStep = updatedTut) }
                }
            }
        }
    }

    private fun canPour(source: SandBottle, target: SandBottle): Boolean {
        if (source.isEmpty || target.isFull || target.isCompleted) return false
        val sourceTopColor = source.topColor ?: return false
        return target.isEmpty || target.topColor == sourceTopColor
    }

    private fun performPour(source: SandBottle, target: SandBottle) {
        val sourceTopColor = source.topColor ?: return
        val availableSpace = target.availableCapacity
        val topCount = source.topGroupCount
        val amountToPour = minOf(topCount, availableSpace)

        if (amountToPour <= 0) return

        // Save current bottles to undo history
        undoHistory.add(_uiState.value.bottles)

        // Set pouring animation state
        _uiState.update {
            it.copy(
                selectedBottleId = null,
                activePour = PouringAnimationState(
                    fromBottleId = source.id,
                    toBottleId = target.id,
                    color = sourceTopColor,
                    count = amountToPour
                ),
                hint = null
            )
        }
        soundManager.playPour()

        viewModelScope.launch {
            // Pouring animation duration to allow smooth bottle tilt and sand stream
            delay(750)

            // Update bottles state after pour
            val newSourceLayers = source.layers.dropLast(amountToPour)
            val newTargetLayers = target.layers + List(amountToPour) { sourceTopColor }

            val updatedBottles = _uiState.value.bottles.map { bottle ->
                when (bottle.id) {
                    source.id -> bottle.copy(layers = newSourceLayers)
                    target.id -> {
                        val isComp = newTargetLayers.size == bottle.capacity &&
                                newTargetLayers.all { it == newTargetLayers[0] }
                        bottle.copy(layers = newTargetLayers, isCompleted = isComp)
                    }
                    else -> bottle
                }
            }

            val newlyCompleted = updatedBottles.filter { it.isCompleted }.map { it.id }.toSet()
            val justCompleted = newlyCompleted - _uiState.value.completedBottleIds
            if (justCompleted.isNotEmpty()) {
                soundManager.playCorkPop()
            }

            val unlockedColors = updatedBottles.filter { it.isCompleted }.mapNotNull { it.topColor }.toSet()

            // Check Win Condition:
            // Every non-empty bottle must be uniform and full
            val isWon = updatedBottles.all { it.isEmpty || it.isCompleted }
            val nextTut = computeTutorialStep(
                _uiState.value.currentLevel,
                updatedBottles,
                null,
                _uiState.value.isTutorialActive && !isWon
            )

            _uiState.update {
                it.copy(
                    bottles = updatedBottles,
                    activePour = null,
                    completedBottleIds = newlyCompleted,
                    unlockedChests = unlockedColors,
                    canUndo = undoHistory.isNotEmpty(),
                    isLevelWon = isWon,
                    tutorialStep = nextTut
                )
            }

            if (isWon) {
                delay(300)
                soundManager.playLevelWin()
                _uiState.update { it.copy(showWinDialog = true) }
            }
        }
    }

    fun undo() {
        if (undoHistory.isEmpty() || _uiState.value.activePour != null) return
        val previousBottles = undoHistory.removeAt(undoHistory.size - 1)
        soundManager.playButtonTap()

        val completedIds = previousBottles.filter { it.isCompleted }.map { it.id }.toSet()
        val completedColors = previousBottles.filter { it.isCompleted }.mapNotNull { it.topColor }.toSet()
        val tut = computeTutorialStep(_uiState.value.currentLevel, previousBottles, null, _uiState.value.isTutorialActive)

        _uiState.update {
            it.copy(
                bottles = previousBottles,
                selectedBottleId = null,
                activePour = null,
                completedBottleIds = completedIds,
                unlockedChests = completedColors,
                canUndo = undoHistory.isNotEmpty(),
                isLevelWon = false,
                showWinDialog = false,
                hint = null,
                tutorialStep = tut
            )
        }
    }

    fun restartLevel() {
        soundManager.playButtonTap()
        loadLevel(_uiState.value.currentLevel)
    }

    fun nextLevel() {
        soundManager.playButtonTap()
        val currentLvl = _uiState.value.currentLevel
        val earnedCoins = 40

        // Interstitial Ad Trigger: Natural break every 2 levels for high ad revenue
        if (currentLvl % 2 == 0) {
            _uiState.update {
                it.copy(
                    coins = it.coins + earnedCoins,
                    showWinDialog = false,
                    isShowingInterstitialAd = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    coins = it.coins + earnedCoins,
                    showWinDialog = false
                )
            }
            loadLevel(currentLvl + 1)
        }
    }

    fun onInterstitialCompleted() {
        val nextLvl = _uiState.value.currentLevel + 1
        _uiState.update { it.copy(isShowingInterstitialAd = false) }
        loadLevel(nextLvl)
    }

    fun claimDoubleCoins() {
        soundManager.playButtonTap()
        val online = networkMonitor.checkIsOnline()
        if (!online) {
            // Offline: Require Wi-Fi or Internet to unlock 2X Coins reward
            _uiState.update { it.copy(networkPromptReason = NetworkPromptReason.DOUBLE_COINS) }
        } else {
            // Online: Proceed with Rewarded Sponsor Video
            _uiState.update {
                it.copy(
                    isShowingRewardedAd = true,
                    activeRewardedType = RewardedAdType.DOUBLE_COINS
                )
            }
        }
    }

    fun requestRewardedExtraBottle() {
        val state = _uiState.value
        if (state.extraBottleAdded || state.isLevelWon || state.activePour != null) return
        soundManager.playButtonTap()
        _uiState.update {
            it.copy(
                isShowingRewardedAd = true,
                activeRewardedType = RewardedAdType.EXTRA_TUBE
            )
        }
    }

    /**
     * Handles hint requests:
     * - The first 2 hints per level are completely FREE and work 100% OFFLINE.
     * - Requesting MORE hints than 2 requires an active Internet or Wi-Fi connection for the reward!
     */
    fun requestHint() {
        val state = _uiState.value
        if (state.isLevelWon || state.activePour != null) return
        soundManager.playButtonTap()

        if (state.freeHintsRemaining > 0) {
            // Free offline hint: use 1 of the 2 default free hints directly without ad or internet!
            _uiState.update { it.copy(freeHintsRemaining = it.freeHintsRemaining - 1) }
            executeShowHint()
        } else {
            // Player requires more hints than 2: Internet / Wi-Fi must be ON!
            val online = networkMonitor.checkIsOnline()
            if (!online) {
                _uiState.update { it.copy(networkPromptReason = NetworkPromptReason.MORE_HINTS) }
            } else {
                _uiState.update {
                    it.copy(
                        isShowingRewardedAd = true,
                        activeRewardedType = RewardedAdType.FREE_HINT
                    )
                }
            }
        }
    }

    fun requestRewardedHint() {
        requestHint()
    }

    fun retryNetworkAction() {
        soundManager.playButtonTap()
        val online = networkMonitor.checkIsOnline()
        _uiState.update { it.copy(isOnline = online) }
        if (online) {
            val pendingReason = _uiState.value.networkPromptReason
            _uiState.update { it.copy(networkPromptReason = NetworkPromptReason.NONE) }
            when (pendingReason) {
                NetworkPromptReason.DOUBLE_COINS -> {
                    _uiState.update {
                        it.copy(
                            isShowingRewardedAd = true,
                            activeRewardedType = RewardedAdType.DOUBLE_COINS
                        )
                    }
                }
                NetworkPromptReason.MORE_HINTS -> {
                    _uiState.update {
                        it.copy(
                            isShowingRewardedAd = true,
                            activeRewardedType = RewardedAdType.FREE_HINT
                        )
                    }
                }
                NetworkPromptReason.NONE -> Unit
            }
        }
    }

    fun dismissNetworkPrompt() {
        soundManager.playButtonTap()
        _uiState.update { it.copy(networkPromptReason = NetworkPromptReason.NONE) }
    }

    fun claimOfflineCoins() {
        soundManager.playButtonTap()
        _uiState.update { it.copy(networkPromptReason = NetworkPromptReason.NONE) }
        val currentLvl = _uiState.value.currentLevel
        val earnedCoins = 40
        _uiState.update {
            it.copy(
                coins = it.coins + earnedCoins,
                showWinDialog = false
            )
        }
        loadLevel(currentLvl + 1)
    }

    fun onRewardedAdCompleted() {
        val type = _uiState.value.activeRewardedType
        _uiState.update {
            it.copy(
                isShowingRewardedAd = false,
                activeRewardedType = null
            )
        }

        when (type) {
            RewardedAdType.EXTRA_TUBE -> {
                executeAddExtraBottle()
            }
            RewardedAdType.DOUBLE_COINS -> {
                val nextLvl = _uiState.value.currentLevel + 1
                val doubleCoins = 80
                _uiState.update {
                    it.copy(
                        coins = it.coins + doubleCoins,
                        showWinDialog = false
                    )
                }
                loadLevel(nextLvl)
            }
            RewardedAdType.FREE_HINT -> {
                executeShowHint()
            }
            null -> Unit
        }
    }

    fun dismissAd() {
        val wasInterstitial = _uiState.value.isShowingInterstitialAd
        _uiState.update {
            it.copy(
                isShowingRewardedAd = false,
                activeRewardedType = null,
                isShowingInterstitialAd = false
            )
        }
        if (wasInterstitial) {
            loadLevel(_uiState.value.currentLevel + 1)
        }
    }

    private fun executeAddExtraBottle() {
        val state = _uiState.value
        if (state.extraBottleAdded) return

        val newId = (state.bottles.maxOfOrNull { it.id } ?: 0) + 1
        val capacity = state.bottles.firstOrNull()?.capacity ?: 4
        val extraBottle = SandBottle(
            id = newId,
            capacity = capacity,
            layers = emptyList()
        )

        val updatedBottles = state.bottles + extraBottle
        val tut = computeTutorialStep(state.currentLevel, updatedBottles, state.selectedBottleId, state.isTutorialActive)
        _uiState.update {
            it.copy(
                bottles = updatedBottles,
                extraBottleAdded = true,
                tutorialStep = tut
            )
        }
    }

    private fun executeShowHint() {
        val bottles = _uiState.value.bottles
        val recommended = findRecommendedMove(bottles)
        if (recommended != null) {
            _uiState.update { it.copy(hint = recommended) }
            return
        }
        for (source in bottles) {
            if (source.isEmpty || source.isCompleted) continue
            for (target in bottles) {
                if (source.id == target.id || target.isCompleted || target.isFull) continue
                if (canPour(source, target)) {
                    _uiState.update { it.copy(hint = HintMove(source.id, target.id)) }
                    return
                }
            }
        }
    }

    fun setTargetStore(store: AdStoreTarget) {
        _uiState.update { it.copy(targetStore = store) }
    }

    fun toggleSound() {
        val newState = !_uiState.value.isSoundEnabled
        soundManager.isSoundEnabled = newState
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun toggleVibration() {
        val newState = !_uiState.value.isVibrationEnabled
        soundManager.isVibrationEnabled = newState
        _uiState.update { it.copy(isVibrationEnabled = newState) }
    }

    fun setSettingsDialogVisible(visible: Boolean) {
        soundManager.playButtonTap()
        _uiState.update { it.copy(showSettingsDialog = visible) }
    }
}
