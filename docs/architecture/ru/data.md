# Данные

[English version](../en/data.md) · [Все разделы](README.md)

ViewModel обращается к use case'ам, use case — к репозиторию, репозиторий — к Room, DataStore или серверу. Что из этого общее, а что принадлежит одной фиче, решает правило из [Модулей](modules.md#куда-класть-новый-код).

&nbsp;

## Слои

Общие модели и их хранение лежат в `shared/`, а то, что нужно одной фиче, остаётся в ней:

| Где | Что |
|---|---|
| `:shared:domain` | модели, интерфейсы репозиториев и use case'ы, нужные двум фичам и больше; чистый Kotlin без Android |
| `:shared:data` | их реализации: Room, DataStore, API корзины; всё `internal` |
| `feature/<name>/impl/…/domain`, `…/data` | собственные модели, репозитории и use case'ы фичи: товары каталога, проверка промокода |

&nbsp;

## Use case'ы

Use case — одно действие через репозиторий. Многие из них в одну строку, но они есть для каждого действия, чтобы ViewModel никогда не зависела от репозитория:

```kotlin
class AddToCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Result<Unit> =
        cartRepository.addItem(product, quantity)
}
```

Правила живут в моделях, а не в use case'ах. Например, итоги корзины считает сама `Cart`:

```kotlin
data class Cart(val items: List<CartItem>, val promoCode: PromoCode? = null) {
    fun subtotal(): Long = items.sumOf { it.price * it.quantity }
    fun discount(): Long = promoCode?.discountFor(subtotal()) ?: 0
    fun totalPrice(): Long = subtotal() - discount()
}
```

Так же что нашла последняя проверка корзины и что ещё не исправлено, знает `CartValidation`, а ограничение по остатку — `canAddOneMore`.

Если имя обещает больше, чем делает код, к нему есть комментарий:

```kotlin
// Orders aren't sent anywhere: the server has no endpoint for them.
// Placing one only resets the cart (items and promo code).
suspend operator fun invoke(): Result<Unit> = cartRepository.reset()
```

&nbsp;

## Репозитории

Интерфейс репозитория говорит только на доменных моделях. Entity Room и DTO сервера остаются внутри `data` и переводятся через `toDomain()` / `toEntity()`.

Репозиторий с одним источником работает с ним напрямую: `ProductRepositoryImpl` вызывает `ProductApi`, `AppSettingsRepositoryImpl` — DataStore.

У корзины источника два, и каждый — отдельный класс:

```kotlin
internal class CartRepositoryImpl @Inject constructor(
    private val localCart: LocalCartDataSource,      // Room: транзакции, слияние товара, который уже есть
    private val validator: CartValidatorDataSource,  // проверка на сервере
) : CartRepository
```

Репозиторий соединяет их и видит только доменные модели, а длинная логика Room живёт отдельно от сетевого кода. Интерфейсов у источников нет: у каждого одна реализация.

&nbsp;

## Результаты и ошибки

У каждой ошибки явный тип. Операция, у которой есть только успех и неудача, возвращает `kotlin.Result`. Если у операции есть исходы, которые не ошибки, у неё свой sealed-тип:

```kotlin
sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult   // ссылка на товар, которого нет: повтор не поможет
    data class Error(val error: Throwable) : ProductLoadResult
}
```

Так же устроены `PromoCodeCheckResult` и `CartValidationResult`.

Исключения в эти результаты превращают две обёртки. [`networkCall`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkResult.kt) возвращает `NetworkResult`, а репозиторий переводит его в результат своей операции:

```kotlin
when (val result = networkCall { api.getProduct(productId) }) {
    is NetworkResult.Success -> ProductLoadResult.Success(result.body.toDomain())
    is NetworkResult.HttpError ->
        if (result.code == HTTP_NOT_FOUND) ProductLoadResult.NotFound else ProductLoadResult.Error(result.error)
    is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
}
```

