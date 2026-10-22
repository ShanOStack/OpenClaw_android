package com.openclaw.agent.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaw.agent.analytics.AppUsageRecord
import com.openclaw.agent.ui.theme.AccentGreen
import com.openclaw.agent.ui.theme.AccentRed
import com.openclaw.agent.ui.theme.CardDark
import com.openclaw.agent.ui.theme.DeepCharcoal
import com.openclaw.agent.ui.theme.ElectricViolet
import com.openclaw.agent.ui.theme.LightSlate
import com.openclaw.agent.ui.theme.NeonCyan
import com.openclaw.agent.voice.VoiceManager

@Composable
fun DashboardScreen(
    voiceState: VoiceManager.VoiceState,
    isA11yActive: Boolean,
    statusMessage: String,
    dailyUsage: List<AppUsageRecord>,
    onMicClick: () -> Unit,
    onSubmitCommand: (String) -> Unit,
    onEnableA11y: () -> Unit,
    onEnableUsageStats: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (voiceState == VoiceManager.VoiceState.LISTENING) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Accessibility Permission Notice
        item {
            if (!isA11yActive) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Accessibility Service Inactive", color = AccentRed, fontWeight = FontWeight.Bold)
                            Text("Device control aur apps automate karne ke liye service enable karein.", color = LightSlate, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onEnableA11y,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                        ) {
                            Text("Turn On", color = Color.White)
                        }
                    }
                }
            }
        }

        // Animated AI Voice Orb
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .scale(if (voiceState == VoiceManager.VoiceState.LISTENING || voiceState == VoiceManager.VoiceState.SPEAKING) pulseScale else 1f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonCyan.copy(alpha = 0.7f), ElectricViolet.copy(alpha = 0.3f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                        .clickable { onMicClick() }
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = CardDark,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (voiceState == VoiceManager.VoiceState.LISTENING) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Mic",
                                tint = if (voiceState == VoiceManager.VoiceState.LISTENING) NeonCyan else LightSlate,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = when (voiceState) {
                        VoiceManager.VoiceState.LISTENING -> "Listening to your voice..."
                        VoiceManager.VoiceState.PROCESSING -> "Processing command..."
                        VoiceManager.VoiceState.SPEAKING -> "Speaking..."
                        VoiceManager.VoiceState.IDLE -> "Tap orb or speak command"
                    },
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = statusMessage,
                    color = LightSlate,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Quick Voice Command Chips
        item {
            Text("Sample Multi-App Commands", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val samples = listOf(
                "Maine jo abhi photo click ki hai use whatsapp se neeraj ko bhej do",
                "YouTube pe sad song chalu karo",
                "Daddy ko call karo",
                "Camera open karo aur photo click karo",
                "Instagram open karo aur anshika ko verygood message karo",
                "Homescreen ka wallpaper change karo",
                "Message me OTP wale message delete karo",
                "Lock screen"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(samples) { sample ->
                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { onSubmitCommand(sample) }
                    ) {
                        Text(
                            text = sample,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Manual Text / Voice Command Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type command or ask question...", color = LightSlate) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onSubmitCommand(textInput)
                                textInput = ""
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                    }
                }
            }
        }

        // Daily Usage Summary Dashboard Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today's App Activity Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (dailyUsage.isEmpty()) {
                            Button(
                                onClick = onEnableUsageStats,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                            ) {
                                Text("Grant Permission", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (dailyUsage.isEmpty()) {
                        Text(
                            "App usage dashboard track karne ke liye Usage Access permission grant karein.",
                            color = LightSlate,
                            fontSize = 12.sp
                        )
                    } else {
                        dailyUsage.take(5).forEach { usage ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(usage.appName, color = Color.White, fontSize = 13.sp)
                                Text("${usage.durationMinutes} mins", color = AccentGreen, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
