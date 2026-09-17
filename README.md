# Task App

A voice-activated task manager for Android, built with **Kotlin** and **Jetpack Compose**.

Wake the app with a spoken wake word and manage your tasks by voice — powered by the **Gemini** generative AI model.

## Features

- **Voice control** — add, update and delete tasks using natural-language voice commands.
- **AI command parsing** — Gemini (`gemini-2.0-flash`) interprets the spoken command and maps it to a task action (ADD / UPDATE / DELETE).
- **Wake word** — Picovoice [Porcupine](https://picovoice.ai/platform/porcupine/) listens for "Hello Task Manager" to start a voice session.
- **Task management** — tasks are stored locally in a Room database.
- **Charts** — a pie chart summarises task completion.

## How it works

1. The wake-word service detects "Hello Task Manager" and starts voice recognition.
2. The user speaks a command, e.g. *"add a task to buy groceries tomorrow"*.
3. Gemini parses the command into a structured task action.
4. The action is executed against the local Room database and reflected in the Compose UI.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **AI:** Google Gemini (`generativeai` SDK)
- **Wake word:** Picovoice Porcupine
- **Voice recognition:** Android `SpeechRecognizer`
- **Networking:** Retrofit + OkHttp (Gemini API)
- **DI:** Hilt
- **Local storage:** Room

## Configuration

Two keys are required and read from `local.properties`:

```properties
GEMINI_API_KEY=your_gemini_api_key
PICO_VOICE_TOKEN=your_picovoice_access_key
```

These are injected as `BuildConfig` fields at build time.

## Build

Requires Android Studio and JDK 11.

```bash
./gradlew assembleDebug
```

## License

MIT — see [LICENSE](LICENSE).
