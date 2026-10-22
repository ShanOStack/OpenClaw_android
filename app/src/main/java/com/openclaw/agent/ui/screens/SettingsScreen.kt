package com.openclaw.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaw.agent.ui.theme.CardDark
import com.openclaw.agent.ui.theme.DeepCharcoal
import com.openclaw.agent.ui.theme.ElectricViolet
import com.openclaw.agent.ui.theme.LightSlate
import com.openclaw.agent.ui.theme.NeonCyan

@Composable
fun SettingsScreen(
    currentPitch: Float,
    currentSpeed: Float,
    onPitchChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenUsageSettings: () -> Unit
) {
    var pitch by remember { mutableFloatStateOf(currentPitch) }
    var speed by remember { mutableFloatStateOf(currentSpeed) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Agent Voice & System Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        // Voice Customization Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Voice Pitch / Tone", color = NeonCyan, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = pitch,
                    onValueChange = {
                        pitch = it
                        onPitchChange(it)
                    },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )
                Text("Pitch: ${String.format("%.1f", pitch)}x (Bhari se Patli aawaz)", color = LightSlate, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(14.dp))

                Text("Speech Speed / Rate", color = NeonCyan, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = speed,
                    onValueChange = {
                        speed = it
                        onSpeedChange(it)
                    },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )
                Text("Speed: ${String.format("%.1f", speed)}x (Slow to Fast)", color = LightSlate, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onTestVoice,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Test Current Voice", color = Color.White)
                }
            }
        }

        // System Permissions Shortcuts
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Device Access & Control", color = NeonCyan, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Accessibility Service", color = Color.White, fontSize = 14.sp)
                        Text("Apps me click aur automate karne ke liye", color = LightSlate, fontSize = 11.sp)
                    }
                    Button(onClick = onOpenAccessibilitySettings) {
                        Text("Open Settings", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("App Usage Access", color = Color.White, fontSize = 14.sp)
                        Text("Daily summary dashboard ke liye", color = LightSlate, fontSize = 11.sp)
                    }
                    Button(onClick = onOpenUsageSettings) {
                        Text("Open Settings", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
