# Task Manager

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**A powerful productivity application built with Kotlin and Jetpack Compose**

[Report Bug](../../issues) • [Request Feature](../../issues)

</div>

---

## About

Task Manager is a comprehensive productivity application designed to help users organize their daily tasks, set reminders, and track their progress. Built using modern Android development practices with Jetpack Compose and Clean Architecture, the app provides an intuitive and efficient task management experience with cloud synchronization capabilities.

### Key Features

- **Clean Architecture** with MVVM pattern for maintainable code
- **Jetpack Compose** for modern declarative UI
- **Cloud Sync** with RESTful API integration
- **Smart Notifications** with WorkManager scheduling
- **Offline-First** approach with Room Database
- **Material Design 3** following latest guidelines

---

## Features

### Task Management
- Create, edit, and delete tasks with ease
- Set task priorities (High, Medium, Low)
- Add detailed descriptions and notes
- Attach tags and labels for organization
- Mark tasks as complete with satisfaction animations
- Archive completed tasks
- Search and filter tasks by multiple criteria

### Reminders & Notifications
- Schedule task reminders with flexible timing
- Recurring task support (daily, weekly, monthly)
- Smart notification system using Firebase Cloud Messaging
- Snooze functionality for reminders
- Custom notification sounds and vibration patterns
- Notification channels for better control

### Categories & Organization
- Create custom categories for different projects
- Color-code categories for visual organization
- Nested subcategories support
- Drag-and-drop task reordering
- Quick task entry with shortcuts
- Batch operations for multiple tasks

### Cloud Synchronization
- Real-time sync across devices using RESTful APIs
- Offline mode with automatic sync when online
- Conflict resolution for concurrent edits
- Data backup to cloud storage
- Cross-device task sharing

### Productivity Insights
- Daily, weekly, and monthly task completion statistics
- Productivity charts and graphs
- Time tracking for tasks
- Goal setting and progress tracking
- Streak tracking for daily completions
- Export productivity reports

### User Experience
- Smooth Material Design 3 animations
- Dark mode with system theme detection
- Customizable app themes and accent colors
- Widget support for home screen quick access
- Gesture controls for common actions
- Adaptive layouts for tablets

---

## Tech Stack

### Core Technologies
- **Language:** Kotlin 1.9+
- **UI Framework:** Jetpack Compose
- **Architecture:** Clean Architecture + MVVM
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Jetpack Libraries
- **Room Database** - Local data persistence
- **ViewModel** - UI state management
- **LiveData & Flow** - Reactive data observation
- **Navigation Component** - Type-safe Compose navigation
- **DataStore** - Preference management
- **WorkManager** - Background task scheduling for reminders
- **Paging 3** - Efficient data loading for large lists

### Networking & API
- **Retrofit** - RESTful API client
- **OkHttp** - HTTP client with interceptors
- **Gson** - JSON serialization/deserialization
- **Coroutines** - Asynchronous network calls

### Dependency Injection
- **Hilt** - Compile-time dependency injection framework

### Background Processing
- **WorkManager** - Scheduled notifications and sync
- **Kotlin Coroutines** - Asynchronous operations
- **Flow** - Reactive streams

### Firebase Services
- **Firebase Cloud Messaging (FCM)** - Push notifications
- **Firebase Analytics** - User behavior tracking
- **Firebase Crashlytics** - Crash reporting

### UI & Design
- **Material Design 3 Components** - Modern UI elements
- **Accompanist** - Additional Compose utilities
- **Lottie** - Vector animations
- **Coil** - Image loading

### Testing
- **JUnit 5** - Unit testing
- **MockK** - Kotlin-friendly mocking
- **Turbine** - Flow testing
- **Espresso** - UI testing
- **Robolectric** - Android unit tests without emulator

---

## Architecture

The application follows **Clean Architecture** with clear separation of concerns:

