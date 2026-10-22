package com.openclaw.agent

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.openclaw.agent.analytics.AppUsageRecord
import com.openclaw.agent.analytics.UsageTracker
import com.openclaw.agent.data.CsvExporter
import com.openclaw.agent.data.TaskDao
import com.openclaw.agent.engine.TaskEngine
import com.openclaw.agent.service.OpenClawAccessibilityService
import com.openclaw.agent.ui.screens.DashboardScreen
import com.openclaw.agent.ui.screens.SettingsScreen
import com.openclaw.agent.ui.screens.TaskHistoryScreen
import com.openclaw.agent.ui.theme.CardDark
import com.openclaw.agent.ui.theme.DeepCharcoal
import com.openclaw.agent.ui.theme.LightSlate
import com.openclaw.agent.ui.theme.NeonCyan
import com.openclaw.agent.ui.theme.OpenClawTheme
import com.openclaw.agent.voice.VoiceManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var voiceManager: VoiceManager
    private lateinit var taskEngine: TaskEngine
    private lateinit var usageTracker: UsageTracker
    private lateinit var taskDao: TaskDao

    private var voiceState by mutableStateOf(VoiceManager.VoiceState.IDLE)
    private var statusMessage by mutableStateOf("Ready for commands")
    private var isA11yActive by mutableStateOf(false)
    private var dailyUsage by mutableStateOf<List<AppUsageRecord>>(emptyList())

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions granted status
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskDao = (application as OpenClawApplication).database.taskDao()
        usageTracker = UsageTracker(this)

        voiceManager = VoiceManager(
            context = this,
            onVoiceResult = { spokenText ->
                statusMessage = "Heard: \"$spokenText\""
                taskEngine.processCommand(spokenText) { update ->
                    statusMessage = update
                }
            },
            onStateChanged = { state ->
                voiceState = state
            }
        )

        taskEngine = TaskEngine(this, voiceManager)

        requestRequiredPermissions()

        setContent {
            OpenClawTheme {
                MainAppScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isA11yActive = OpenClawAccessibilityService.isServiceRunning
        if (usageTracker.hasUsagePermission()) {
            dailyUsage = usageTracker.getTodayUsageSummary()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceManager.destroy()
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    @Composable
    private fun MainAppScreen() {
        var selectedTab by remember { mutableIntStateOf(0) }
        val scope = rememberCoroutineScope()
        val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = CardDark
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Agent Orb") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate,
                            indicatorColor = DeepCharcoal
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.History, contentDescription = "Tasks") },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate,
                            indicatorColor = DeepCharcoal
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate,
                            indicatorColor = DeepCharcoal
                        )
                    )
                }
            }
        ) { paddingValues ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        voiceState = voiceState,
                        isA11yActive = isA11yActive,
                        statusMessage = statusMessage,
                        dailyUsage = dailyUsage,
                        onMicClick = {
                            if (voiceState == VoiceManager.VoiceState.LISTENING) {
                                voiceManager.stopListening()
                            } else {
                                voiceManager.startListening()
                            }
                        },
                        onSubmitCommand = { cmd ->
                            statusMessage = "Executing: $cmd"
                            taskEngine.processCommand(cmd) { update ->
                                statusMessage = update
                            }
                        },
                        onEnableA11y = {
                            OpenClawAccessibilityService.openAccessibilitySettings(this@MainActivity)
                        },
                        onEnableUsageStats = {
                            usageTracker.openUsageAccessSettings()
                        }
                    )
                    1 -> TaskHistoryScreen(
                        tasks = tasks,
                        onDownloadCsv = {
                            scope.launch {
                                val allTasks = taskDao.getAllTasksSync()
                                if (allTasks.isNotEmpty()) {
                                    val file = CsvExporter.exportTasksToCsv(this@MainActivity, allTasks)
                                    CsvExporter.shareCsvFile(this@MainActivity, file)
                                } else {
                                    voiceManager.speak("CSV export karne ke liye abhi koi task history nahi hai.")
                                }
                            }
                        },
                        onClearHistory = {
                            scope.launch { taskDao.clearAll() }
                        }
                    )
                    2 -> SettingsScreen(
                        currentPitch = voiceManager.currentPitch,
                        currentSpeed = voiceManager.currentSpeed,
                        onPitchChange = { voiceManager.currentPitch = it },
                        onSpeedChange = { voiceManager.currentSpeed = it },
                        onTestVoice = {
                            voiceManager.speak("Namaste! Mai aapka OpenClaw AI assistant hu. Meri aawaz ab is tarah sunai degi.")
                        },
                        onOpenAccessibilitySettings = {
                            OpenClawAccessibilityService.openAccessibilitySettings(this@MainActivity)
                        },
                        onOpenUsageSettings = {
                            usageTracker.openUsageAccessSettings()
                        }
                    )
                }
            }
        }
    }
}
