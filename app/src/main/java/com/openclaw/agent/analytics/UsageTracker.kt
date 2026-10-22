package com.openclaw.agent.analytics

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import java.util.Calendar

data class AppUsageRecord(
    val packageName: String,
    val appName: String,
    val durationMinutes: Long,
    val lastTimeUsed: Long
)

class UsageTracker(private val context: Context) {

    fun hasUsagePermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun getTodayUsageSummary(): List<AppUsageRecord> {
        if (!hasUsagePermission()) return emptyList()

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            ?: return emptyList()

        val pm = context.packageManager
        val map = mutableMapOf<String, UsageStats>()

        for (stat in stats) {
            val existing = map[stat.packageName]
            if (existing == null || stat.lastTimeUsed > existing.lastTimeUsed) {
                map[stat.packageName] = stat
            }
        }

        return map.values
            .filter { it.totalTimeInForeground > 30 * 1000 } // active for > 30 seconds
            .map { stat ->
                val friendlyName = try {
                    val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    stat.packageName.substringAfterLast('.')
                }
                AppUsageRecord(
                    packageName = stat.packageName,
                    appName = friendlyName,
                    durationMinutes = stat.totalTimeInForeground / (1000 * 60),
                    lastTimeUsed = stat.lastTimeUsed
                )
            }
            .sortedByDescending { it.durationMinutes }
    }
}
