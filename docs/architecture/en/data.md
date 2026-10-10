# Data

[Русская версия](../ru/data.md) · [All pages](README.md)

A ViewModel calls use cases, a use case calls a repository, and a repository talks to Room, DataStore or the server. Which of these are shared and which belong to a single feature follows the rule from [Modules](modules.md#where-new-code-goes).

&nbsp;

## Layers

Shared models and their storage live in `shared/`; anything only one feature needs stays in that feature:

| Where | What |
|---|---|
| `:shared:domain` | models, repository interfaces and use cases used by two or more features; pure Kotlin, no Android |
| `:shared:data` | their implementations: Room, DataStore, the cart API; everything is `internal` |
| `feature/<name>/impl/…/domain`, `…/data` | a feature's own models, repositories and use cases |

&nbsp;

## Use cases

A use case performs one action through a repository:

```kotlin
class AddToCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Result<Unit> =
        cartRepository.addItem(product, quantity)
}
```

Business rules can live in use cases as well as in models. For example, the `Cart` model computes its own totals:

```kotlin
data class Cart(val items: List<CartItem>, val promoCode: PromoCode? = null) {
    fun subtotal(): Long = items.sumOf { it.price * it.quantity }
    fun discount(): Long = promoCode?.discountFor(subtotal()) ?: 0
    fun totalPrice(): Long = subtotal() - discount()
}
```

In the same way, `CartValidation` knows what the last cart check found and what hasn't been fixed yet, and `canAddOneMore` knows the stock limit.

&nbsp;

## Repositories

A repository interface uses only domain models. Room entities and server DTOs stay inside `data` and are converted with `toDomain()` / `toEntity()`.

A repository with a single source works with it directly: `ProductRepositoryImpl` calls `ProductApi`, `AppSettingsRepositoryImpl` calls DataStore.

The cart has two sources, and each one is a separate class:

```kotlin
internal class CartRepositoryImpl @Inject constructor(
    private val localCart: LocalCartDataSource,      // Room: transactions, merging a product already in the cart
    private val validator: CartValidatorDataSource,  // the server check
) : CartRepository
```

The repository combines them and works only with domain models, while the lengthy Room logic stays separate from the network code. The sources have no interfaces, since each has only one implementation.

&nbsp;

## Results and errors

Every failure has an explicit type. An operation that can only succeed or fail returns `kotlin.Result`. An operation with other outcomes that aren't errors has its own sealed type:

```kotlin
sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult   // a link to a product that doesn't exist: retrying won't help
    data class Error(val error: Throwable) : ProductLoadResult
}
```

`PromoCodeCheckResult` and `CartValidationResult` are built the same way.

Two wrappers convert exceptions into these results. [`networkCall`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkResult.kt) returns a `NetworkResult`, and the repository converts it into the result of its own operation:

```kotlin
when (val result = networkCall { api.getProduct(productId) }) {
    is NetworkResult.Success -> ProductLoadResult.Success(result.body.toDomain())
    is NetworkResult.HttpError ->
        if (result.code == HTTP_NOT_FOUND) ProductLoadResult.NotFound else ProductLoadResult.Error(result.error)
    is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
}
```

[`databaseCall`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/database/DatabaseCall.kt) catches only SQLite errors. A failed `require` is a bug, so the app crashes instead of disguising it as a database error:

```kotlin
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Logger.e(e) { "Database operation failed" }
    Result.failure(e)
}
```

> [!NOTE]
> A broad exception type is never taken to mean something it might not be: an `IOException` doesn't necessarily mean "no internet", because file operations throw it too.

&nbsp;

## Local storage

The cart and the settings are stored on the device:

| Storage | Holds |
|---|---|
| Room (`shopping.db`) | cart items and the applied promo code; the schema is exported to `shared/data/schemas/` |
| DataStore Preferences | the theme and the request delay from the settings |

The app hasn't been released yet, so schema changes don't need migrations for now:

- **Every schema change bumps `version`** in `ShoppingDatabase`. Room recreates the database only when the version changes: with the old version and a new schema, it fails with an error.
- **A new version recreates the database** in every build, debug and release alike: the cart and the promo code are lost, but the app keeps working. This matters because the release APK from the README is also installed over older versions.

&nbsp;

## Network

Retrofit and Coil's image loading share one `OkHttpClient`, with the same connections and timeouts. Interceptors are provided to it by Hilt as a set, so `:shared:data` can add the request delay without `:core:network` knowing about it:

```kotlin
@Binds
@IntoSet
@ApplicationInterceptor
abstract fun bindNetworkDelayInterceptor(impl: NetworkDelayInterceptor): Interceptor
```

- **The request delay** from the settings is applied before every server request, including images, to show how screens behave while they wait.
- **The HTTP log** is added only in debug.
- **Plain HTTP is allowed only for the server's address** (`network_security_config.xml`): the server has no domain, so it can't use HTTPS.

&nbsp;

## Prices

Every price is in US dollars, stored as a `Long` number of cents: `14999` is $149.99. The server's JSON, the DTOs, Room and the domain models all use the same numbers. Prices are displayed only through [`formatPrice`](../../../shared/ui/src/main/java/krio/systemdesign/shoppingapp/shared/ui/text/PriceFormat.kt), always in US format, whatever the phone's language:

```kotlin
fun formatPrice(amountCents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(BigDecimal.valueOf(amountCents, 2))
```

There is no `Money` value class. It could only live in the domain: `:shared:ui` takes plain values, and a domain type in UI state would be unstable for Compose. Since most of the code that deals with prices is UI code, such a type wouldn't protect much. It is worth adding once a second currency or taxes appear.

&nbsp;

## The contract with the server

The server's API and data rules are described in [`server/README.md`](../../../server/README.md). Here is what the app relies on:

- **The DTOs are copies of the server's**, each with a comment pointing to its counterpart. The JSON samples in [`server/api-samples/`](../../../server/api-samples/) keep the two sides in sync, and [tests on both sides](testing.md#contract-with-the-server) check against them.
- **Paging by page number is safe**: the server never removes products and always adds new ones at the end. A renamed product can still move around in search results, so `ProductPagingSource` drops duplicate ids.
- **The server decides whether checkout can open.** The cart shows what its last check found, but "Checkout" opens only after a fresh `Success` from the server.
- **A promo code's discount percentage never changes**, so the server only checks that the code still exists.
- **The test items in the settings rely on four products** keeping their stock and prices; a server test makes sure they do.
