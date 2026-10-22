package com.openclaw.agent.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportTasksToCsv(context: Context, tasks: List<TaskEntity>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val exportDir = File(context.getExternalFilesDir(null), "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val csvFile = File(exportDir, "OpenClaw_Tasks_$timestamp.csv")

        FileWriter(csvFile).use { writer ->
            writer.append("ID,Timestamp,Date_Time,User_Voice_Prompt,Target_App,Action_Performed,Status,Execution_Mode\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            for (task in tasks) {
                val formattedDate = dateFormat.format(Date(task.timestamp))
                val safePrompt = task.voicePrompt.replace("\"", "\"\"")
                val safeAction = task.actionDetail.replace("\"", "\"\"")
                writer.append("${task.id},${task.timestamp},\"$formattedDate\",\"$safePrompt\",\"${task.targetApp}\",\"$safeAction\",\"${task.status}\",\"${task.executionType}\"\n")
            }
            writer.flush()
        }
        return csvFile
    }

    fun shareCsvFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "OpenClaw AI Execution History")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Download or Share CSV File").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
