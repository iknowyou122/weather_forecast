# Weather Forecast App

一個使用 Kotlin、Jetpack Compose 和 Clean Architecture 建置的 Android 天氣預報應用程式。

## 功能

- **今日天氣預報**: 顯示目前溫度、天氣狀況、最高/最低溫、濕度和風速
- **7 天預報**: 顯示未來 7 天的天氣預報
- **城市切換**: 提供 10+ 個城市可供選擇
- **搜尋功能**: 支援搜尋城市名稱
- **離線支援**: 使用 Room 資料庫快取天氣資料
- **錯誤處理**: 網路錯誤時顯示適當的錯誤訊息和重試機制

## 技術架構

### 架構模式
- **Clean Architecture**: 分層為 presentation、domain、data
- **MVI (Model-View-Intent)**: 單向資料流
- **Repository Pattern**: 抽象資料來源

### 技術棧
| 類別 | 技術 |
|------|------|
| 語言 | Kotlin |
| UI | Jetpack Compose |
| 依賴注入 | Hilt |
| 非同步 | Coroutines + Flow |
| 網路 | Retrofit + OkHttp |
| 資料庫 | Room |
| 圖片 | Coil |

### 模組結構
```
├── app                     # 應用程式入口
├── core
│   ├── common             # 通用工具 (Result, Dispatchers)
│   ├── network            # Retrofit, API Service, DTO
│   └── database           # Room Database, DAO, Entities
└── feature
    └── forecast           # 天氣預報功能 (Domain + Data + Presentation)
```

## 專案結構

```
app/
├── src/main/java/com/weather/weather_forecast/
│   ├── MainActivity.kt           # 主 Activity
│   ├── WeatherApplication.kt     # Hilt Application
│   ├── di/
│   │   └── AppModule.kt          # 應用程式層級 DI
│   └── ui/theme/
│       └── Theme.kt              # Compose 主題

core/common/
├── src/main/java/com/weather/core/common/
│   ├── Result.kt                 # 泛型 Result 類型
│   ├── DispatchersProvider.kt    # Coroutine Dispatchers
│   ├── UiError.kt               # UI 錯誤定義
│   └── di/
│       └── CommonModule.kt       # DI Module

core/network/
├── src/main/java/com/weather/core/network/
│   ├── api/
│   │   └── WeatherApiService.kt  # Retrofit API
│   ├── dto/
│   │   └── OneCallResponse.kt   # API DTOs
│   └── di/
│       └── NetworkModule.kt     # 網路 DI

core/database/
├── src/main/java/com/weather/core/database/
│   ├── WeatherDatabase.kt        # Room Database
│   ├── dao/
│   │   └── ForecastDao.kt       # Data Access Objects
│   ├── entity/
│   │   ├── CachedForecastEntity.kt
│   │   └── CachedDailyWeatherEntity.kt
│   └── di/
│       └── DatabaseModule.kt    # 資料庫 DI

feature/forecast/
├── src/main/java/com/weather/feature/forecast/
│   ├── domain/
│   │   ├── model/               # Domain Models
│   │   │   ├── City.kt
│   │   │   ├── Forecast.kt
│   │   │   ├── CurrentWeather.kt
│   │   │   └── DailyWeather.kt
│   │   ├── repository/          # Repository Interfaces
│   │   │   ├── WeatherRepository.kt
│   │   │   └── CityRepository.kt
│   │   └── usecase/             # Use Cases
│   │       ├── GetForecastUseCase.kt
│   │       ├── GetCitiesUseCase.kt
│   │       ├── GetSelectedCityUseCase.kt
│   │       └── SelectCityUseCase.kt
│   ├── data/
│   │   ├── local/
│   │   │   └── CityLocalDataSource.kt
│   │   ├── mapper/
│   │   │   └── ForecastMapper.kt
│   │   ├── repository/
│   │   │   ├── WeatherRepositoryImpl.kt
│   │   │   └── CityRepositoryImpl.kt
│   │   └── di/
│   │       ├── RepositoryModule.kt
│   │       └── ForecastModule.kt
│   └── presentation/
│       ├── forecast/
│       │   ├── ForecastContract.kt      # MVI State/Intent/Effect
│       │   ├── ForecastViewModel.kt
│       │   └── ForecastScreen.kt        # Compose UI
│       ├── citylist/
│       │   ├── CityListContract.kt
│       │   ├── CityListViewModel.kt
│       │   └── CityListScreen.kt
│       └── navigation/
│           ├── ForecastRoutes.kt
│           └── ForecastNavHost.kt
```

## API Key 設定

此應用程式使用 [OpenWeatherMap](https://openweathermap.org/) API。您需要取得 API Key：

### 1. 取得 API Key
1. 前往 [OpenWeatherMap](https://home.openweathermap.org/users/sign_up) 註冊帳號
2. 登入後前往 API Keys 頁面
3. 複製您的 API Key

### 2. 設定 API Key

在專案根目錄建立 `local.properties` 檔案：

```properties
WEATHER_API_KEY=your_api_key_here
```

**注意**: `local.properties` 不應該提交到 Git。專案 `.gitignore` 已設定忽略此檔案。

## 執行專案

### 需求
- Android Studio Hedgehog (2023.1.1) 或更新版本
- JDK 17
- Android SDK 34

### 步驟
1. Clone 專案
```bash
git clone <repository-url>
cd weather_forecast
```

2. 設定 API Key（見上方說明）

3. 開啟專案
```bash
# 或使用 Android Studio 開啟
```

4. 建置並執行
```bash
./gradlew :app:assembleDebug
```

或使用 Android Studio 的 Run 按鈕。

## 執行測試

```bash
# 執行所有測試
./gradlew test

# 執行特定模組測試
./gradlew :feature:forecast:test
```

## 支援的城市

預設支援 10 個城市：
1. Taipei, TW
2. Tokyo, JP
3. Naha, JP
4. London, GB
5. New York, US
6. San Francisco, US
7. Los Angeles, US
8. Berlin, DE
9. Marseille, FR
10. Sydney, AU

## 資料流架構

```
┌─────────────┐     Intent      ┌─────────────────┐
│   Compose   │ ───────────────>│                 │
│     UI      │                 │  ViewModel      │
│             │ <───────────────│                 │
└─────────────┘     State       └────────┬────────┘
     ^                                     │
     │ Effect                              │ UseCase
     │                                     ▼
     │                            ┌─────────────────┐
     │                            │   Repository    │
     │                            │   Interface     │
     │                            └────────┬────────┘
     │                                     │
     │                              ┌──────┴──────┐
     │                              ▼             ▼
     │                      ┌──────────┐    ┌──────────┐
     │                      │  Remote  │    │   Cache  │
     │                      │   API    │    │  (Room)  │
     │                      └──────────┘    └──────────┘
     │
     └──────────────────────────────────────────────
```

## 授權

MIT License
