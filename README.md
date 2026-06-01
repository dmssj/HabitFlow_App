# HabitFlow

HabitFlow — это современное и удобное Android-приложение для формирования и отслеживания полезных привычек. Оно помогает пользователям дисциплинировать себя, визуализировать прогресс и интегрирует интеллектуальные функции для улучшения пользовательского опыта.

## Описание приложения
Приложение позволяет пользователям создавать список ежедневных привычек, отмечать их выполнение, просматривать детальную статистику своей активности и использовать встроенные инструменты, такие как распознавание объектов через камеру для автоматизации рутины.

## Используемые технологии
*   **Kotlin** — основной язык разработки.
*   **Jetpack Compose** — современный декларативный UI.
*   **MVVM** — архитектурный паттерн презентационного слоя.
*   **Clean Architecture** — разделение на независимые слои (domain, data, presentation).
*   **Hilt** — внедрение зависимостей (Dependency Injection).
*   **Room** — локальная база данных.
*   **Firebase** (FCM, Remote Config, Firestore, Analytics, Crashlytics).
*   **AppMetrica** — аналитика и мониторинг.
*   **Yandex Login (Yandex ID)** — авторизация через внешний сервис.
*   **Yandex MapKit** — работа с картами и геолокацией.
*   **TensorFlow Lite** — искусственный интеллект для распознавания объектов.
*   **WorkManager** — фоновые задачи и периодические уведомления.
*   **Coroutines & Flow** — асинхронная работа.


## Архитектура проекта
Проект построен на многомодульной архитектуре:
- **:app** — точка входа, навигация и конфигурация Hilt.
- **:domain** — чистая бизнес-логика: модели, интерфейсы репозиториев и Use Cases.
- **:data** — реализация репозиториев, работа с БД, сетью и SDK.
- **:core** — общие компоненты, утилиты и базовые классы UI.
- **:feature-* ** — отдельные модули для каждого экрана/функционала (auth, main, statistics, detection, about).

---

# Где реализованы критерии

| Критерий | Файлы | Краткое описание |
| :--- | :--- | :--- |
| **Clean Architecture** | 8 | `:domain/*`, `:data/*`, `:feature-*` | Проект строго разделен на слои. Домен не зависит от Android SDK. |
| **WorkManager** | 6 | `HabitReminderWorker.kt`, `HabitFlowApplication.kt` | Периодическая задача (24ч) для отправки напоминаний о привычках. |
| **Compose Animation** | 4 | `HabitListScreen.kt`, `CreateHabitScreen.kt` | Анимация прогресс-бара (`animateFloatAsState`) и появление шаблонов (`AnimatedVisibility`). |
| **XML + Compose** | 4 | `activity_about.xml`, `AboutActivity.kt` | Экран "О приложении" использует XML layout с внедренным `ComposeView`. |
| **Product Flavors** | 4 | `app/build.gradle.kts` | Реализованы флаворы `free` и `pro` с управлением доступом через `IS_PRO`. |
| **Firebase** | 3 | `FirebasePushService.kt`, `FirebaseRemoteConfigService.kt` | Интеграция Push-уведомлений и удаленной конфигурации (Remote Config). |
| **AI (TensorFlow Lite)** | 3 | `TFLiteObjectDetectionService.kt` | Использование обученной модели для распознавания объектов на фото. |
| **Yandex Login** | 2 | `YandexAuthService.kt`, `LoginScreen.kt` | Полноценная авторизация через Yandex Auth SDK 3.1.0. |
| **AppMetrica / Crashlytics** | 1 | `AppMetricaAnalyticsService.kt`, `FirebaseCrashReporter.kt` | Сбор аналитики событий и отчетов о сбоях. |

