# Данные

> **Вариант `kmp`**: Android и iOS, Metro, Ktor
>
> Эта страница в других вариантах:
> - [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/ru/data.md): Android, Hilt, Retrofit
> - [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/ru/data.md): Android, Metro, Ktor

[English version](../en/data.md) · [Все разделы](README.md)

ViewModel обращается к use case'ам, use case — к репозиторию, а репозиторий — к Room, DataStore или серверу. Что из этого общее, а что принадлежит одной фиче, определяет правило из раздела [Модули](modules.md#куда-класть-новый-код).

&nbsp;

## Слои

Общие модели и всё, что связано с их хранением, лежат в `crossfeature/`, а то, что нужно только одной фиче, остаётся в ней:

| Где | Что |
|---|---|
| `:crossfeature:domain` | модели, интерфейсы репозиториев и use case'ы, нужные двум фичам и больше; чистый Kotlin без Android |
| `:crossfeature:data` | их реализации: Room, DataStore, API корзины; всё объявлено `internal` |
| `feature/<name>/impl/…/domain`, `…/data` | собственные модели, репозитории и use case'ы фичи |

&nbsp;

## Use case'ы

Use case выполняет одно действие через репозиторий:

```kotlin
@Inject
class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Result<Unit> =
        cartRepository.addItem(product, quantity)
}
```

Бизнес-правила могут жить как в use case'ах, так и в моделях. Например, итоги корзины считает сама модель `Cart`:

```kotlin
data class Cart(val items: List<CartItem>, val promoCode: PromoCode? = null) {
    fun subtotal(): Long = items.sumOf { it.price * it.quantity }
    fun discount(): Long = promoCode?.discountFor(subtotal()) ?: 0
    fun totalPrice(): Long = subtotal() - discount()
}
```

Точно так же `CartValidation` знает, что нашла последняя проверка корзины и что ещё не исправлено, а `canAddOneMore` — сколько товара осталось на складе.

&nbsp;

## Репозитории

