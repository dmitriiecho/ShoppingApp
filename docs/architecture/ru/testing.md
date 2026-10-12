# Тесты

[English version](../en/testing.md) · [Все разделы](README.md)

Тесты запускаются на JVM, без эмулятора. Они проверяют логику приложения, ViewModel, слой данных и экраны (скриншот каждого превью), контракт с сервером и сам сервер. Приложение и сервер следуют одним и тем же правилам, поэтому тесты с обеих сторон читаются одинаково.

&nbsp;

## Запуск тестов

Каждая сборка запускает свои тесты, а CI запускает оба набора на каждом pull request:

```sh
./gradlew test testAndroidHostTest   # приложение на JVM: все модули, по одному разу, включая скриншоты
./gradlew iosSimulatorArm64Test      # общие тесты на iOS-симуляторе (на Mac; CI запускает их на macOS)
cd server && ./gradlew test          # сервер
./gradlew recordRoborazziDebug recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true  # записывает новые скриншоты после изменения интерфейса
```

- **Android-модули** запускают unit-тесты задачей `test` и только в debug-варианте: `shoppingapp.android.library` отключает release-тесты, которые просто повторяли бы их.
- **У модулей на Kotlin Multiplatform задачи `test` нет.** Их тесты в `src/commonTest` идут на обеих платформах: на JVM задачей `testAndroidHostTest`, на iOS-симуляторе задачей `iosSimulatorArm64Test`. Тесты, которым нужны классы Android (Robolectric), лежат в `src/androidHostTest` и идут только на JVM.
- **iOS-тесты не видят ресурсов Compose.** Compose Multiplatform копировал бы их рядом с тестовой программой для iOS, но из-за этой копии Gradle не может кешировать саму программу, поэтому сборка копирование отключает ([`KmpComposeConventionPlugin.kt`](../../../build-logic/src/main/kotlin/KmpComposeConventionPlugin.kt)). Общие тесты сравнивают `StringResource`, а не читают их, а скриншоты, на которых видны тексты, идут на Android. Модуль, чьи iOS-тесты всё же читают ресурсы, включает задачи `copyTestComposeResourcesFor*` обратно.
- **Имена тестов в `src/commonTest`** не могут содержать запятые, точки, двоеточия и точки с запятой: Kotlin/Native не принимает их в имени функции.

&nbsp;

## Как пишется тест

Вот короткий тест из проекта:

```kotlin
@Test
fun `discount is the promo code percent of the subtotal`() {
    val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

    assertThat(cart.discount()).isEqualTo(200)
}
```

- **Один тест — одно поведение.** Несколько независимых проверок в одном тесте заслоняют друг друга: первая упавшая не даёт выполниться остальным. Однотипные случаи собираются в одну таблицу `tableOf` из AssertK: она проверяет каждую строку и показывает все упавшие. Таблица не умеет вызывать suspend-функции, поэтому случаи, которые отправляются на сервер, сначала отправляются все, а потом проверяются вместе: `assertThat(statuses.filterValues { it != BadRequest }).isEmpty()` перечисляет все неверные ответы.
- **В имени теста — условие и ожидаемый результат**, в обратных кавычках: `` `cart with a changed price reports the new price` ``, а не `` `price changed` ``.
- **Подготовка, действие, проверка** отделяются друг от друга пустой строкой.
- **Данные теста лежат в самом тесте.** Построители со значениями по умолчанию (`testCartItem(price = 2000)`) позволяют задать только те поля, которые важны для теста. Общего набора данных на все тесты нет: любая его правка ломала бы чужие тесты.
- **Работающие подмены вместо моков.** Репозитории — это интерфейсы, поэтому тест использует реализацию в памяти с настоящим поведением; библиотеки для моков в проекте нет.
- **Никакого реального времени**: тесты корутин работают в `runTest` с его виртуальными часами.
- **Каждый новый тест хотя бы раз должен упасть**: ломаем код, который он проверяет, убеждаемся, что тест падает, и возвращаем код обратно. Тест, который ни разу не падал, может вообще ничего не проверять.
- **Настоящие файлы данных проверяются отдельно от поведения** — по правилу («у каждой картинки каталога есть файл»), а не по количеству.