[`databaseCall`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/database/DatabaseCall.kt) ловит только ошибки SQLite. Сработавший `require` — это баг, и приложение падает, а не выдаёт его за ошибку базы:

```kotlin
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Timber.e(e, "Database operation failed")
    Result.failure(e)
}
```

> [!NOTE]
> Широкий тип исключения не трактуется как то, чем он может не быть: `IOException` — это не «нет интернета», его бросают и файлы.

&nbsp;

## Локальное хранение

Локально хранятся корзина и настройки:

| Хранилище | Что в нём |
|---|---|
| Room (`shopping.db`) | товары корзины и применённый промокод; схема экспортируется в `shared/data/schemas/` |
| DataStore Preferences | тема и задержка запросов из настроек |

Приложение ещё не выпущено, поэтому изменение схемы пока не требует миграции:

- **Каждое изменение схемы повышает `version`** в `ShoppingDatabase`. Room пересоздаёт базу только при смене версии: со старой версией и новой схемой он останавливается с ошибкой.
- **Новая версия пересоздаёт базу** в любой сборке, debug и release одинаково: корзина и промокод теряются, приложение продолжает работать. Release APK из README тоже ставят поверх старых.

&nbsp;

## Сеть

Retrofit и загрузка картинок через Coil используют один `OkHttpClient`: общие соединения и таймауты. Перехватчики приходят в него из Hilt набором, поэтому `:shared:data` добавляет задержку запросов, а `:core:network` о ней не знает:

```kotlin
@Binds
@IntoSet
@ApplicationInterceptor
abstract fun bindNetworkDelayInterceptor(impl: NetworkDelayInterceptor): Interceptor
```

- **Задержка запросов** из настроек ждёт перед каждым запросом к серверу, в том числе к картинкам, чтобы показать, как экраны ведут себя при ожидании.
- **HTTP-лог** добавляется только в debug.
- **Обычный HTTP разрешён только для адреса сервера** (`network_security_config.xml`): у него нет домена, а значит, и HTTPS.

&nbsp;

## Цены

Все цены в долларах США, числом центов типа `Long`: `14999` — это $149.99. Те же числа в JSON сервера, DTO, Room и доменных моделях. Показываются они только через [`formatPrice`](../../../shared/ui/src/main/java/krio/systemdesign/shoppingapp/shared/ui/text/PriceFormat.kt), в формате США при любом языке телефона:

```kotlin
fun formatPrice(amountCents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(BigDecimal.valueOf(amountCents, 2))
```

Value class `Money` нет. Он мог бы жить только в домене: `:shared:ui` принимает простые значения, а доменный тип в UI state был бы нестабилен для Compose. Большая часть кода с ценами — это UI, так что тип защищал бы немногое. Его стоит вводить вместе со второй валютой или налогами.

&nbsp;

## Договорённости с сервером

API и правила данных сервера описаны в [`server/README.ru.md`](../../../server/README.ru.md). Приложение рассчитывает на следующее:

- **DTO — копии серверных**, с комментарием, указывающим на другую сторону. Их согласованность держат образцы JSON в [`server/api-samples/`](../../../server/api-samples/): их проверяют [тесты обеих сторон](testing.md#контракт-с-сервером).
- **Пагинация по номеру страницы безопасна**: сервер никогда не удаляет товары и добавляет новые в конец. Переименованный товар всё же может сдвинуться в результатах поиска, поэтому `ProductPagingSource` отбрасывает повторяющиеся id.
- **Открыть ли оформление, решает сервер.** Корзина показывает, что нашла её последняя проверка, но «Оформить» открывает оформление только после свежего `Success` от сервера.
- **Процент скидки промокода никогда не меняется**, поэтому сервер проверяет только, что код ещё существует.
- **Тестовые пункты настроек рассчитывают на четыре товара**, которые сохраняют свой остаток и цену; их проверяет тест сервера.