Интерфейс репозитория работает только с доменными моделями. Entity Room и DTO сервера остаются внутри `data` и преобразуются через `toDomain()` / `toEntity()`; сами DTO берутся из [контракта](#договорённости-с-сервером).

Репозиторий с одним источником работает с ним напрямую: `ProductRepositoryImpl` вызывает `ProductApi`, `AppSettingsRepositoryImpl` — DataStore.

У корзины два источника, и каждый из них — отдельный класс:

```kotlin
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)  // граф приложения получает его как CartRepository
internal class CartRepositoryImpl(
    private val localCart: LocalCartDataSource,      // Room: транзакции, слияние товара, который уже есть
    private val validator: CartValidatorDataSource,  // проверка на сервере
) : CartRepository
```

Репозиторий объединяет их и работает только с доменными моделями, а объёмная логика Room живёт отдельно от сетевого кода. Интерфейсов у источников нет, потому что у каждого всего одна реализация.

&nbsp;

## Результаты и ошибки

У каждой ошибки есть явный тип. Операция, которая может только успешно завершиться или упасть, возвращает `kotlin.Result`. Если у операции бывают и другие исходы, которые ошибками не являются, у неё свой sealed-тип:

```kotlin
sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult   // ссылка на товар, которого нет: повтор не поможет
    data class Error(val error: Throwable) : ProductLoadResult
}
```

Так же устроены `PromoCodeCheckResult` и `CartValidationResult`.

Превращать исключения в такие результаты помогают две обёртки. [`networkCall`](../../../core/network/src/commonMain/kotlin/krio/systemdesign/shoppingapp/core/network/NetworkResult.kt) возвращает `NetworkResult`, а репозиторий превращает его в результат своей операции:

```kotlin
when (val result = networkCall { api.getProduct(productId) }) {
    is NetworkResult.Success -> ProductLoadResult.Success(result.body.toDomain())
    is NetworkResult.HttpError ->
        if (result.code == HttpStatusCode.NotFound.value) ProductLoadResult.NotFound else ProductLoadResult.Error(result.error)
    is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
}
```

[`databaseCall`](../../../crossfeature/data/src/commonMain/kotlin/krio/systemdesign/shoppingapp/crossfeature/data/database/DatabaseCall.kt) ловит только ошибки SQLite. Сработавший `require` — это баг, поэтому приложение падает, а не выдаёт его за ошибку базы данных:

```kotlin
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Logger.e(e) { "Database operation failed" }
    Result.failure(e)
}
```

> [!NOTE]
> Широкий тип исключения никогда не толкуется как что-то, чем он может и не быть: `IOException` — это не обязательно «нет интернета», его выбрасывают и операции с файлами.

&nbsp;

## Локальное хранение

Локально хранятся корзина и настройки:

| Хранилище | Что в нём |
|---|---|
| Room (`shopping.db`) | товары корзины и применённый промокод; схема экспортируется в `crossfeature/data/schemas/` |
| DataStore Preferences | тема и задержка запросов из настроек |

Оба хранилища на Kotlin Multiplatform, и код, который с ними работает, общий; разное только расположение файлов:

- **Android** хранит файлы там же, где всегда (`AndroidDataModule`): базу — среди баз приложения, настройки — в `files/datastore/`. Обновление приложения сохраняет корзину и настройки.
- **iOS** хранит их в папке Documents приложения (`IosDataModule`).
- **SQLite у приложения своя** (`BundledSQLiteDriver`), одной версии на обеих платформах, а не системная. Она добавляет к APK около 1,2 МБ на каждый тип процессора; приложение хранит эти библиотеки сжатыми, поэтому универсальный APK вырастает примерно на 2,5 МБ.
- **Транзакции** идут через `writeTransaction` — мультиплатформенный аналог `withTransaction` из Room, который есть только на Android.

Приложение ещё не выпущено, поэтому миграции при изменении схемы пока не нужны:

- **Каждое изменение схемы повышает `version`** в `ShoppingDatabase`. Room пересоздаёт базу только при смене версии: если версия старая, а схема новая, он падает с ошибкой.
- **Новая версия пересоздаёт базу** в любой сборке, и в debug, и в release: корзина и промокод теряются, но приложение продолжает работать. Это важно, потому что release APK из README тоже устанавливают поверх старых версий.

&nbsp;

## Сеть

API-классы и загрузка картинок через Coil используют один `HttpClient` из Ktor с общими соединениями и таймаутами. Другие модули дополняют его через `HttpClientSetup`, который Metro собирает в набор, поэтому `:crossfeature:data` может добавить задержку запросов, а `:core:network` о ней ничего не знает:

```kotlin
@ContributesIntoSet(AppScope::class)
internal class NetworkDelayPlugin(private val appSettingsRepository: AppSettingsRepository) : HttpClientSetup
```

- **Задержка запросов** из настроек срабатывает перед каждым запросом к серверу, в том числе за картинками, чтобы показать, как экраны ведут себя, пока ждут ответа. Это плагин Ktor, который приостанавливает запрос, поэтому уход с экрана сразу отменяет ожидание.
- **Ответы 4xx и 5xx** выбрасывают `ResponseException` из Ktor, а `networkCall` превращает его в `NetworkResult.HttpError`.
- **HTTP-лог** добавляется только в debug.
- **Обычный HTTP разрешён только для адреса сервера** (`network_security_config.xml`): у сервера нет домена, а значит, нет и HTTPS.

&nbsp;

## Цены

Все цены указаны в долларах США и хранятся как число центов типа `Long`: `14999` — это $149.99. Те же числа используются в JSON сервера, DTO, Room и доменных моделях. Показываются цены только через [`formatPrice`](../../../crossfeature/ui/src/commonMain/kotlin/krio/systemdesign/shoppingapp/crossfeature/ui/text/PriceFormat.kt) и всегда в американском формате, на каком бы языке ни работал телефон:

```kotlin
fun formatPrice(amountCents: Long): String {
    val sign = if (amountCents < 0) "-" else ""
    val cents = amountCents.absoluteValue
    val dollars = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val remainder = (cents % 100).toString().padStart(2, '0')
    return "$sign$$dollars.$remainder"
}
```
Форматер написан вручную: форматы чисел платформ недоступны в общем коде, а приложению нужен только этот один формат.


Value class `Money` в проекте нет. Он мог бы жить только в домене: `:crossfeature:ui` принимает простые значения, а доменный тип в UI state был бы нестабилен для Compose. Большая часть кода, который работает с ценами, — это UI, так что такой тип защищал бы немногое. Его стоит ввести, когда появится вторая валюта или налоги.

&nbsp;

## Договорённости с сервером

API сервера и правила для его данных описаны в [`server/README.ru.md`](../../../server/README.ru.md). Приложение рассчитывает на следующее:

- **Приложение и сервер используют одни и те же DTO** из [`contract/`](../../../contract/): это сборка на Kotlin Multiplatform (JVM и iOS), которую подключают обе стороны, поэтому поле, изменённое на одной стороне, не скомпилируется на другой, пока его не поменяют и там. Маперы в доменные модели остаются в пакетах `data` приложения. Образцы JSON в [`server/api-samples/`](../../../server/api-samples/) по-прежнему проверяют то, чего не видят типы: адреса запросов и настройки `Json` каждой стороны ([тесты на обеих сторонах](testing.md#контракт-с-сервером)).
- **Постраничная загрузка по номеру страницы безопасна**: сервер никогда не удаляет товары и всегда добавляет новые в конец. Переименованный товар всё же может сдвинуться в результатах поиска, поэтому `ProductPagingSource` отбрасывает повторяющиеся id.
- **Можно ли перейти к оформлению, решает сервер.** Корзина показывает, что нашла последняя проверка, но кнопка «Оформить» открывает оформление только после свежего `Success` от сервера.
- **Процент скидки промокода никогда не меняется**, поэтому сервер проверяет только, что код ещё существует.
- **Тестовые пункты в настройках рассчитаны на четыре товара**, у которых не меняются остаток и цена; за этим следит тест сервера.
