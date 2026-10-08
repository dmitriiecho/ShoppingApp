# Тесты

[English version](../en/testing.md) · [Все разделы](README.md)

Тесты идут на JVM, без эмулятора: логика приложения, его слой данных и сервер. Приложение и сервер следуют одним правилам, поэтому тест читается одинаково с обеих сторон.

&nbsp;

## Запуск тестов

Каждая сборка запускает свои тесты:

```sh
./gradlew testDebugUnitTest                         # Android-модули
./gradlew :shared:domain:test :shared:analytics:test  # модули на чистом Kotlin
cd server && ./gradlew test                         # сервер
```

&nbsp;

## Как пишется тест

Короткий тест из проекта:

```kotlin
@Test
fun `discount is the promo code percent of the subtotal`() {
    val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

    assertThat(cart.discount()).isEqualTo(200)
}
```

- **Одно поведение — один тест.** Несколько независимых проверок в одном тесте прячут друг друга: первая упавшая останавливает остальные. Однотипные случаи собираются в одну таблицу `tableOf` из AssertK: она проверяет каждую строку и показывает все упавшие. Таблица не вызывает suspend-функции, поэтому случаи, которые идут на сервер, сначала все отправляются, а потом проверяются вместе: `assertThat(statuses.filterValues { it != BadRequest }).isEmpty()` перечисляет все неверные ответы.
- **Имя называет условие и ожидаемый результат**, в обратных кавычках: `` `cart with a changed price reports the new price` ``, а не `` `price changed` ``.
- **Подготовка, действие, проверка**, разделённые пустой строкой.
- **Данные теста — в самом тесте.** Построители с умолчаниями (`testCartItem(price = 2000)`) позволяют задать только значимые для теста поля. Общего набора данных на все тесты нет: его правка ломала бы чужие тесты.
- **Рабочие подмены, а не моки.** Репозитории — интерфейсы, поэтому тест берёт реализацию в памяти с настоящим поведением; библиотеки моков нет.
- **Без реального времени**: тесты корутин идут на `runTest` с его виртуальными часами.
- **Новый тест хотя бы раз видят падающим**: ломают код, который он проверяет, смотрят, как тест падает, и возвращают код. Тест, который ни разу не падал, может ничего не проверять.
- **Реальные файлы данных проверяются отдельно от поведения**, правилом («у каждой картинки каталога есть файл»), а не количеством.

Комментарии — по [стилю кода](build.md#стиль-кода): только там, где код не может сказать «почему».

&nbsp;

## Имена и место

Всё, что сделано для тестов, начинается с `Test` или `test`, как в Now in Android. Префикс `Fake` занят: `FakeInsightsClient` и `FakeAdTrackerClient` в `:apps:shop` — рабочий код, заглушки систем аналитики.

| Что | Имя | Пример |
|---|---|---|
| Тестовый класс | проверяемый файл + `Test`, в том же пакете | `Cart.kt` → `CartTest` |
| Подменная реализация | `Test` + что подменяет | `TestAnalyticsClient` |
| Построитель данных | `test` + модель | `testCartItem()`, `testCart()` |
| Функция-вход вида тестов | вид + `Test` | `networkTest {}`, `serverTest {}` |

Исключение из первой строки одно: проверка файлов `data/` сервера — `DataFilesTest`, потому что это данные, а не код.

Помощник, нужный тестам **двух модулей и больше**, лежит в **test fixtures** модуля, которому принадлежит подменяемое; нужный одному модулю — в его `src/test`. Это то же правило, что для [кода и компонентов](modules.md#куда-класть-новый-код):

| Помощник | Модуль | Source set |
|---|---|---|
| `testCartItem()`, `testCart()` | `:shared:domain` | `src/testFixtures` |
| `TestAnalyticsClient` | `:shared:analytics` | `src/testFixtures` |
| `networkTest {}` | `:core:network` | `src/testFixtures` |

Модуль берёт чужие fixtures через `testImplementation(testFixtures(project(":shared:domain")))`. Fixtures видят только публичный API своего модуля. [Проверка графа модулей](modules.md#проверка) смотрит только `api` и `implementation`, так что тестовые зависимости следуют уровням по договорённости, а не по проверке.

&nbsp;

## Инструменты

Библиотеки работают в Kotlin Multiplatform, кроме тех, что для кода только под Android:

| Инструмент | Для чего |
|---|---|
| `kotlin.test` | `@Test` и другие аннотации; тесты не импортируют `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | все проверки: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` сужает тип, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` и виртуальные часы |
| MockWebServer | локальный HTTP-сервер для сетевого кода |

Раннер скрыт за `kotlin.test`, поэтому тесты выглядят одинаково, хотя раннеры разные: JUnit4 в приложении (Robolectric работает только на нём), JUnit5 на сервере.

&nbsp;

## Виды тестов

Каждый вид тестов начинается с одной функции-входа, которая готовит всё, что этому виду нужно.

### Сеть

`networkTest {}` запускает MockWebServer и даёт клиент для API-интерфейса, который читает JSON так же, как приложение, — тем же [`networkJson`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkJson.kt):

```kotlin
@Test
fun `client error returns HttpError with its code`() = networkTest<TestApi> { server, api ->
    server.enqueue(MockResponse.Builder().code(404).build())

    val result = networkCall { api.item() }

    assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
}
```

### Сервер

`serverTest {}` запускает сервер в памяти через `testApplication` из Ktor на данных, которые передаёт тест (`testShopData(products = …)`), и даёт клиент, читающий JSON собственным `serverJson` сервера. Подробнее об API сервера — в [`server/README.md`](../../../server/README.ru.md).