```
app/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── entities/         # Database entities
│   │   └── database/         # Database configuration
│   ├── remote/
│   │   ├── api/              # Retrofit API interfaces
│   │   ├── dto/              # Data transfer objects
│   │   └── interceptors/     # Network interceptors
│   ├── repository/           # Repository implementations
│   └── mapper/               # Data mappers
│
├── domain/
│   ├── model/                # Domain models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic use cases
│
├── presentation/
│   ├── ui/
│   │   ├── screens/          # Screen composables
│   │   │   ├── home/
│   │   │   ├── task/
│   │   │   ├── category/
│   │   │   └── settings/
│   │   ├── components/       # Reusable UI components
│   │   └── theme/            # Material theming
│   ├── viewmodel/            # Screen ViewModels
│   ├── navigation/           # Navigation setup
│   └── util/                 # UI utilities
│
├── di/                       # Dependency injection modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
└── worker/                   # WorkManager workers
    ├── SyncWorker.kt
    └── ReminderWorker.kt
```

### Architecture Layers

**Presentation Layer**
- Composable UI screens
- ViewModels managing UI state
- Navigation logic

**Domain Layer**
- Business logic in UseCases
- Domain models
- Repository interfaces

**Data Layer**
- Repository implementations
- Local data sources (Room)
- Remote data sources (Retrofit)
- Data mappers

### Data Flow
```
UI (Composables)
    ↕
ViewModel (StateFlow/LiveData)
    ↕
UseCase (Business Logic)
    ↕
Repository (Data Abstraction)
    ↕
Data Sources (Room + Retrofit)
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK (API 24+)
- Kotlin 1.9+
- Gradle 8.0+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Chinmay-tayade/Task-App.git
cd Task-App
```

2. **Open in Android Studio**
   - Launch Android Studio
   - File > Open > Select the project directory

3. **Configure API Keys** (Optional - for cloud sync)
   
   Create a `local.properties` file in the root directory:
   ```properties
   API_BASE_URL="https://your-api-endpoint.com/"
   API_KEY="your_api_key_here"
   ```

4. **Sync Gradle**
   - Let Gradle sync all dependencies
   - This may take a few minutes

5. **Build the project**
```bash
./gradlew build
```

6. **Run the app**
   - Connect Android device or start emulator
   - Run > Run 'app'

---

## Configuration

### Firebase Setup (Optional)

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Download `google-services.json`
3. Place it in the `app/` directory
4. Enable Firebase Cloud Messaging and Analytics

### API Configuration

The app supports cloud synchronization. Configure your backend API in `gradle.properties`:

```properties
BASE_URL=https://api.taskmanager.com/v1/
```

---

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Build Variants
- `debug` - Debug build with logging
- `release` - Optimized production build
- `staging` - Testing with staging API

---

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
```bash
./gradlew jacocoTestReport
```

### Test Structure
```
test/
├── viewmodel/         # ViewModel tests
├── repository/        # Repository tests
├── usecase/          # UseCase tests
└── mapper/           # Mapper tests

androidTest/
├── dao/              # Database tests
├── ui/               # Compose UI tests
└── worker/           # WorkManager tests
```

---

## Code Quality

### Static Analysis
```bash
# Lint check
./gradlew lint

# Kotlin lint
./gradlew ktlintCheck

# Detekt static analysis
./gradlew detekt
```

### Code Formatting
```bash
./gradlew ktlintFormat
```

---

## Contributing

Contributions make the open-source community amazing! Any contributions are greatly appreciated.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Kotlin coding conventions
- Write unit tests for new features
- Update documentation as needed
- Keep PRs focused and small
- Write clear commit messages

---

## Roadmap

- [ ] Calendar integration
- [ ] Collaborative task lists
- [ ] Voice input for tasks
- [ ] AI-powered task suggestions
- [ ] Time tracking with Pomodoro timer
- [ ] Habit tracking
- [ ] Integration with Google Tasks
- [ ] Wear OS app
- [ ] Desktop companion app

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Author

**Chinmay Tayade**

- GitHub: [@Chinmay-tayade](https://github.com/Chinmay-tayade)
- LinkedIn: [chinmaytayade](https://linkedin.com/in/chinmaytayade)
- Email: chinmaytayade@outlook.com

---

## Acknowledgments

- Android Jetpack team for excellent libraries
- Material Design team for beautiful components
- Open-source community for continuous inspiration
- All contributors who help improve this project

---

<div align="center">

**Built with Kotlin and Jetpack Compose**

Made by Chinmay Tayade

</div>
