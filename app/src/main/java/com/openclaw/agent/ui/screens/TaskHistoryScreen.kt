package com.openclaw.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaw.agent.data.TaskEntity
import com.openclaw.agent.ui.theme.AccentGreen
import com.openclaw.agent.ui.theme.AccentRed
import com.openclaw.agent.ui.theme.CardDark
import com.openclaw.agent.ui.theme.DeepCharcoal
import com.openclaw.agent.ui.theme.LightSlate
import com.openclaw.agent.ui.theme.NeonCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskHistoryScreen(
    tasks: List<TaskEntity>,
    onDownloadCsv: () -> Unit,
    onClearHistory: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Task Execution History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${tasks.size} actions recorded", color = LightSlate, fontSize = 12.sp)
            }
            Row {
                Button(
                    onClick = onDownloadCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "CSV", tint = DeepCharcoal)
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(" CSV Export", color = DeepCharcoal, fontWeight = FontWeight.Bold)
                }
                if (tasks.isNotEmpty()) {
                    IconButton(onClick = onClearHistory) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", tint = LightSlate)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Abhi tak koi task run nahi kiya gaya hai.", color = LightSlate)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tasks) { task ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.targetApp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    color = if (task.status == "SUCCESS") AccentGreen.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = task.status,
                                        color = if (task.status == "SUCCESS") AccentGreen else AccentRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Voice Command: \"${task.voicePrompt}\"",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Detail: ${task.actionDetail}",
                                color = LightSlate,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = dateFormat.format(Date(task.timestamp)),
                                color = LightSlate.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
