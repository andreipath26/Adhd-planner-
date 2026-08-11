# FocusFlow - Build & Test Guide

## Project Overview
**FocusFlow** is an ADHD-friendly planner app built with:
- **Language**: Kotlin
- **Framework**: Jetpack Compose (UI)
- **Database**: Room (SQLite)
- **Build System**: Gradle
- **Target**: Android 24+ (API Level 24+)
- **Compile SDK**: Android 36

## Features
✨ Dashboard with customizable widgets
🧠 Brain Dump voice/text capture
⏰ Visual Pomodoro-style timer
🎯 Task decomposition with AI
🔔 Distraction-free focus mode with notification shield
📊 Progress tracking & gamification
👥 Community leaderboard

---

## Prerequisites

### Windows / macOS / Linux
1. **Java Development Kit (JDK) 11+**
   - Download: https://www.oracle.com/java/technologies/downloads/#java11
   - Verify: `java -version`

2. **Android SDK**
   - Download Android Studio: https://developer.android.com/studio
   - Or install command-line tools: https://developer.android.com/studio#downloads

3. **Gradle** (included in project, but you can install separately)
   - Min version: 8.0

---

## Setup Instructions

### Step 1: Clone the Repository
```bash
git clone https://github.com/andreipath26/Adhd-planner-.git
cd Adhd-planner-
```

### Step 2: Set Environment Variables
```bash
# macOS / Linux
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Windows (PowerShell)
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:Path += ";$env:ANDROID_HOME\tools;$env:ANDROID_HOME\platform-tools"
```

### Step 3: Create Local Properties File
Create a file named `local.properties` in the root directory:
```properties
sdk.dir=/path/to/Android/Sdk
```

**Example paths:**
- macOS: `/Users/YourUsername/Library/Android/sdk`
- Linux: `/home/YourUsername/Android/Sdk`
- Windows: `C:\Users\YourUsername\AppData\Local\Android\Sdk`

### Step 4: Create .env File (Optional - for API keys)
```bash
cp .env.example .env
# Edit .env and add your Firebase API keys if needed
```

---

## Building the App

### Option A: Build Debug APK (Easiest)
```bash
./gradlew assembleDebug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

Install on device/emulator:
```bash
./gradlew installDebug
```

### Option B: Build Release APK
```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

### Option C: Build using Android Studio
1. Open Android Studio
2. **File → Open → Select project root**
3. Wait for Gradle sync
4. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Option D: Build AAB (Google Play)
```bash
./gradlew bundleRelease
```

**Output**: `app/build/outputs/bundle/release/app-release.aab`

---

## Testing the App

### On Physical Device
1. **Enable Developer Mode**: Settings → About Phone → Tap Build Number 7 times
2. **Enable USB Debugging**: Developer Options → USB Debugging
3. **Connect via USB**
4. Run:
   ```bash
   ./gradlew installDebug
   ```

### On Android Emulator
1. **Launch Emulator** (from Android Studio)
2. **Run**:
   ```bash
   ./gradlew installDebug
   ```

### Run App Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (on device/emulator)
./gradlew connectedAndroidTest

# With screenshot tests (Roborazzi)
./gradlew recordRoborazziDebug
./gradlew verifyRoborazziDebug
```

---

## Troubleshooting

### Issue: `ANDROID_HOME not set`
**Solution:**
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
```

### Issue: Gradle build fails - "Could not determine java version"
**Solution:**
```bash
./gradlew --version
# If needed, explicitly set Java 11:
export JAVA_HOME=/path/to/java11
```

### Issue: `local.properties` not found
**Solution:**
Create the file or run:
```bash
echo "sdk.dir=$(which android-sdk)" > local.properties
```

### Issue: Firebase configuration warning
**Note:** The app includes Firebase features but will work without `google-services.json`. A warning is normal.

### Issue: Compilation cache issues
**Solution:**
```bash
./gradlew clean
./gradlew build
```

---

## Project Structure

```
Adhd-planner-/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt           (Entry point)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainScreen.kt         (Main navigation)
│   │   │   │   │   ├── PlannerViewModel.kt   (Business logic)
│   │   │   │   │   ├── screens/              (5 main screens)
│   │   │   │   │   ├── components/           (Reusable UI)
│   │   │   │   │   └── theme/                (Colors & styles)
│   │   │   │   ├── data/
│   │   │   │   │   ├── AppDatabase.kt        (Room DB)
│   │   │   │   │   ├── dao/                  (Data access)
│   │   │   │   │   ├── model/                (Entity classes)
│   │   │   │   │   ├── repository/           (Data layer)
│   │   │   │   │   └── ai/                   (AI integration)
│   │   │   │   └── util/                     (Helpers)
│   │   │   ├── res/                          (Resources)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                             (Unit tests)
│   │   └── androidTest/                      (Integration tests)
│   ├── build.gradle.kts                      (App config)
│   └── proguard-rules.pro                    (Obfuscation)
├── gradle/                                   (Wrapper files)
├── build.gradle.kts                          (Root config)
├── settings.gradle.kts                       (Module config)
├── gradle.properties                         (Gradle settings)
├── .env.example                              (API keys template)
└── README.md
```

---

## Key Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose | Modern UI framework |
| Room | Local database |
| Retrofit + OkHttp | API calls |
| Moshi | JSON parsing |
| Coroutines | Async operations |
| Lifecycle | ViewModel & livedata |
| Firebase AI | AI task decomposition |
| Material3 | Design components |

---

## Development Commands

```bash
# Format code
./gradlew spotlessApply

# Run linter
./gradlew lint

# Check dependencies
./gradlew dependencies

# Build and run on device
./gradlew installDebug

# Get APK info
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# Profile build speed
./gradlew assemble --profile
```

---

## How to Get the APK File

After building successfully:

1. **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
2. **Release APK**: `app/build/outputs/apk/release/app-release.apk`
3. **AAB (for Play Store)**: `app/build/outputs/bundle/release/app-release.aab`

### Share APK via GitHub
1. Create a GitHub Release
2. Upload the APK file
3. Share download link

### Install APK Manually
```bash
# Via adb
adb install app/build/outputs/apk/debug/app-debug.apk

# Via file browser
# 1. Copy APK to phone via USB
# 2. Enable "Unknown Sources" in settings
# 3. Tap APK to install
```

---

## Next Steps

1. ✅ **Run the app locally**
2. ✅ **Test core features** (Dashboard, Planner, Focus, Brain Dump)
3. ✅ **Create a GitHub Release** with APK
4. ✅ **Share with team for testing**

---

## Support

For issues:
- Check Android Studio Logcat for errors
- Run `./gradlew clean build` to clear cache
- Verify JDK version: `java -version` should show 11+
- Review Firebase configuration if needed

**Happy planning with FocusFlow! 🚀**
