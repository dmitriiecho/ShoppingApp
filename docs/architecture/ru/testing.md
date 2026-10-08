# Тесты

[English version](../en/testing.md) · [Все разделы](README.md)

Тесты идут на JVM, без эмулятора: логика приложения, его слой данных и сервер. Приложение и сервер следуют одним правилам, поэтому тест читается одинаково с обеих сторон.

&nbsp;

## Запуск тестов

Каждая сборка запускает свои тесты, а CI запускает обе на каждом pull request:

```sh
./gradlew test                # приложение: все модули, по одному разу
cd server && ./gradlew test   # сервер
```

Android-модули гоняют unit-тесты только на debug (`shoppingapp.android.library` выключает release-тесты, которые бы их повторяли), поэтому один `test` охватывает и Android-модули, и модули на чистом Kotlin.

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
| Действие в тесте | глагол | `typeText()`, `keepCollecting()` |

Исключение из первой строки одно: проверка файлов `data/` сервера — `DataFilesTest`, потому что это данные, а не код.

Помощник, нужный тестам **двух модулей и больше**, лежит в **test fixtures** модуля, которому принадлежит подменяемое; нужный одному модулю — в его `src/test`. Это то же правило, что для [кода и компонентов](modules.md#куда-класть-новый-код):

| Помощник | Модуль | Source set |
|---|---|---|
| `testProduct()`, `testCartItem()`, `testCart()` | `:shared:domain` | `src/testFixtures` |
| `TestCartRepository` | `:shared:domain` | `src/testFixtures` |
| `TestAnalyticsClient`, `TestAnalytics` | `:shared:analytics` | `src/testFixtures` |
| `viewModelTest {}`, `keepCollecting()`, `typeText()` | `:core:compose-utils` | `src/testFixtures` |
| `networkTest {}`, `apiSample()` | `:core:network` | `src/testFixtures` |
| `databaseTest {}` | `:shared:data` | `src/test` |
| `TestProductRepository` | `:feature:catalog:impl` | `src/test` |
| `TestPromoCodeRepository` | `:feature:promo:impl` | `src/test` |

Модуль берёт чужие fixtures через `testImplementation(testFixtures(project(":shared:domain")))`. Fixtures видят только публичный API своего модуля. [Проверка графа модулей](modules.md#проверка) смотрит только `api` и `implementation`, так что тестовые зависимости следуют уровням по договорённости, а не по проверке.

&nbsp;

## Инструменты

Библиотеки работают в Kotlin Multiplatform, кроме тех, что для кода только под Android:

| Инструмент | Для чего |
|---|---|
| `kotlin.test` | `@Test` и другие аннотации; тесты не импортируют `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | все проверки: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` сужает тип, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` и виртуальные часы |
| [Turbine](https://github.com/cashapp/turbine) | разовые эффекты экрана: `effects.test { awaitItem() }` |
| MockWebServer | локальный HTTP-сервер для сетевого кода |
| [Robolectric](https://robolectric.org) | классы Android на JVM, только для кода, который без них не работает (Room) |

Раннер скрыт за `kotlin.test`, поэтому тесты выглядят одинаково, хотя раннеры разные: JUnit4 в приложении (Robolectric работает только на нём), JUnit5 на сервере.

&nbsp;

## Виды тестов

Каждый вид тестов начинается с одной функции-входа, которая готовит всё, что этому виду нужно.

### ViewModel

`viewModelTest {}` подменяет `Dispatchers.Main`, на котором работает `viewModelScope`, тестовым диспетчером на виртуальных часах `runTest`. ViewModel получает настоящие use case'ы поверх `Test`-репозиториев, поэтому тест задаёт данные и ответ сервера и проверяет, что получает экран:

```kotlin
@Test
fun `checkout of a cart the server changed asks to review the changes`() = viewModelTest {
    cartRepository.validationAnswer = { invalid(ItemIssue.Unavailable("2")) }
    val viewModel = cartViewModel()

    viewModel.effects.test {
        viewModel.onEvent(CartEvent.OnCheckoutClick)

        assertThat(awaitItem()).isEqualTo(snackBar(R.string.cart_changed))
    }
}
```

- **ViewModel создаётся внутри `viewModelTest {}`**, уже после подмены `Dispatchers.Main`.
- **На состояние держится подписка, как у экрана**: `keepCollecting(viewModel.uiState)` сразу после создания, дальше тест читает `viewModel.uiState.value`. Поток `stateIn(WhileSubscribed)` без подписчика остаётся с начальным значением. Эффекты разовые, поэтому их проверяет Turbine.
- **Ввод — через `typeText()`**: на устройстве Compose применяет изменение поля на следующем кадре, а у теста кадров нет.
- **Время виртуальное**: `advanceTimeBy()` проматывает debounce или анимацию без ожидания.
- **Сохранение на случай смерти процесса** проверяется, только когда это обычное значение в `SavedStateHandle` (диалог, способ оплаты): тот же handle передаётся новой ViewModel. Поля ввода сохраняются через Android `Bundle`, которого в JVM-тесте нет.
- **Запрос «в процессе»** — это `CompletableDeferred`, которого ждёт ответ: тест тем временем меняет корзину, а потом завершает его.
- **У событий аналитики нет `equals`**, поэтому `TestAnalytics.sentEvents` хранит их имя и параметры: `assertThat(analytics.sentEvents).containsExactly(CartClearedAnalyticsEvent().sent())`.

### База данных

`databaseTest {}` даёт пустую `ShoppingDatabase` в памяти. Room нужен Android `Context`, поэтому такие тестовые классы идут на Robolectric — это единственное место, где тест импортирует `org.junit`:

```kotlin
@RunWith(RobolectricTestRunner::class)
class LocalCartDataSourceTest {

    @Test
    fun `adding a product already in the cart adds to its quantity`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 2)

        cart.addItem(testProduct(id = "1"), quantity = 3)

        assertThat(cart.current().quantityOf("1")).isEqualTo(5)
    }
}
```

Robolectric работает на `targetSdk` приложения: `shoppingapp.android.library` даёт тестам каждого library-модуля этот SDK, его ресурсы и флаги JVM, которые Robolectric нужны на Java 17+. Без них он взял бы свой самый старый SDK, старше `minSdk` приложения. DataStore `Context` не нужен, поэтому настройки проверяются на настоящем DataStore во временном файле, без Robolectric.

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

### Контракт с сервером

Приложение и сервер описывают JSON API каждый в своих DTO. Согласованность держит [`server/api-samples/`](../../../server/api-samples/): по одному JSON-файлу на каждый запрос или ответ, на который опирается приложение. `apiSample("product.json")` читает файл на обеих сторонах, разбирая его `Json` своей стороны, а проверка сравнивает деревья JSON: порядок полей не важен, а переименованное, лишнее или пропавшее поле — важно:

| Сторона | Что проверяет |
|---|---|
| Сервер | принимает образцы запросов и отвечает в точности как образцы ответов |
| Приложение | отправляет запросы в точности как образцы запросов и разбирает образцы ответов в ожидаемые модели |

Изменение формата с одной стороны роняет тесты этой стороны, пока не будут изменены образец, а затем и другая сторона. Сборка объявляет папку входом каждой тестовой задачи, поэтому изменённый образец перезапускает тесты.

### Сервер

`serverTest {}` запускает сервер в памяти через `testApplication` из Ktor на данных, которые передаёт тест (`testShopData(products = …)`), и даёт клиент, читающий JSON собственным `serverJson` сервера. Подробнее об API сервера — в [`server/README.md`](../../../server/README.ru.md).
