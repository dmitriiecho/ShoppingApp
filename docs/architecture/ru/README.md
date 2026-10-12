# Архитектура

[English version](../en/README.md)

Вариант: **`kmp`** (Android и iOS, Metro, Ktor) · эта страница в [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/ru/README.md) (Android, Hilt, Retrofit) · [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/ru/README.md) (Android, Metro, Ktor)

ShoppingApp — демонстрационный интернет-магазин для Android и iOS: каталог с поиском, карточка товара, корзина, которая хранится на устройстве и сверяется с сервером, промокоды и форма заказа. Небольшой сервер на Ktor из папки [`server/`](../../../server/README.ru.md) отдаёт каталог и проверяет корзину. Обе платформы работают на одном коде Kotlin Multiplatform: экраны, логика и данные общие, своё у платформ — только точки входа и немного платформенных деталей.

Эти страницы объясняют, как устроено приложение и почему именно так. На каждой — правила, причины, по которым они приняты, и короткие фрагменты кода проекта.

&nbsp;

## Коротко о модулях

Приложение разделено на четыре уровня, снизу вверх:

```text
apps/         shop (app, android, ios), uikit                приложения: общий код и вход для каждой платформы
feature/      catalog, cart, promo, checkout, settings       каждая фича — это ui + impl
crossfeature/ domain, data, ui, analytics                    код магазина, нужный двум фичам и больше
core/         designsystem, compose-utils, network, config   ничего не знает о магазине
```

Модуль не зависит от уровней выше своего, а фичи не зависят друг от друга. Оба правила проверяет сборка.

&nbsp;

## Разделы

Каждый раздел можно читать отдельно:

| Раздел | О чём |
|---|---|
| [Модули](modules.md) | уровни, куда класть новый код, что фича делает публичным, проверка графа модулей |
| [Навигация](navigation.md) | как приложение собирает фичи вместе, вкладки, результат экрана, deep links |
| [Экраны](screens.md) | из каких файлов состоит экран, UI state, события и эффекты, поля ввода, стабильность Compose, широкие экраны |
| [Данные](data.md) | use case'ы, репозитории, типы ошибок, локальное хранение, сеть, цены, договорённости с сервером |
| [Аналитика](analytics.md) | события, системы аналитики, просмотры экранов, что можно отправлять |
| [Сборка](build.md) | convention-плагины, каталог версий, стиль кода, CI, подпись, iOS-приложение |
| [Тесты](testing.md) | как пишутся тесты, где лежат тестовые помощники и как они называются, инструменты, виды тестов |

&nbsp;

## Стек

Основные библиотеки и инструменты:

| Область | Что используется |
|---|---|
| Платформы | Android (minSdk 26), iOS 18.5+ (iPhone); общий код на Kotlin Multiplatform |
| Язык и UI | Kotlin 2.4, Compose Multiplatform 1.12 (на Android — Jetpack Compose), Material 3 |
| Устройство экранов | MVVM: одно состояние на экран и разовые эффекты |
| DI | Metro (на этапе компиляции, плагин компилятора Kotlin) |
| Навигация | Navigation Compose (мультиплатформенная сборка JetBrains) с типизированными маршрутами |
| Данные | Room, DataStore, Paging 3 |
| Сеть | Ktor Client (движок OkHttp на Android, Darwin на iOS), kotlinx.serialization, Coil для картинок |
| Логирование | Kermit (logcat, консоль Xcode) |
| Сборка | Gradle 9.8, AGP 9.4, JDK 21, convention-плагины; targetSdk 37, compileSdk 37; Xcode для iOS-приложения |
| Сервер | Ktor 3.6, данные в JSON-файлах |
| Тесты | kotlin.test, AssertK, kotlinx-coroutines-test, Turbine, Ktor MockEngine, Robolectric, Roborazzi с ComposablePreviewScanner |
