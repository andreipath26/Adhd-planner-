# ✨ FocusFlow - ADHD-Friendly Planner App

> **Your Brain Deserves Better.** A distraction-free, AI-powered planning companion built for ADHD minds.

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

**[Quick Start](#-quick-start)** • **[Features](#-features)** • **[Install](#-installation)** • **[Build](#-building)** • **[Docs](#-documentation)**

</div>

---

## 🧠 What is FocusFlow?

FocusFlow is a **complete planning ecosystem** designed specifically for people with ADHD. It features intelligent task decomposition, distraction-free focus modes, and a gentle, judgment-free interface that understands your brain works differently.

### The Problem We Solve
- ❌ Overwhelmed by big tasks? Can't break them down?
- ❌ Notifications derailing your focus sessions?
- ❌ Thoughts fleeting away? No way to capture them?
- ❌ Progress invisible? Motivation tanking?

### The FocusFlow Solution
- ✅ **AI Task Decomposition** - Break big goals into bite-sized micro-steps
- ✅ **Brain Dump Inbox** - Capture thoughts via voice or text, triage later
- ✅ **Focus Shield** - Distraction-free mode blocks notifications intelligently
- ✅ **Visual Timer** - See time remaining at a glance
- ✅ **Energy-Aware Scheduling** - Match tasks to your energy levels
- ✅ **Gamification** - Celebrate wins, build streaks, unlock badges
- ✅ **Community Leaderboard** - Connect & cheer others on

---

## ✨ Features

### 🎯 Dashboard
- Daily Intention Screen - Set your mindset for the day
- Customizable Widgets - Drag, drop, show/hide what matters
- Visual Timer Widget - Quick access Pomodoro sessions
- Weekly Overview - See your week at a glance
- Gentle Affirmations - Encouragement when you need it
- Mood & Energy Check-in - Track how you're feeling

### 📋 Smart Planner
- Day-by-Day Planning - Organize by energy levels & time blocks
- Color-Coded Tasks - Visual categorization (Low/Medium/High energy)
- Time Slot Organization - Morning, Afternoon, Evening blocks
- Micro-Step Decomposition - Break tasks into actionable pieces
- Spotlight Task - Pin your top priority for the day

### 🧠 Brain Dump Inbox
- Voice Dictation - Record thoughts hands-free
- Quick Text Capture - Rapid-fire idea entry
- Triage System - Convert dumps to tasks or archive
- Energy Level Hints - Tag thoughts by effort required
- Category Tags - Organize by topic

### ⏰ Distraction-Free Focus Mode
- Full-Screen Timer - Minimal, immersive interface
- Notification Shield - Blocks distracting alerts intelligently
- Smart Whitelist - Allow only emergency contacts
- Focus Audio - White noise, rain sounds, lo-fi beats
- Step-by-Step Progress - Tick off micro-steps as you go

### 📊 Progress & Achievements
- XP System - Earn points for completing tasks
- Daily Streaks - Build momentum day by day
- Badges & Milestones - Unlock achievements
- Focus Session History - Track deep work sessions
- Weekly Statistics - See your productivity patterns

### 👥 Community Leaderboard
- Global Leaderboard - See top focus session loggers
- Cheer System - Celebrate others' wins
- Anonymous Support - Connect without pressure

---

## 🎨 Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Database | Room (SQLite) |
| Architecture | MVVM + Repository Pattern |
| State Management | Kotlin Coroutines & Flow |
| AI Integration | Google Gemini API |
| Backend | Firebase (optional) |
| Build System | Gradle |
| Testing | JUnit, Roborazzi, Espresso |

---

## 🚀 Quick Start

### Prerequisites
- Android Studio 2023.1+ or Android SDK tools
- JDK 11+
- Android SDK API Level 36 (target) / API Level 24+ (minimum)

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/andreipath26/Adhd-planner-.git
   cd Adhd-planner-
   ```

2. **Create local.properties**
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```

3. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

**For detailed setup**, see [BUILD_AND_RUN.md](./BUILD_AND_RUN.md)

---

## 📦 Installation Options

### Option 1: GitHub Actions (Easiest)
1. Go to **Actions** tab
2. Download latest `debug-apk` artifact
3. Install: `adb install app-debug.apk`

### Option 2: Build Locally
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option 3: Physical Device
1. Enable Developer Mode (tap Build Number 7x)
2. Enable USB Debugging
3. Connect via USB
4. Run: `./gradlew installDebug`

---

## 🛠️ Building

### Build Commands

```bash
# Debug APK (development)
./gradlew assembleDebug

# Release APK (optimized)
./gradlew assembleRelease

# Android App Bundle (Google Play)
./gradlew bundleRelease

# Run on device/emulator
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

---

## 📚 Documentation

- **[BUILD_AND_RUN.md](./BUILD_AND_RUN.md)** - Setup & troubleshooting guide
- **[Architecture](#architecture)** - Code organization & patterns
- **[Contributing](#contributing)** - How to contribute

### Architecture

```
app/
├── java/com/example/
│   ├── MainActivity.kt              ← Entry point
│   ├── ui/
│   │   ├── MainScreen.kt            ← Navigation
│   │   ├── PlannerViewModel.kt       ← Logic
│   │   ├── screens/                 ← Main screens
│   │   ├── components/              ← UI components
│   │   └── theme/                   ← Design system
│   ├── data/
│   │   ├── AppDatabase.kt           ← Database
│   │   ├── dao/                     ← Data access
│   │   ├── model/                   ← Entities
│   │   ├── repository/              ← Data layer
│   │   └── ai/                      ← AI features
│   └── util/                        ← Helpers
└── res/                             ← Resources
```

---

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Screenshot tests (Roborazzi)
./gradlew recordRoborazziDebug
./gradlew verifyRoborazziDebug
```

---

## 🤝 Contributing

We welcome contributions!

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 🐛 Issues & Feedback

- **Report Issues**: [GitHub Issues](https://github.com/andreipath26/Adhd-planner-/issues)
- **Discussions**: [GitHub Discussions](https://github.com/andreipath26/Adhd-planner-/discussions)
- **Email**: andreipath@gmail.com

---

## 🎯 Roadmap

### Phase 1: Core Features ✅
- [x] Task management & planning
- [x] Brain dump inbox
- [x] Focus timer & session tracking
- [x] Task decomposition
- [x] Dashboard customization

### Phase 2: Social & Gamification 🚀
- [ ] Enhanced leaderboard system
- [ ] Achievement badges & streaks
- [ ] Weekly challenges
- [ ] Social sharing

### Phase 3: Intelligence 🧠
- [ ] Improved AI decomposition
- [ ] Productivity insights
- [ ] Smart scheduling
- [ ] Natural language task parsing

### Phase 4: Platform Expansion 📱
- [ ] iOS version
- [ ] Web dashboard
- [ ] API for integrations
- [ ] Wearable companion app

---

## 💡 Why FocusFlow?

**We believe:**
- ADHD brains aren't broken—they're just wired differently
- The right tools transform how you plan & execute
- Celebration matters as much as productivity
- You deserve software that fits you

**FocusFlow is:**
- Built WITH ADHD in mind, not as an afterthought
- Judgment-free and encouraging
- Customizable to your unique workflow
- Community-driven and open source

---

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Powered by [Google Gemini API](https://ai.google.dev/)
- Inspired by ADHD community feedback
- Designed for neurodiversity ❤️

---

<div align="center">

### ✨ Ready to Reclaim Your Focus? ✨

**[Download](#-installation) • [Contribute](#-contributing) • [Report Issues](#-issues--feedback)**

*Built with ❤️ for ADHD brains everywhere*

**⭐ If you find FocusFlow helpful, please star this repo!**

</div>

---

*Last Updated: August 2026 | Version: 1.0-alpha*
