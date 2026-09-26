package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun PrivacyPolicyDialog(
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (!show) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E133D)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF6B4FD0), RoundedCornerShape(24.dp))
                .testTag("privacy_policy_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 540.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy & Safety",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_privacy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C2E1A))
                        .border(1.dp, Color(0xFF00E676).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✓ 100% Offline-Friendly & Zero Personal Data Collection\n" +
                               "Sand Sort is built to be safe, privacy-first, and fully compliant with Google Play Store Policies.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB9F6CA),
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Data Safety
                PolicyItem(
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFFFFD54F),
                    title = "1. Data Collection & Storage",
                    body = "We do NOT collect, transmit, or sell any personal data (such as names, emails, phone numbers, locations, contacts, photos, or files). All level progression, coin balances, and unlocked themes are stored strictly locally on your device using local storage."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Permissions Used
                PolicyItem(
                    icon = Icons.Default.Wifi,
                    iconTint = Color(0xFF00E5FF),
                    title = "2. Minimal Permissions (Least Privilege)",
                    body = "• INTERNET & NETWORK STATE: Used strictly to check connectivity when you voluntarily request rewarded bonuses (Double Coins, Extra Hints).\n" +
                            "• VIBRATE: Provides satisfying tactile feedback when sand pours or bottles clear (can be toggled off anytime in Settings).\n" +
                            "• ZERO sensitive or dangerous permissions requested."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Advertising & Family Safety
                PolicyItem(
                    icon = Icons.Default.Shield,
                    iconTint = Color(0xFFE040FB),
                    title = "3. Ads & Family Protection (COPPA)",
                    body = "• Ads are non-disruptive and adhere to Google Play Better Ads Standards.\n" +
                            "• No full-screen popups interrupt ongoing gameplay.\n" +
                            "• Rewarded bonuses are strictly opt-in by player choice.\n" +
                            "• Suitable for players of all ages."
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4FD0)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("privacy_got_it_button")
                ) {
                    Text(
                        text = "Got it",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicyItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    body: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF281C50))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 16.sp
            )
        }
    }
}