Комментарии пишутся по [стилю кода](build.md#стиль-кода): только там, где сам код не может объяснить «почему».

&nbsp;

## Имена и расположение

Имена всего, что сделано для тестов, начинаются с `Test` или `test`, как в Now in Android. Префикс `Fake` уже занят: `FakeInsightsClient` и `FakeAdTrackerClient` в `:apps:shop` — это рабочий код, заглушки систем аналитики.

| Что | Имя | Пример |
|---|---|---|
| Тестовый класс | проверяемый файл + `Test`, в том же пакете | `Cart.kt` → `CartTest` |
| Подменная реализация | `Test` + что подменяет | `TestAnalyticsClient` |
| Построитель данных | `test` + модель | `testCartItem()`, `testCart()` |
| Функция-вход вида тестов | вид + `Test` | `networkTest {}`, `serverTest {}` |
| Действие в тесте | глагол | `typeText()`, `keepCollecting()` |

Из первой строки есть одно исключение: проверка файлов `data/` сервера называется `DataFilesTest`, потому что эти файлы — данные, а не код.

Помощник, который нужен тестам **двух модулей и больше**, лежит рядом с модулем, которому принадлежит то, что он подменяет: в его модуле `testing` (у модулей на Kotlin Multiplatform нет test fixtures) или в его **test fixtures** (у Android-модулей). Помощник, нужный одному модулю, лежит в его тестах. Это то же правило, что и для [кода и компонентов](modules.md#куда-класть-новый-код):

| Помощник | Модуль | Source set |
|---|---|---|
| `testProduct()`, `testCartItem()`, `testCart()` | `:crossfeature:domain:testing` | `src/commonMain` |
| `TestCartRepository` | `:crossfeature:domain:testing` | `src/commonMain` |
| `TestAnalyticsClient`, `TestAnalytics` | `:crossfeature:analytics:testing` | `src/commonMain` |
| `viewModelTest {}`, `keepCollecting()`, `typeText()` | `:core:compose-utils:testing` | `src/commonMain` |
| `PausedClockPreviewTester` | `:core:compose-utils:testing` | `src/androidMain` |
| `networkTest {}`, `TestServer`, `apiSample()`, `apiRequest()` | `:core:network:testing` | `src/commonMain` |
| `databaseTest {}` | `:crossfeature:data` | `src/androidHostTest` |
| `TestProductRepository` | `:feature:catalog:impl` | `src/test` |
| `TestPromoCodeRepository` | `:feature:promo:impl` | `src/commonTest` |
| `serverTest {}`, `testShopData()`, `testProduct()`, `apiSample()`, `sendApiRequest()` | `server/` | `src/test` |

Модуль подключает чужих помощников через `testImplementation(project(":crossfeature:domain:testing"))` (в KMP-модуле — `commonTest.dependencies { implementation(…) }`) или через `testImplementation(testFixtures(project(…)))` для test fixtures Android-модуля. Помощники видят только публичный API своего модуля. [Проверка графа модулей](modules.md#проверка) смотрит только на `api` и `implementation`, так что тестовые зависимости соблюдают уровни по договорённости, а не под контролем проверки.

&nbsp;

## Инструменты

Все библиотеки работают в Kotlin Multiplatform, кроме тех, что нужны для кода только под Android:

| Инструмент | Для чего |
|---|---|
| `kotlin.test` | `@Test` и другие аннотации; тесты не импортируют `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | все проверки: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` сужает тип, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` и виртуальные часы |
| [Turbine](https://github.com/cashapp/turbine) | разовые эффекты экрана: `effects.test { awaitItem() }` |
| `MockEngine` из Ktor | отвечает на запросы сетевого кода без настоящего сервера (`TestServer`) |
| [Robolectric](https://robolectric.org) | классы Android на JVM, только для кода, который без них не работает (Room, скриншоты) |
| [Roborazzi](https://github.com/takahirom/roborazzi) | скриншот-тесты, созданные из функций `@Preview` |

Раннер скрыт за `kotlin.test`, поэтому тесты выглядят одинаково, хотя раннеры разные: JUnit4 в приложении на JVM (Robolectric работает только с ним), собственный раннер Kotlin/Native на iOS и JUnit5 на сервере.

&nbsp;

## Виды тестов

Каждый вид тестов начинается с функции-входа, которая готовит всё, что нужно этому виду тестов.

### ViewModel

`viewModelTest {}` подменяет `Dispatchers.Main`, на котором работает `viewModelScope`, тестовым диспетчером на виртуальных часах `runTest`. ViewModel получает настоящие use case'ы, которые работают с `Test`-репозиториями, поэтому тест задаёт данные и ответ сервера и проверяет, что в итоге получает экран:

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
- **Подписка на состояние держится всё время, как на экране**: `keepCollecting(viewModel.uiState)` вызывается сразу после создания ViewModel, а дальше тест читает `viewModel.uiState.value`. Поток `stateIn(WhileSubscribed)` без подписчика так и остаётся с начальным значением. Эффекты разовые, поэтому их проверяют через Turbine.
- **Список Paging всё время «показан», как на экране**: Paging загружает страницы, только пока их кто-то показывает, поэтому тест каталога держит `PagingDataPresenter`, который собирает `viewModel.products`, и проверяет, какие страницы запрошены у репозитория. `asSnapshot()` не подходит из-за задержки поиска: он сам двигает виртуальные часы, пока всё не закончится.
- **Ввод текста — через `typeText()`**: на устройстве Compose применяет изменение поля на следующем кадре, а в тесте кадров нет.
- **Время виртуальное**: `advanceTimeBy()` проматывает debounce или анимацию без реального ожидания.
- **Сохранение на случай смерти процесса** проверяется, только если сохраняется обычное значение в `SavedStateHandle` (открытый диалог, способ оплаты): тот же handle передаётся новой ViewModel. Поля ввода сохраняются через Android `Bundle`, которого в JVM-тесте нет.
- **Запрос «в процессе»** изображает `CompletableDeferred`, которого ждёт ответ: тем временем тест меняет корзину, а потом завершает запрос.
- **У событий аналитики нет `equals`**, поэтому `TestAnalytics.sentEvents` хранит их имена и параметры: `assertThat(analytics.sentEvents).containsExactly(CheckoutStartedAnalyticsEvent(itemCount = 3, totalCents = 5000).sent())`.

### База данных

`databaseTest {}` создаёт пустую `ShoppingDatabase` в памяти. На Android Room нужен `Context`, поэтому такие тестовые классы лежат в `src/androidHostTest` и запускаются на Robolectric — это единственное место, где тест импортирует `org.junit`. Они работают на SQLite из Android: встроенная SQLite приложения — нативная библиотека для устройств и на JVM не загружается.

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

Robolectric работает на `targetSdk` приложения: `shoppingapp.android.library` и `shoppingapp.kmp.library` передают тестам каждого library-модуля этот SDK, его ресурсы и флаги JVM, которые нужны Robolectric на Java 17 и новее. Без них он взял бы свой самый старый SDK, который старше `minSdk` приложения. DataStore не нужен `Context`, поэтому настройки проверяются на настоящем DataStore во временном файле, без Robolectric.

### Сеть

`networkTest {}` даёт тесту `TestServer` и API-класс на клиенте с настройками приложения ([`serverApi`](../../../core/network/src/commonMain/kotlin/krio/systemdesign/shoppingapp/core/network/NetworkJson.kt)), поэтому JSON читается тем же `networkJson`. `TestServer` отвечает ответами из очереди и записывает запросы; он работает на `MockEngine` из Ktor, без настоящего HTTP-сервера:

```kotlin
@Test
fun `client error returns HttpError with its code`() = networkTest(::TestApi) { server, api ->
    server.enqueue(code = 404)

    val result = networkCall { api.item() }

    assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
}
```

### Контракт с сервером

Приложение и сервер используют общие DTO API ([`contract/`](../../../contract/)), так что типы разойтись не могут. То, чего типы не покрывают, — адреса запросов и настройки `Json` каждой стороны — сверяется с папкой [`server/api-samples/`](../../../server/api-samples/): в ней по одному JSON-файлу на каждый запрос и ответ, на которые опирается приложение. `apiSample("product.json")` читает такой файл на любой из сторон и разбирает его собственным `Json` этой стороны, а проверка сравнивает деревья JSON: порядок полей не важен, а вот переименованное, лишнее или пропавшее поле — важно. Куда отправляется каждый запрос, записано в `requests.json` в виде метода и адреса: `"product": "GET /products/1"` — это запрос, ответом на который служит `product.json`.

| Сторона | Что проверяет |
|---|---|
| Сервер | принимает образцы запросов по их адресам и отвечает в точности так, как в образцах ответов |
| Приложение | отправляет запросы в точности как в образцах, по их адресам, и разбирает образцы ответов в ожидаемые модели |

Если на одной стороне поменять формат или адрес, тесты этой стороны будут падать, пока не обновят образец, а затем и другую сторону. Сборка объявляет папку входными данными каждой тестовой задачи, поэтому после правки образца тесты запускаются заново. У iOS-тестов нет системных свойств, поэтому `:core:network:testing` получает путь к папке константой, которую создаёт сборка, а образцы на обеих платформах читаются через kotlinx-io.

### Скриншоты

Каждый `@Preview` — это скриншот-тест, так что писать такие тесты вручную не нужно. Плагин `shoppingapp.android.screenshots`, который подключает каждая библиотека с превью (Android или KMP), поручает [Roborazzi](https://github.com/takahirom/roborazzi) отрисовать каждое превью на Robolectric и сравнить результат с картинкой, сохранённой в папке модуля `screenshots/`:

| Команда | Что делает |
|---|---|
| `./gradlew test testAndroidHostTest` | сравнивает каждое превью с сохранённой картинкой и падает, если они различаются (`test` в Android-модулях, `testAndroidHostTest` в KMP-модулях) |
| `./gradlew recordRoborazziDebug recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true` | отрисовывает превью и сохраняет их как новые картинки; удаляет картинки превью, которых больше нет |

Эти тесты не говорят, правильно ли выглядит экран, — они ловят изменения, которых никто не заказывал. Поэтому картинки записывает только человек, сознательно и только после изменения интерфейса:

1. Изменить интерфейс.
2. Убедиться, что изменилось только то, что задумано, одним из двух способов:
   - **сначала тесты**: запустить `./gradlew test testAndroidHostTest`. Должны упасть только те превью, которые и должны были измениться, а на `build/outputs/roborazzi/…_compare.png` каждого из них красным должно быть отмечено только задуманное. Там виден даже сдвиг на пиксель-два, который на двух картинках рядом не заметить;
   - **сначала запись**: записать картинки (шаг 3), а потом посмотреть, какие из них изменились (`git status` или сами картинки в IDE). Так быстрее — один прогон вместо двух, — но старая картинка уже перезаписана, и картинки сравнения не будет.

   В обоих случаях тридцать изменившихся картинок после правки одной кнопки — повод разобраться, что пошло не так.
3. Записать картинки, если это ещё не сделано: `./gradlew recordRoborazziDebug recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true` или через конфигурацию запуска Record screenshots в Android Studio.
4. Закоммитить картинки вместе с кодом; в pull request старая и новая версия каждой картинки будут показаны рядом.

> [!IMPORTANT]
> Если вид приложения не должен был меняться, записывать картинки не нужно. В этом случае упавший скриншот-тест нашёл настоящую проблему: отступ, съехавший при рефакторинге, обновление библиотеки, которое поменяло кнопки, общий компонент, задевший другой экран. CI картинки никогда не записывает.

- **Изменившийся экран — это упавший тест**, пока его новые картинки не записаны и не закоммичены.
- **Флаг очистки — только с задачами записи.** Roborazzi подключается и к iOS-задачам тестов KMP-модулей, а они скриншотов не снимают: с этим флагом они удалили бы все сохранённые картинки модуля.
- **Упавший тест оставляет картинку сравнения** в папке сборки: в `build/outputs/roborazzi/*_compare.png` — сохранённая картинка, разница, выделенная красным, и новая картинка. В CI такие картинки прикладываются к прогону как `changed-screenshots` и показываются в комментарии к pull request.
- **Часы остановлены**: `PausedClockPreviewTester` отрисовывает каждое превью на первом кадре, поэтому бесконечная анимация (шиммер, индикатор загрузки) не подвешивает тест, а каждый прогон даёт одну и ту же картинку.
- **Старые картинки удаляются только при записи** — за это отвечает тот самый флаг: Roborazzi удаляет все картинки, которые прогон не отрисовал, поэтому, если бы флаг был включён всегда, запуск одного теста из Android Studio удалил бы остальные картинки модуля.
- **Диалог отрисовывается на весь экран** вместе с затемнённым фоном, поэтому у диалога есть превью, как у любого другого компонента. Android Studio отрисовывает только основное окно, поэтому там такое превью пустое; Run Preview показывает его на устройстве.
- **Приватные превью тоже проверяются**: правила lint от Slack требуют, чтобы превью были приватными, и плагин их учитывает.
- **У приложения UI kit собственных скриншотов нет**: его разделы показывают те же компоненты, которые уже проверяются скриншотами в их модулях.

Compose Preview Screenshot Testing от Google делает то же самое с помощью рендерера Android Studio, но пока он в alpha, поэтому до выхода стабильной версии в проекте используется Roborazzi.

### Сервер

`serverTest {}` запускает сервер в памяти через `testApplication` из Ktor на тех данных и картинках товаров, которые передаёт тест (`testShopData(products = …)`, `images = mapOf("1.png" to …)`), и создаёт клиент, который читает JSON собственным `serverJson` сервера. Картинки лежат во временной папке, которая удаляется после теста. Запрос из образцов отправляет `sendApiRequest("product")`, и любой ответ, кроме успешного, роняет тест. Подробнее об API сервера — в [`server/README.ru.md`](../../../server/README.ru.md).
