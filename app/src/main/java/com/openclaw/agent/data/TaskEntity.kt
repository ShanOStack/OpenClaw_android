package com.openclaw.agent.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val voicePrompt: String,
    val targetApp: String,
    val actionDetail: String,
    val status: String, // SUCCESS, FAILED, RUNNING
    val executionType: String // OFFLINE, ONLINE
)
