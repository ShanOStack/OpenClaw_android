# OpenClaw Android Agent 🤖📱

**OpenClaw Android Agent** ek autonomous on-device AI phone assistant application hai jo Android phones par chalne ke liye design ki gayi hai. Ye device ki screen, hardware, settings aur sabhi third-party apps (WhatsApp, YouTube, Instagram, Gallery, SMS, Contacts) ko voice commands ke through control aur automate karta hai.

---

## 🌟 Key Features

1. **Online & Offline Dual-Mode Voice Engine**:
   - **Offline STT & TTS**: Android native speech recognition aur zero-latency offline Text-To-Speech.
   - **Voice Customization**: Pitch (aawaz ka tone/bhari-pan) aur Speed (bolne ki raftar) customize karne ke options.
2. **Complete Device Automation**:
   - Camera, Lock Screen, Home, Back, Notifications, Quick Settings, Screenshot and System Settings.
3. **Cross-App & In-App UI Automation**:
   - Android `AccessibilityService` ka use karke apps me click, search, text-type aur gestures perform karta hai.
4. **Multi-App Workflow (Complex Tasks)**:
   - Example: Gallery me pichle 10-15 minute ki photos dhoondhna -> WhatsApp open karna -> Contact dhoondhna -> Photo bhej dena.
5. **Today's App Activity Dashboard**:
   - `UsageStatsManager` ke through pure din ka app usage summary (kon sa app kab aur kitni der chala).
6. **Room Database & CSV Export**:
   - Har ek voice command aur action details automatically local database me store hoti hai.
   - Ek click me **CSV file generate** karke WhatsApp/Drive/Download folder me export/share kar sakte hain.

---

## 📂 Project Structure

```
OpenClawAndroidAgent/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/openclaw/agent/
│   │   │   ├── MainActivity.kt                # Jetpack Compose Navigation & Core Lifecycle
│   │   │   ├── OpenClawApplication.kt         # Application Base & DB singleton
│   │   │   ├── service/
│   │   │   │   └── OpenClawAccessibilityService.kt # UI Tree Node Reader, Click, Type & Gestures
│   │   │   ├── voice/
│   │   │   │   └── VoiceManager.kt            # Offline/Online Voice STT & TTS Engine
│   │   │   ├── engine/
│   │   │   │   └── TaskEngine.kt              # Complex Multi-App Automation & Intent Brain
│   │   │   ├── analytics/
│   │   │   │   └── UsageTracker.kt            # UsageStatsManager Daily Analytics
│   │   │   ├── data/
│   │   │   │   ├── TaskEntity.kt              # Room Database Model
│   │   │   │   ├── TaskDao.kt                 # Database DAO
│   │   │   │   ├── TaskDatabase.kt            # Local SQLite Room DB
│   │   │   │   └── CsvExporter.kt             # Task History to CSV File Generator
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── DashboardScreen.kt     # Glowing Voice Orb, Quick Chips & Usage Card
│   │   │       │   ├── TaskHistoryScreen.kt   # History List & Download CSV Button
│   │   │       │   └── SettingsScreen.kt      # Voice Pitch, Speed & Permission Toggles
│   │   │       └── theme/
│   │   └── res/xml/
│   │       ├── accessibility_service_config.xml
│   │       └── file_paths.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 How to Run & Build APK on Phone

1. **Android Studio Me Open Karein**:
   - Android Studio open karein -> `Open Project` par click karein.
   - Path select karein: `C:\Users\shant\.gemini\antigravity\scratch\OpenClawAndroidAgent`.
2. **Sync Project with Gradle Files**:
   - Android Studio automatically dependencies download kar lega.
3. **Apna Android Phone Connect Karein**:
   - Phone me `Developer Options` -> `USB Debugging` enable karke USB se connect karein (ya Wi-Fi Pair karein).
4. **Run Button (`Shift + F10`) Dabayein**:
   - App direct phone me install aur launch ho jayegi.
5. **App Permissions Grant Karein**:
   - **Accessibility Service**: Phone Settings -> Accessibility -> `OpenClaw Agent Core Controller` ko **ON** karein.
   - **Usage Access**: Settings me jaakar `OpenClaw Agent` ko Usage Data Access allow karein.
   - **Microphone & Contacts**: App ke first launch par microphone aur contacts allow karein.

---

## 🗣️ Supported Voice Commands & Triggers

- *"Suno maine jo abhi photo click ki hai usse WhatsApp se Neeraj ko bhej do"*
- *"YouTube pe sad song chalu karo"*
- *"Daddy ko call karo"*
- *"Camera open karo aur photo click karo"*
- *"Instagram open karo aur anshika ko verygood message karo"*
- *"Homescreen ka wallpaper change karo"*
- *"Message me OTP wale message delete kar do"*
- *"Lock screen"* / *"Home jao"* / *"Screenshot lo"* / *"Notification panel kholo"*
- *"Voice recording open karo"*
