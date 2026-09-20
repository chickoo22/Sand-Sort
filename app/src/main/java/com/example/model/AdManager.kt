package com.example.model

/**
 * AdManager handles the ad monetization state and configurations
 * for Google Play Store (AdMob) and Indus Appstore (Indus Ads / AppLovin / Unity Ads).
 */
enum class RewardedAdType(
    val title: String,
    val rewardDescription: String,
    val buttonText: String
) {
    EXTRA_TUBE(
        title = "Unlock Extra Tube",
        rewardDescription = "Watch a short video to get 1 Extra Empty Bottle for this level!",
        buttonText = "Watch to Unlock (+1 Tube)"
    ),
    DOUBLE_COINS(
        title = "Double Your Reward",
        rewardDescription = "Watch a quick video to double your win bonus: +80 Coins!",
        buttonText = "Watch for 2X Coins"
    ),
    FREE_HINT(
        title = "Free Move Hint",
        rewardDescription = "Watch a short video to highlight the optimal next move!",
        buttonText = "Watch for Hint"
    )
}

enum class AdStoreTarget(val displayName: String, val networkName: String) {
    GOOGLE_PLAY(
        displayName = "Google Play Store",
        networkName = "Google Mobile Ads (AdMob)"
    ),
    INDUS_APPSTORE(
        displayName = "Indus Appstore",
        networkName = "Indus Ads / AppLovin MAX"
    )
}

data class AdConfig(
    val isSimulationMode: Boolean = true,
    val targetStore: AdStoreTarget = AdStoreTarget.GOOGLE_PLAY,
    val interstitialFrequencyLevels: Int = 2, // Show interstitial every 2 levels
    val googlePlayAdMobAppId: String = "ca-app-pub-3940256099942544~3347511713", // Official Google Test ID
    val indusAppstoreAppId: String = "indus-app-sand-sort-1001",
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917"
)

enum class NetworkPromptReason {
    NONE,
    DOUBLE_COINS,
    MORE_HINTS
}
