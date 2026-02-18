# Weather Forecast App

A modern Android weather forecast application built with **Clean Architecture** and **MVI (Model-View-Intent)** design patterns using Kotlin and Jetpack Compose.

---

## 🌟 Key Features

### 🌤️ Weather Forecast
- **Today's Weather Details**: Displays current temperature, weather conditions, high/low temperatures, humidity, and wind speed.
- **7-Day Forecast**: Provides weather trends for the upcoming week to help users plan ahead.
- **Dynamic Weather Icons**: Visualizes weather conditions using official OpenWeatherMap icons integrated via the **Coil** library.

### 🏙️ City Management
- **Multi-city Support**: Pre-configured with 10+ major global cities (Taipei, Tokyo, London, etc.).
- **Real-time Search**: Supports searching by city name to quickly switch between locations of interest.

### 🔋 Performance & Reliability
- **Offline-first**: Implements a caching mechanism using **Room** database to allow viewing the last updated data even without an internet connection.
- **Error Handling**: Provides precise UI prompts and retry functionality for exceptions such as missing API keys or network disconnections.

### 🎨 User Interface
- **Material 3**: Fully adopts Material Design 3 specifications and components.
- **Visual Optimization**: The main screen features a **Linear Gradient** background to create a sky-like feel, paired with **Glassmorphism** design to enhance visual depth.

---

## 🛠️ Technical Architecture

This project strictly follows **Clean Architecture**, dividing the code into independent modules to ensure high testability and maintainability.

### Module Breakdown
- **`:app`**: Application entry point, handling Hilt injection and navigation management.
- **`:core`**
    - `network`: Retrofit configuration, API definitions, and DTOs.
    - `database`: Room setup, DAOs, and Entities.
    - `common`: Shared utilities, Dispatcher providers, and generic Result classes.
- **`:feature:forecast`**: Core weather forecast feature module, containing complete Domain, Data, and Presentation layers.

### MVI Unidirectional Data Flow
- **Intent**: User actions (e.g., `Refresh`, `SelectCity`).
- **State**: Single Source of Truth for the UI state, ensuring state synchronization.
- **Effect**: One-time events (e.g., `NavigateToCityList`, `ShowToast`).

---

## 🚀 Getting Started

### 1. API Key Configuration
This project uses the [OpenWeatherMap API](https://openweathermap.org/).

Please follow these steps to set up your environment:
1. Locate the `local.properties.example` file in the project root directory.
2. **Rename** the file to `local.properties`.
3. Fill in your API key in `local.properties`:
```properties
WEATHER_API_KEY=your_api_key_here
```

### 2. Environment Requirements
- **Android Studio**: Hedgehog (2023.1.1+) or newer
- **JDK**: 17
- **Gradle**: 8.7.3

### 3. Build & Run
```bash
# After syncing Gradle, click "Run" in Android Studio
# Or build via CLI
./gradlew assembleDebug
```

---

## 🧪 Test Coverage

The project implements comprehensive unit tests covering business logic across all layers.

- **Domain Layer**: Tests business logic branches for Use Cases.
- **Presentation Layer**: Tests ViewModel state machine transitions and Effect emissions.
- **Data Layer**: Tests Repository caching strategies (Cache-then-Network) and exception transformations.

**Run Tests:**
```bash
./gradlew test
```

---

## 📦 Project Structure

```text
├── app
├── core
│   ├── common       # Dispatchers, Result, UiError
│   ├── network      # Retrofit, ApiService, DTOs
│   └── database     # Room, DAOs, Entities
└── feature
    └── forecast     # Weather Feature (MVI + Clean Architecture)
        ├── domain   # Use Cases, Models, Repositories
        ├── data     # RepositoryImpl, Mappers, LocalData
        └── presentation # UI (Compose), ViewModel (MVI)
```
